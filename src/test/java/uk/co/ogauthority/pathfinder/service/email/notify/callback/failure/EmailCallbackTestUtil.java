package uk.co.ogauthority.pathfinder.service.email.notify.callback.failure;

import uk.co.ogauthority.pathfinder.model.email.NotifyCallback;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.service.email.notify.callback.EmailCallback;

public class EmailCallbackTestUtil {

  private static final String NOTIFICATION_ID = "123-456";
  private static final NotifyTemplate NOTIFY_TEMPLATE = NotifyTemplate.ADDED_TO_TEAM;
  private static final String RECIPIENT_EMAIL_ADDRESS = "someone@example.com";
  private static final String SUBJECT = "subject";
  private static final String BODY = "body";
  private static final NotifyCallback.NotifyCallbackStatus STATUS = NotifyCallback.NotifyCallbackStatus.DELIVERED;

  public static EmailCallback constructEmailCallback(NotifyTemplate notifyTemplate,
                                                     NotifyCallback.NotifyCallbackStatus notifyCallbackStatus) {
    return constructEmailCallback(
        NOTIFICATION_ID,
        notifyTemplate,
        RECIPIENT_EMAIL_ADDRESS,
        SUBJECT,
        BODY,
        notifyCallbackStatus
    );
  }

  public static EmailCallback constructEmailCallback(String recipientEmailAddress) {
    return constructEmailCallback(
        NOTIFICATION_ID,
        NOTIFY_TEMPLATE,
        recipientEmailAddress,
        SUBJECT,
        BODY,
        STATUS
    );
  }

  public static EmailCallback constructEmailCallback(String notificationId,
                                                     NotifyTemplate notifyTemplate,
                                                     String recipientEmailAddress,
                                                     String subject,
                                                     String body,
                                                     NotifyCallback.NotifyCallbackStatus notifyCallbackStatus) {
    return new EmailCallback(
        notificationId,
        notifyTemplate,
        recipientEmailAddress,
        subject,
        body,
        notifyCallbackStatus
    );
  }
}
