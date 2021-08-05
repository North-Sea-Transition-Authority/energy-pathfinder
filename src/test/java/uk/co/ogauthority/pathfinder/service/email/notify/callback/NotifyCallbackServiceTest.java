package uk.co.ogauthority.pathfinder.service.email.notify.callback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.NotifyCallback;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.service.email.notify.NotifyTemplateService;
import uk.co.ogauthority.pathfinder.service.email.notify.callback.failure.PathfinderEmailFailureService;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientApi;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.Template;

@RunWith(MockitoJUnitRunner.class)
public class NotifyCallbackServiceTest {

  private static final String CALLBACK_TOKEN = "test-token";

  @Mock
  private NotificationClientApi notificationClientMock;

  @Mock
  private NotifyTemplateService notifyTemplateService;

  @Mock
  private PathfinderEmailFailureService pathfinderEmailFailureService;

  private NotifyCallback notifyCallback;

  private NotifyCallbackService notifyCallbackService;

  @Before
  public void setup() {

    notifyCallbackService = new NotifyCallbackService(
        notificationClientMock,
        notifyTemplateService,
        pathfinderEmailFailureService,
        CALLBACK_TOKEN
    );

    notifyCallback = new NotifyCallback(
        "be0a4c7d-1657-4b83-8771-2a40e7408d67",
        345235,
        NotifyCallback.NotifyCallbackStatus.DELIVERED,
        "test@test.email.co.uk",
        NotifyCallback.NotifyNotificationType.EMAIL,
        Instant.now(),
        Instant.now(),
        Instant.now()
    );
  }

  @Test
  public void handleCallback_whenEmailWithDeliveredStatus_thenVerifyNoInteractionWithFailureService() {
    notifyCallback.setStatus(NotifyCallback.NotifyCallbackStatus.DELIVERED);
    notifyCallbackService.handleCallback(notifyCallback);
    verifyNoInteractions(pathfinderEmailFailureService);
  }

  @Test
  public void handleCallback_whenEmailWithFailureStatus_thenVerifyInteractionWithFailureService() throws NotificationClientException {

    final var failedNotification = new Notification(getFailedNotifyNotificationJson());

    final var failureStatus = NotifyCallback.NotifyCallbackStatus.PERMANENT_FAILURE;
    notifyCallback.setStatus(failureStatus);
    notifyCallback.setId(failedNotification.getId().toString());

    final var expectedNotifyTemplate = new Template(getExampleTemplateJson());
    final var expectedServiceTemplate = NotifyTemplate.ADDED_TO_TEAM;

    when(notificationClientMock.getNotificationById(anyString())).thenReturn(failedNotification);
    when(notificationClientMock.getTemplateById(anyString())).thenReturn(expectedNotifyTemplate);
    when(notifyTemplateService.getNotifyTemplateByTemplateNameOrError(anyString())).thenReturn(expectedServiceTemplate);

    notifyCallbackService.handleCallback(notifyCallback);

    assertThat(failedNotification.getEmailAddress()).isPresent();
    assertThat(failedNotification.getSubject()).isPresent();

    final var expectedEmailCallback = new EmailCallback(
        failedNotification.getId().toString(),
        expectedServiceTemplate,
        failedNotification.getEmailAddress().get(),
        failedNotification.getSubject().get(),
        failedNotification.getBody(),
        failureStatus
    );

    verify(pathfinderEmailFailureService, times(1)).processNotifyEmailDeliveryFailure(expectedEmailCallback);
  }

  @Test
  public void isTokenValid_whenInvalidToken_thenFalse() {
    final var bearerToken = NotifyCallbackService.AUTHORIZATION_SCHEME + "invalid-token";
    assertThat(notifyCallbackService.isTokenValid(bearerToken)).isFalse();
  }

  @Test
  public void isTokenValid_whenValidToken_thenTrue() {
    final var bearerToken = NotifyCallbackService.AUTHORIZATION_SCHEME + CALLBACK_TOKEN;
    assertThat(notifyCallbackService.isTokenValid(bearerToken)).isTrue();
  }

  private JSONObject getExampleTemplateJson() {
    final var templateJson = new JSONObject();
    templateJson.put("id", UUID.randomUUID().toString());
    templateJson.put("name", "TEMPLATE_NAME");
    templateJson.put("type", "TYPE");
    templateJson.put("created_at", Instant.now().toString());
    templateJson.put("updated_at", Instant.now().toString());
    templateJson.put("version", "1");
    templateJson.put("body", "BODY");
    templateJson.put("subject", "SUBJECT");
    templateJson.put("letter_contact_block", "");
    templateJson.put("personalisation", Map.of());
    return templateJson;
  }

  private JSONObject getFailedNotifyNotificationJson() {
    final var failedNotificationJson = new JSONObject();
    failedNotificationJson.put("id", UUID.randomUUID().toString());
    failedNotificationJson.put("reference", "4234134");
    failedNotificationJson.put("status", "permanent-failure");
    failedNotificationJson.put("email_address", "test@test.email.co.uk");
    failedNotificationJson.put("created_at", Instant.now().toString());
    failedNotificationJson.put("completed_at", Instant.now().toString());
    failedNotificationJson.put("sent_at", Instant.now().toString());
    failedNotificationJson.put("type", "email");

    final var templateJsonObject = new JSONObject();
    templateJsonObject.put("id", UUID.randomUUID().toString());
    templateJsonObject.put("version", "1");
    templateJsonObject.put("uri", "www.template-uri.com");

    failedNotificationJson.put("template", templateJsonObject);

    failedNotificationJson.put("subject", "subject");
    failedNotificationJson.put("body", "body");
    failedNotificationJson.put("estimatedDelivery", Instant.now().toString());
    failedNotificationJson.put("createdByName", "name");

    return failedNotificationJson;
  }
}
