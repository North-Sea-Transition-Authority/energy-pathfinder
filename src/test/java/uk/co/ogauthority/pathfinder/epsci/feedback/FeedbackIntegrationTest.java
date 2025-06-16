package uk.co.ogauthority.pathfinder.epsci.feedback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.ogauthority.pathfinder.epsci.feedback.FeedbackIntegrationTest.FeedbackTestConfig.mockWebServer;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.HttpHeaders;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@TestPropertySource(properties = {
    "pathfinder.url.base = http://test/",
    "context-path = pathfinder"
})
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = EpsciFeedbackController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {EpsciFeedbackService.class, ValidationService.class}))
            //DefaultExceptionResolver and SupportContactService have to be included so
            //Spring knows how to handle CannotSendFeedbackException in some tests
@Import({FeedbackIntegrationTest.FeedbackTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FeedbackIntegrationTest extends AbstractControllerTest {


  @Autowired
  ObjectMapper objectMapper;

  private MockResponse response;

  @Before
  public void setUp() {

    response = new MockResponse()
        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .setResponseCode(200)
        .setBody("{\"feedbackId\": 1}");
  }

  @After
  public void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  public void saveFeedback() throws Exception {

    var rating = "VERY_SATISFIED";
    var comment = "Test comment";
    var epsciPath = "/projects/12";

    mockWebServer.enqueue(response);

    mockMvc.perform(post("/energy-pathfinder-feedback")
            .with(csrf())
            .param("serviceRating", rating)
            .param("feedback", comment)
            .param("epsciPath", epsciPath))
        .andExpect(redirectedUrlTemplate("/energy-pathfinder-feedback/submitted"));

    var observedRequest = mockWebServer.takeRequest();
    assertThat(observedRequest.getMethod()).isEqualTo(HttpMethod.POST.toString());
    assertThat(observedRequest.getPath()).isEqualTo("/api/v1/save-feedback");
    assertThat(observedRequest.getHeaders().get("Content-type")).isEqualTo("application/json");
    assertThat(observedRequest.getHeaders().get("Authorization")).isEqualTo("dev");

    var postedJson = objectMapper.readTree(observedRequest.getBody().readUtf8());
    assertThat(postedJson.get("submitterName").isNull()).isTrue();
    assertThat(postedJson.get("submitterEmail").isNull()).isTrue();
    assertThat(postedJson.get("serviceRating").asText()).isEqualTo(rating);
    assertThat(postedJson.get("comment").asText()).isEqualTo(comment);
    assertThat(postedJson.get("serviceName").asText()).isEqualTo("PATHFINDER_EPSCI");
    assertThat(postedJson.get("transactionId").isNull()).isTrue();
    assertThat(postedJson.get("transactionReference").asText()).isEqualTo(epsciPath);
    assertThat(postedJson.get("transactionLink").isNull()).isTrue();
  }

  @Test
  public void saveFeedback_minimumParams() throws Exception {

    var rating = "VERY_SATISFIED";

    mockWebServer.enqueue(response);

    mockMvc.perform(post("/energy-pathfinder-feedback")
        .with(csrf())
        .param("serviceRating", rating))
      .andExpect(redirectedUrlTemplate("/energy-pathfinder-feedback/submitted"));

    var observedRequest = mockWebServer.takeRequest();
    assertThat(observedRequest.getMethod()).isEqualTo(HttpMethod.POST.toString());
    assertThat(observedRequest.getPath()).isEqualTo("/api/v1/save-feedback");
    assertThat(observedRequest.getHeaders().get("Content-type")).isEqualTo("application/json");
    assertThat(observedRequest.getHeaders().get("Authorization")).isEqualTo("dev");

    var postedJson = objectMapper.readTree(observedRequest.getBody().readUtf8());
    assertThat(postedJson.get("submitterName").isNull()).isTrue();
    assertThat(postedJson.get("submitterEmail").isNull()).isTrue();
    assertThat(postedJson.get("serviceRating").asText()).isEqualTo(rating);
    assertThat(postedJson.get("comment").isNull()).isTrue();
    assertThat(postedJson.get("serviceName").asText()).isEqualTo("PATHFINDER_EPSCI");
    assertThat(postedJson.get("transactionId").isNull()).isTrue();
    assertThat(postedJson.get("transactionReference").isNull()).isTrue();
    assertThat(postedJson.get("transactionLink").isNull()).isTrue();
  }

  @Test
  public void saveFeedback_invalidParameters() throws Exception {
    mockWebServer.enqueue(response);

    mockMvc.perform(post("/energy-pathfinder-feedback")
            .with(csrf())
            .param("serviceRating", ""))
        .andExpect(status().isOk());

    var observedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
    assertThat(observedRequest).isNull();
  }

  @Test
  public void saveFeedback_serverDown_throwCannotSendFeedbackException() throws Exception {
    mockWebServer.shutdown();

    mockMvc.perform(post("/energy-pathfinder-feedback")
            .with(csrf())
            .param("serviceRating", "VERY_GOOD"))
      .andExpect(status().isOk());
  }


  @DynamicPropertySource
  static void dynamicProperties(DynamicPropertyRegistry registry) throws IOException {

    mockWebServer = new MockWebServer();
    mockWebServer.start();

    registry.add("fms.url.base", () -> String.format("http://localhost:%s", mockWebServer.getPort()));
    registry.add("fms.http.connectTimeout", () -> "2");
    registry.add("fms.url.saveFeedback", () -> "/api/v1/save-feedback");
    registry.add("fms.service.name", () -> "PATHFINDER");
    registry.add("fms.auth.presharedKey", () -> "dev");
  }
  @TestConfiguration
  public static class FeedbackTestConfig {

    protected static MockWebServer mockWebServer;

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    public Clock utcClock() {
      return Clock.fixed(Instant.now(), ZoneId.of("UTC"));
    }
  }
}
