package uk.co.ogauthority.pathfinder.epsci.feedback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.ogauthority.pathfinder.epsci.feedback.FeedbackIntegrationTest.FeedbackTestConfig.feedbackMockWebServer;
import static uk.co.ogauthority.pathfinder.epsci.feedback.FeedbackIntegrationTest.FeedbackTestConfig.recaptchaMockWebServer;

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
import uk.co.ogauthority.pathfinder.google.GoogleConfig;
import uk.co.ogauthority.pathfinder.google.recaptcha.RecaptchaService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@TestPropertySource(properties = {
    "pathfinder.url.base = http://test/",
    "context-path = pathfinder",
    "google.recaptcha.site-key=site-key",
    "google.recaptcha.secret-key=secret-key"
})
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = EpsciFeedbackController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {EpsciFeedbackService.class, ValidationService.class, RecaptchaService.class, GoogleConfig.class}))
            //DefaultExceptionResolver and SupportContactService have to be included so
            //Spring knows how to handle CannotSendFeedbackException in some tests
@Import({FeedbackIntegrationTest.FeedbackTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FeedbackIntegrationTest extends AbstractControllerTest {


  @Autowired
  ObjectMapper objectMapper;

  private MockResponse feedbackResponse;
  private MockResponse recaptchaPassedResponse;
  private MockResponse recaptchaFailedResponse;

  @Before
  public void setUp() {

    feedbackResponse = new MockResponse()
        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .setResponseCode(200)
        .setBody("{\"feedbackId\": 1}");

    recaptchaPassedResponse = new MockResponse()
      .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
      .setResponseCode(200)
      .setBody("{\"success\": true}");

    recaptchaFailedResponse = new MockResponse()
      .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
      .setResponseCode(200)
      .setBody("{\"success\": false}");
  }

  @After
  public void tearDown() throws IOException {
    feedbackMockWebServer.shutdown();
    recaptchaMockWebServer.shutdown();

  }

  @Test
  public void saveFeedback_validAndPassedCaptcha() throws Exception {

    var rating = "VERY_SATISFIED";
    var comment = "Test comment";
    var epsciPath = "/projects/12";

    feedbackMockWebServer.enqueue(feedbackResponse);
    recaptchaMockWebServer.enqueue(recaptchaPassedResponse);

    mockMvc.perform(post("/energy-pathfinder-feedback")
            .with(csrf())
            .param("serviceRating", rating)
            .param("feedback", comment)
            .param("epsciPath", epsciPath)
        .param(RecaptchaService.RESPONSE_REQUEST_PARAMETER_NAME, "recaptcha-passed-response"))
        .andExpect(redirectedUrlTemplate("/energy-pathfinder-feedback/submitted"));

    var observedFeedbackRequest = feedbackMockWebServer.takeRequest();
    assertThat(observedFeedbackRequest.getMethod()).isEqualTo(HttpMethod.POST.toString());
    assertThat(observedFeedbackRequest.getPath()).isEqualTo("/api/v1/save-feedback");
    assertThat(observedFeedbackRequest.getHeaders().get("Content-type")).isEqualTo("application/json");
    assertThat(observedFeedbackRequest.getHeaders().get("Authorization")).isEqualTo("dev");

    var postedFeedbackJson = objectMapper.readTree(observedFeedbackRequest.getBody().readUtf8());
    assertThat(postedFeedbackJson.get("submitterName").isNull()).isTrue();
    assertThat(postedFeedbackJson.get("submitterEmail").isNull()).isTrue();
    assertThat(postedFeedbackJson.get("serviceRating").asText()).isEqualTo(rating);
    assertThat(postedFeedbackJson.get("comment").asText()).isEqualTo(comment);
    assertThat(postedFeedbackJson.get("serviceName").asText()).isEqualTo("PATHFINDER_EPSCI");
    assertThat(postedFeedbackJson.get("transactionId").isNull()).isTrue();
    assertThat(postedFeedbackJson.get("transactionReference").asText()).isEqualTo(epsciPath);
    assertThat(postedFeedbackJson.get("transactionLink").isNull()).isTrue();

    var observedRecaptchaRequest = recaptchaMockWebServer.takeRequest();
    assertThat(observedRecaptchaRequest.getMethod()).isEqualTo(HttpMethod.POST.toString());
    assertThat(observedRecaptchaRequest.getPath()).isEqualTo("/verify");
    assertThat(observedRecaptchaRequest.getHeaders().get("Content-type")).isEqualTo("application/x-www-form-urlencoded");

    var postedRecaptchaBody = observedRecaptchaRequest.getBody().readUtf8();
    assertThat(postedRecaptchaBody).contains("secret=secret-key");
    assertThat(postedRecaptchaBody).contains("response=recaptcha-passed-response");

  }

  @Test
  public void saveFeedback_minimumParams() throws Exception {

    var rating = "VERY_SATISFIED";

    feedbackMockWebServer.enqueue(feedbackResponse);
    recaptchaMockWebServer.enqueue(recaptchaPassedResponse);

    mockMvc.perform(post("/energy-pathfinder-feedback")
        .with(csrf())
        .param("serviceRating", rating)
        .param(RecaptchaService.RESPONSE_REQUEST_PARAMETER_NAME, "recaptcha-passed-response"))
      .andExpect(redirectedUrlTemplate("/energy-pathfinder-feedback/submitted"));

    var observedRequest = feedbackMockWebServer.takeRequest();
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
    feedbackMockWebServer.enqueue(feedbackResponse);
    recaptchaMockWebServer.enqueue(recaptchaPassedResponse);

    mockMvc.perform(post("/energy-pathfinder-feedback")
            .with(csrf())
            .param("serviceRating", "")
            .param(RecaptchaService.RESPONSE_REQUEST_PARAMETER_NAME, "recaptcha-passed-response"))
        .andExpect(status().isOk());

    var observedRequest = feedbackMockWebServer.takeRequest(1, TimeUnit.SECONDS);
    assertThat(observedRequest).isNull();
  }

  @Test
  public void saveFeedback_serverDown_throwCannotSendFeedbackException() throws Exception {
    feedbackMockWebServer.shutdown();
    recaptchaMockWebServer.enqueue(recaptchaPassedResponse);

    mockMvc.perform(post("/energy-pathfinder-feedback")
            .with(csrf())
            .param("serviceRating", "VERY_GOOD")
        .param(RecaptchaService.RESPONSE_REQUEST_PARAMETER_NAME, "recaptcha-passed-response"))
      .andExpect(status().isOk());
  }

  @Test
  public void saveFeedback_failedRecaptcha() throws Exception {

    recaptchaMockWebServer.enqueue(recaptchaFailedResponse);
    var feedbackRequestCount = feedbackMockWebServer.getRequestCount();

    mockMvc.perform(post("/energy-pathfinder-feedback")
        .with(csrf())
        .param("serviceRating", "VERY_SATISFIED")
        .param(RecaptchaService.RESPONSE_REQUEST_PARAMETER_NAME, "recaptcha-failed-response"))
      .andExpect(status().isOk())
      .andExpect(model().errorCount(1));

    var observedRecaptchaRequest = recaptchaMockWebServer.takeRequest();
    var postedRecaptchaBody = observedRecaptchaRequest.getBody().readUtf8();
    assertThat(postedRecaptchaBody).contains("secret=secret-key");
    assertThat(postedRecaptchaBody).contains("response=recaptcha-failed-response");

    //Make sure we didn't send a request to FMS
    assertThat(feedbackMockWebServer.getRequestCount()).isEqualTo(feedbackRequestCount);
  }


  @DynamicPropertySource
  static void dynamicProperties(DynamicPropertyRegistry registry) throws IOException {

    feedbackMockWebServer = new MockWebServer();
    feedbackMockWebServer.start();

    recaptchaMockWebServer = new MockWebServer();
    recaptchaMockWebServer.start();

    registry.add("fms.url.base", () -> String.format("http://localhost:%s", feedbackMockWebServer.getPort()));
    registry.add("fms.http.connectTimeout", () -> "2");
    registry.add("fms.url.saveFeedback", () -> "/api/v1/save-feedback");
    registry.add("fms.service.name", () -> "PATHFINDER");
    registry.add("fms.auth.presharedKey", () -> "dev");
    registry.add("google.recaptcha.verify-url",
      () -> String.format("http://localhost:%s/verify", recaptchaMockWebServer.getPort()));
  }
  @TestConfiguration
  public static class FeedbackTestConfig {

    protected static MockWebServer feedbackMockWebServer;
    protected static MockWebServer recaptchaMockWebServer;

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    public Clock utcClock() {
      return Clock.fixed(Instant.now(), ZoneId.of("UTC"));
    }
  }
}
