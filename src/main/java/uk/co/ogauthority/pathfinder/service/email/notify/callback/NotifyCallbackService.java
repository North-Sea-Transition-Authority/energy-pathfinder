package uk.co.ogauthority.pathfinder.service.email.notify.callback;

import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.NotifyCallback;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.service.email.notify.NotifyTemplateService;
import uk.co.ogauthority.pathfinder.service.email.notify.callback.failure.PathfinderEmailFailureService;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientApi;
import uk.gov.service.notify.NotificationClientException;

@Service
public class NotifyCallbackService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotifyCallbackService.class);

  private static final Set<NotifyCallback.NotifyCallbackStatus> FAILURE_STATUSES = Set.of(
      NotifyCallback.NotifyCallbackStatus.PERMANENT_FAILURE,
      NotifyCallback.NotifyCallbackStatus.TEMPORARY_FAILURE
  );

  protected static final String AUTHORIZATION_SCHEME = "Bearer ";

  private final NotificationClientApi notificationClient;

  private final NotifyTemplateService notifyTemplateService;

  private final PathfinderEmailFailureService pathfinderEmailFailureService;

  private final String callbackToken;

  @Autowired
  public NotifyCallbackService(NotificationClientApi notificationClient,
                               NotifyTemplateService notifyTemplateService,
                               PathfinderEmailFailureService pathfinderEmailFailureService,
                               @Value("${email.notifyCallbackToken}") String callbackToken) {
    this.notificationClient = notificationClient;
    this.notifyTemplateService = notifyTemplateService;
    this.pathfinderEmailFailureService = pathfinderEmailFailureService;
    this.callbackToken = callbackToken;
  }

  public void handleCallback(NotifyCallback notifyCallback) {

    if (FAILURE_STATUSES.contains(notifyCallback.getStatus())) {

      try {
        final var failedEmail = notificationClient.getNotificationById(notifyCallback.getId());
        final var failedEmailNotifyTemplate = notificationClient.getTemplateById(failedEmail.getTemplateId().toString());

        final var serviceEmailTemplate = notifyTemplateService.getNotifyTemplateByTemplateNameOrError(
            failedEmailNotifyTemplate.getName()
        );

        final var emailCallback = createEmailCallbackObject(
            notifyCallback,
            failedEmail,
            serviceEmailTemplate
        );

        pathfinderEmailFailureService.processNotifyEmailDeliveryFailure(emailCallback);

      } catch (NotificationClientException | IllegalArgumentException exception) {
        LOGGER.error(
            String.format("Failed to retrieve Notify information for notification with ID %s", notifyCallback.getId()),
            exception
        );
      }
    }
  }

  public boolean isTokenValid(String bearerToken) {
    return StringUtils.removeStart(bearerToken, AUTHORIZATION_SCHEME).equals(callbackToken);
  }

  private EmailCallback createEmailCallbackObject(NotifyCallback notifyCallback,
                                                  Notification notification,
                                                  NotifyTemplate notifyTemplate) throws NotificationClientException {
    return new EmailCallback(
        notifyCallback.getId(),
        notifyTemplate,
        notification.getEmailAddress()
            .orElseThrow(() -> new NotificationClientException("Email address for notification with ID %s email cannot be retrieved")),
        notification.getSubject()
            .orElse(""),
        notification.getBody(),
        notifyCallback.getStatus()
    );
  }
}
