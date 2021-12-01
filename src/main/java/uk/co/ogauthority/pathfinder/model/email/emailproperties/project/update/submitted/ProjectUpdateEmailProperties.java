package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;

public class ProjectUpdateEmailProperties extends EmailProperties {

  public static final String UPDATE_SUBMITTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME = "UPDATE_SUBMITTED_INTRO_TEXT";
  public static final String UPDATE_SUBMITTED_SUBJECT_MAIL_MERGE_FIELD_NAME = "UPDATE_SUBMITTED_SUBJECT_TEXT";

  public static final String DEFAULT_UPDATE_SUBMITTED_INTRO_TEXT = "An update has been submitted";
  public static final String DEFAULT_UPDATE_SUBJECT_TEXT = "An update has been submitted";

  private final String serviceLoginUrl;

  public ProjectUpdateEmailProperties(String serviceLoginUrl) {
    super(NotifyTemplate.PROJECT_UPDATE_SUBMITTED);
    this.serviceLoginUrl = serviceLoginUrl;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {

    final var emailPersonalisation = super.getEmailPersonalisation();

    emailPersonalisation.put(CommonEmailMergeField.SERVICE_LOGIN_URL, serviceLoginUrl);

    emailPersonalisation.put(
        UPDATE_SUBMITTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("%s.", DEFAULT_UPDATE_SUBMITTED_INTRO_TEXT)
    );

    emailPersonalisation.put(
        UPDATE_SUBMITTED_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format("%s", DEFAULT_UPDATE_SUBJECT_TEXT)
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
    ProjectUpdateEmailProperties that = (ProjectUpdateEmailProperties) o;
    return Objects.equals(serviceLoginUrl, that.serviceLoginUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), serviceLoginUrl);
  }
}
