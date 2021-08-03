package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.infrastructure;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.ProjectUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class InfrastructureUpdateEmailProperties extends ProjectUpdateEmailProperties {

  private final String projectOperatorName;

  private final String projectTitle;

  public InfrastructureUpdateEmailProperties(String serviceLoginUrl,
                                             String projectOperatorName,
                                             String projectTitle) {
    super(serviceLoginUrl);
    this.projectOperatorName = projectOperatorName;
    this.projectTitle = projectTitle;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {

    final var emailPersonalisation = super.getEmailPersonalisation();

    final var projectTypeDisplayNameLowerCase = ProjectType.INFRASTRUCTURE.getLowercaseDisplayName();

    emailPersonalisation.put(
        ProjectUpdateEmailProperties.UPDATE_SUBMITTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s by %s for %s: %s.",
            ProjectUpdateEmailProperties.DEFAULT_UPDATE_SUBMITTED_INTRO_TEXT,
            projectOperatorName,
            projectTypeDisplayNameLowerCase,
            projectTitle
        )
    );

    emailPersonalisation.put(
        ProjectUpdateEmailProperties.UPDATE_SUBMITTED_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s for %s: %s",
            ProjectUpdateEmailProperties.DEFAULT_UPDATE_SUBJECT_TEXT,
            projectTypeDisplayNameLowerCase,
            projectTitle
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
    InfrastructureUpdateEmailProperties that = (InfrastructureUpdateEmailProperties) o;
    return Objects.equals(projectOperatorName, that.projectOperatorName)
        && Objects.equals(projectTitle, that.projectTitle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), projectOperatorName, projectTitle);
  }
}
