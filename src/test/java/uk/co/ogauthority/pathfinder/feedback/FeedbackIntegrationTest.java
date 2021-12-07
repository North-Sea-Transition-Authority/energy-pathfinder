package uk.co.ogauthority.pathfinder.feedback;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.ogauthority.pathfinder.feedback.FeedbackIntegrationTest.FeedbackTestConfig.mockWebServer;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.fivium.feedbackmanagementservice.client.FeedbackClientService;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.feedback.FeedbackController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail;
import uk.co.ogauthority.pathfinder.model.enums.feedback.ServiceFeedbackRating;
import uk.co.ogauthority.pathfinder.mvc.error.DefaultExceptionResolver;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.service.contact.SupportContactService;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = FeedbackController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {FeedbackService.class, FeedbackModelService.class, ValidationService.class,
            FeedbackEmailService.class, DefaultExceptionResolver.class, SupportContactService.class}))
            //DefaultExceptionResolver and SupportContactService have to be included so
            //Spring knows how to handle CannotSendFeedbackException in some tests
@Import({FeedbackIntegrationTest.FeedbackTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FeedbackIntegrationTest extends AbstractControllerTest {

  private final static String TEST_RATING = ServiceFeedbackRating.NEITHER.name();
  private final static String COMMENT = "testImprovement";
  private final static Instant TEST_DATETIME = Instant.parse("2020-04-29T10:15:30Z");
  private final static Integer TEST_PROJECT_ID = 1;
  private final static String TEST_TITLE = "TestTitle";
  private final static Integer TEST_PROJECT_DETAIL_ID = 2;
  private final static String SUPPORT_EMAIl = ServiceContactDetail.TECHNICAL_SUPPORT.getEmailAddress();

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  ProjectDetailsRepository projectDetailsRepository;

  @MockBean
  ProjectInformationService projectInformationService;

  @MockBean
  EmailService emailService;

  private AuthenticatedUserAccount user;
  private Person person;
  private MockResponse responseWhenAuthorized;
  private MockResponse responseWhenNotAuthorized;

  @Before
  public void setUp() {
    user = UserTestingUtil.getAuthenticatedUserAccount();
    person = user.getLinkedPerson();

    var projectDetail = ProjectUtil.getProjectDetails();
    when(projectDetailsRepository.findById(TEST_PROJECT_DETAIL_ID)).thenReturn(Optional.of(projectDetail));

    when(projectInformationService.getProjectTitle(projectDetail)).thenReturn(TEST_TITLE);

    responseWhenAuthorized = new MockResponse()
        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .setResponseCode(200)
        .setBody("{\"feedbackId\": 1}");

    responseWhenNotAuthorized = new MockResponse()
        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .setResponseCode(403)
        .setBody("{\"timestamp\":\"2021-11-04T17:55:25.329+00:00\",\"status\":403,\"error\":\"Forbidden\",\"message\":\"Access Denied\",\"path\":\"/fmslocal/api/v1/save-feedback\"}");
  }

  @After
  public void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  public void saveFeedback_authorized() throws Exception {
    mockWebServer.enqueue(responseWhenAuthorized);

    mockMvc.perform(post("/feedback")
            .with(authenticatedUserAndSession(user))
            .with(csrf())
            .param("serviceRating", TEST_RATING)
            .param("feedback", COMMENT))
        .andExpect(redirectedUrlTemplate("/work-area"));

    verify(emailService, never()).sendEmail(any(EmailProperties.class), any());

    var observedRequest = mockWebServer.takeRequest();
    assertThat(observedRequest.getMethod()).isEqualTo(HttpMethod.POST.toString());
    assertThat(observedRequest.getPath()).isEqualTo("/api/v1/save-feedback");
    assertThat(observedRequest.getHeaders().get("Content-type")).isEqualTo("application/json");
    assertThat(observedRequest.getHeaders().get("Authorization")).isEqualTo("dev");
  }

  @Test
  public void saveFeedback_unauthorized()  throws Exception{
    mockWebServer.enqueue(responseWhenNotAuthorized);

    mockMvc.perform(post("/feedback")
            .with(authenticatedUserAndSession(user))
            .with(csrf())
            .param("serviceRating", TEST_RATING)
            .param("feedback", COMMENT))
        .andExpect(redirectedUrlTemplate("/work-area"));

    verify(emailService, times(1)).sendEmail(any(EmailProperties.class), eq(SUPPORT_EMAIl));
  }

  @Test
  public void saveFeedback_withProjectDetailId() throws Exception {
    mockWebServer.enqueue(responseWhenAuthorized);

    mockMvc.perform(post("/feedback")
            .with(authenticatedUserAndSession(user))
            .with(csrf())
            .param("serviceRating", TEST_RATING)
            .param("feedback", COMMENT)
            .param("projectDetailId", TEST_PROJECT_DETAIL_ID.toString()))
        .andExpect(redirectedUrlTemplate("/work-area"));

    var observedRequest = mockWebServer.takeRequest();
    assertThat(observedRequest.getMethod()).isEqualTo(HttpMethod.POST.toString());
    assertThat(observedRequest.getPath()).isEqualTo("/api/v1/save-feedback");
    assertThat(observedRequest.getHeaders().get("Content-type")).isEqualTo("application/json");
    assertThat(observedRequest.getHeaders().get("Authorization")).isEqualTo("dev");

    var postedJson = objectMapper.readTree(observedRequest.getBody().readUtf8());
    assertThat(postedJson.get("submitterName").asText()).isEqualTo(person.getFullName());
    assertThat(postedJson.get("submitterEmail").asText()).isEqualTo(person.getEmailAddress());
    assertThat(postedJson.get("serviceRating").asText()).isEqualTo(TEST_RATING);
    assertThat(postedJson.get("comment").asText()).isEqualTo(COMMENT);
    assertThat(Instant.parse(postedJson.get("givenDatetime").asText())).isEqualTo(TEST_DATETIME);
    assertThat(postedJson.get("serviceName").asText()).isEqualTo("PATHFINDER");
    assertThat(postedJson.get("transactionId").asText()).isEqualTo(TEST_PROJECT_ID.toString());
    assertThat(postedJson.get("transactionReference").asText()).isEqualTo(TEST_TITLE);
  }

  @Test
  public void saveFeedback_withoutProjectDetailId() throws Exception {
    mockWebServer.enqueue(responseWhenAuthorized);

    mockMvc.perform(post("/feedback")
            .with(authenticatedUserAndSession(user))
            .with(csrf())
            .param("serviceRating", TEST_RATING)
            .param("feedback", COMMENT))
        .andExpect(redirectedUrlTemplate("/work-area"));

    var observedRequest = mockWebServer.takeRequest();
    assertThat(observedRequest.getMethod()).isEqualTo(HttpMethod.POST.toString());
    assertThat(observedRequest.getPath()).isEqualTo("/api/v1/save-feedback");
    assertThat(observedRequest.getHeaders().get("Content-type")).isEqualTo("application/json");
    assertThat(observedRequest.getHeaders().get("Authorization")).isEqualTo("dev");

    var postedJson = objectMapper.readTree(observedRequest.getBody().readUtf8());
    assertThat(postedJson.get("submitterName").asText()).isEqualTo(person.getFullName());
    assertThat(postedJson.get("submitterEmail").asText()).isEqualTo(person.getEmailAddress());
    assertThat(postedJson.get("serviceRating").asText()).isEqualTo(TEST_RATING);
    assertThat(postedJson.get("comment").asText()).isEqualTo(COMMENT);
    assertThat(Instant.parse(postedJson.get("givenDatetime").asText())).isEqualTo(TEST_DATETIME);
    assertThat(postedJson.get("serviceName").asText()).isEqualTo("PATHFINDER");
    assertThat(postedJson.get("transactionId").isNull()).isTrue();
    assertThat(postedJson.get("transactionReference").isNull()).isTrue();
  }

  @Test
  public void saveFeedback_invalidParameters() throws Exception {
    mockWebServer.enqueue(responseWhenAuthorized);

    mockMvc.perform(post("/feedback")
            .with(authenticatedUserAndSession(user))
            .with(csrf())
            .param("serviceRating", ""))
        .andExpect(status().isOk());

    var observedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
    assertThat(observedRequest).isNull();
  }

  @Test
  public void saveFeedback_serverDown_throwCannotSendFeedbackException() throws Exception {
    mockWebServer.shutdown();

    mockMvc.perform(post("/feedback")
            .with(authenticatedUserAndSession(user))
            .with(csrf())
            .param("serviceRating", TEST_RATING)
            .param("feedback", COMMENT))
        .andExpect(redirectedUrlTemplate("/work-area"));

    verify(emailService, times(1)).sendEmail(any(EmailProperties.class), eq(SUPPORT_EMAIl));
  }

  @Test
  public void saveFeedback_unexpectedRespone() throws Exception {
    mockWebServer.enqueue(new MockResponse()
        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .setResponseCode(500)
    );

    mockMvc.perform(post("/feedback")
            .with(authenticatedUserAndSession(user))
            .with(csrf())
            .param("serviceRating", TEST_RATING)
            .param("feedback", COMMENT))
        .andExpect(redirectedUrlTemplate("/work-area"));

    verify(emailService, times(1)).sendEmail(any(EmailProperties.class), eq(SUPPORT_EMAIl));
  }

  @TestConfiguration
  public static class FeedbackTestConfig {

    protected static MockWebServer mockWebServer;

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    public FeedbackClientService getFeedbackClientService() throws IOException {
      mockWebServer = new MockWebServer();
      mockWebServer.start();
      String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
      return new FeedbackClientService(objectMapper, baseUrl, 20L, "/api/v1/save-feedback", "PATHFINDER", "dev");
    }

    @Bean
    public Clock utcClock() {
      return Clock.fixed(TEST_DATETIME, ZoneId.of("UTC"));
    }
  }
}
