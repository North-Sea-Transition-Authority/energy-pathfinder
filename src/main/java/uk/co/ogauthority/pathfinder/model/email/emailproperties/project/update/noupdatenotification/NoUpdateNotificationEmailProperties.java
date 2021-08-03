package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.noupdatenotification;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;

public class NoUpdateNotificationEmailProperties extends EmailProperties {

  public static final String NO_UPDATE_PROJECT_INTRO_TEXT_MAIL_MERGE_FIELD_NAME = "NO_UPDATE_PROJECT_INTRO_TEXT";
  public static final String NO_UPDATE_NOTIFICATION_SUBJECT_MAIL_MERGE_FIELD_NAME = "NO_UPDATE_SUBJECT_TEXT";
  public static final String NO_UPDATE_REASON_MAIL_MERGE_FIELD_NAME = "NO_UPDATE_REASON";

  public static final String DEFAULT_NO_UPDATE_PROJECT_INTRO_TEXT = "A no update notification has been submitted";
  public static final String DEFAULT_NO_UPDATE_SUBJECT_TEXT = "No update notification submitted";

  private final String noUpdateReason;

  private final String serviceLoginUrl;

  public NoUpdateNotificationEmailProperties(String serviceLoginUrl,
                                             String noUpdateReason) {
    super(NotifyTemplate.NO_UPDATE_NOTIFICATION);
    this.serviceLoginUrl = serviceLoginUrl;
    this.noUpdateReason = noUpdateReason;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {

    final var emailPersonalisation = super.getEmailPersonalisation();

    emailPersonalisation.put(CommonEmailMergeField.SERVICE_LOGIN_URL, serviceLoginUrl);

    emailPersonalisation.put("NO_UPDATE_REASON", noUpdateReason);

    emailPersonalisation.put(
        NO_UPDATE_PROJECT_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("%s.", DEFAULT_NO_UPDATE_PROJECT_INTRO_TEXT)
    );

    emailPersonalisation.put(
        NO_UPDATE_NOTIFICATION_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format("%s", DEFAULT_NO_UPDATE_SUBJECT_TEXT)
    );

    return emailPersonalisation;
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (this == o) {
      return true;
    }
    if (getClass() != o.getClass()) {
      return false;
    }
    NoUpdateNotificationEmailProperties that = (NoUpdateNotificationEmailProperties) o;
    return Objects.equals(noUpdateReason, that.noUpdateReason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), noUpdateReason);
  }
}
