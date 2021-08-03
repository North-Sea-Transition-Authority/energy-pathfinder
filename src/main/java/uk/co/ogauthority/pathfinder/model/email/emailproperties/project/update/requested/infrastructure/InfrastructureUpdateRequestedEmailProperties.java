package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.infrastructure;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.ProjectUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class InfrastructureUpdateRequestedEmailProperties extends ProjectUpdateRequestedEmailProperties {

  private final String regulatorMnemonic;

  private final String projectTitle;

  public InfrastructureUpdateRequestedEmailProperties(String updateReason,
                                                      String deadlineDate,
                                                      String projectUrl,
                                                      String regulatorMnemonic,
                                                      String projectTitle) {
    super(updateReason, deadlineDate, projectUrl);
    this.regulatorMnemonic = regulatorMnemonic;
    this.projectTitle = projectTitle;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {

    final var emailPersonalisation = super.getEmailPersonalisation();

    final var projectTypeDisplayNameLowerCase = ProjectType.INFRASTRUCTURE.getLowercaseDisplayName();

    emailPersonalisation.put(
        ProjectUpdateRequestedEmailProperties.UPDATE_REQUESTED_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s on %s: %s",
            ProjectUpdateRequestedEmailProperties.DEFAULT_UPDATE_REQUESTED_SUBJECT_TEXT,
            projectTypeDisplayNameLowerCase,
            projectTitle
        )
    );

    emailPersonalisation.put(
        ProjectUpdateRequestedEmailProperties.UPDATE_REQUESTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s on your %s, %s.",
            String.format("The %s have requested an update", regulatorMnemonic),
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
    InfrastructureUpdateRequestedEmailProperties that = (InfrastructureUpdateRequestedEmailProperties) o;
    return Objects.equals(regulatorMnemonic, that.regulatorMnemonic)
        && Objects.equals(projectTitle, that.projectTitle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), regulatorMnemonic, projectTitle);
  }
}
