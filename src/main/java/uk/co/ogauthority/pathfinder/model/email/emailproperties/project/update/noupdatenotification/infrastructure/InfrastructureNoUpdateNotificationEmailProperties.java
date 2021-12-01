package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.noupdatenotification.infrastructure;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.noupdatenotification.NoUpdateNotificationEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class InfrastructureNoUpdateNotificationEmailProperties extends NoUpdateNotificationEmailProperties {

  private final String projectName;

  private final String projectOperatorName;

  public InfrastructureNoUpdateNotificationEmailProperties(String projectName,
                                                           String serviceLoginUrl,
                                                           String noUpdateReason,
                                                           String projectOperatorName) {
    super(serviceLoginUrl, noUpdateReason);
    this.projectName = projectName;
    this.projectOperatorName = projectOperatorName;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {

    final var emailPersonalisation = super.getEmailPersonalisation();

    final var projectTypeDisplayNameLowerCase = ProjectType.INFRASTRUCTURE.getLowercaseDisplayName();

    emailPersonalisation.put(
        NoUpdateNotificationEmailProperties.NO_UPDATE_PROJECT_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s by %s for %s: %s.",
            NoUpdateNotificationEmailProperties.DEFAULT_NO_UPDATE_PROJECT_INTRO_TEXT,
            projectOperatorName,
            projectTypeDisplayNameLowerCase,
            projectName
        )
    );

    emailPersonalisation.put(
        NoUpdateNotificationEmailProperties.NO_UPDATE_NOTIFICATION_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s for %s: %s",
            NoUpdateNotificationEmailProperties.DEFAULT_NO_UPDATE_SUBJECT_TEXT,
            projectTypeDisplayNameLowerCase,
            projectName
        )
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
    InfrastructureNoUpdateNotificationEmailProperties that = (InfrastructureNoUpdateNotificationEmailProperties) o;
    return Objects.equals(projectName, that.projectName)
        && Objects.equals(projectOperatorName, that.projectOperatorName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), projectName, projectOperatorName);
  }

}
