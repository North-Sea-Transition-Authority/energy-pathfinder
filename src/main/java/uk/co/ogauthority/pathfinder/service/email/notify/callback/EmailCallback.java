package uk.co.ogauthority.pathfinder.service.email.notify.callback;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.NotifyCallback;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public final class EmailCallback {

  private final String notificationId;

  private final NotifyTemplate template;

  private final String recipientEmailAddress;

  private final String subject;

  private final String body;

  private final NotifyCallback.NotifyCallbackStatus status;

  public EmailCallback(String notificationId,
                       NotifyTemplate template,
                       String recipientEmailAddress,
                       String subject,
                       String body,
                       NotifyCallback.NotifyCallbackStatus status) {
    this.notificationId = notificationId;
    this.template = template;
    this.recipientEmailAddress = recipientEmailAddress;
    this.subject = subject;
    this.body = body;
    this.status = status;
  }

  public String getNotificationId() {
    return notificationId;
  }

  public NotifyTemplate getTemplate() {
    return template;
  }

  public String getRecipientEmailAddress() {
    return recipientEmailAddress;
  }

  public String getSubject() {
    return subject;
  }

  public String getBody() {
    return body;
  }

  public NotifyCallback.NotifyCallbackStatus getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return "EmailCallback {" +
        "  notificationId='" + notificationId + '\'' +
        ", template=" + template.getTemplateName() +
        ", recipientEmailAddress='" + recipientEmailAddress + '\'' +
        ", subject='" + subject + '\'' +
        ", body=" + body + '\'' +
        ", status=" + status + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof EmailCallback)) {
      return false;
    }

    EmailCallback that = (EmailCallback) o;
    return Objects.equals(notificationId, that.notificationId)
        && Objects.equals(template, that.template)
        && Objects.equals(recipientEmailAddress, that.recipientEmailAddress)
        && Objects.equals(subject, that.subject)
        && Objects.equals(body, that.body)
        && Objects.equals(status, that.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        notificationId,
        template,
        recipientEmailAddress,
        subject,
        body,
        subject
    );
  }
}
