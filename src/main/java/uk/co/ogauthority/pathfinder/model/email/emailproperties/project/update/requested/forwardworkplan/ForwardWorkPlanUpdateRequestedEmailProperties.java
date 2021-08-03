package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.forwardworkplan;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.ProjectUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class ForwardWorkPlanUpdateRequestedEmailProperties extends ProjectUpdateRequestedEmailProperties {

  private final String regulatorMnemonic;

  public ForwardWorkPlanUpdateRequestedEmailProperties(String updateReason,
                                                       String deadlineDate,
                                                       String projectUrl,
                                                       String regulatorMnemonic) {
    super(updateReason, deadlineDate, projectUrl);
    this.regulatorMnemonic = regulatorMnemonic;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {

    final var emailPersonalisation = super.getEmailPersonalisation();

    final var projectTypeDisplayNameLowerCase = ProjectType.FORWARD_WORK_PLAN.getLowercaseDisplayName();

    emailPersonalisation.put(
        ProjectUpdateRequestedEmailProperties.UPDATE_REQUESTED_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s to your organisation's %s",
            ProjectUpdateRequestedEmailProperties.DEFAULT_UPDATE_REQUESTED_SUBJECT_TEXT,
            projectTypeDisplayNameLowerCase
        )
    );

    emailPersonalisation.put(
        ProjectUpdateRequestedEmailProperties.UPDATE_REQUESTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s to your organisation's %s.",
            String.format("The %s have requested an update", regulatorMnemonic),
            projectTypeDisplayNameLowerCase
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
    ForwardWorkPlanUpdateRequestedEmailProperties that = (ForwardWorkPlanUpdateRequestedEmailProperties) o;
    return Objects.equals(regulatorMnemonic, that.regulatorMnemonic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), regulatorMnemonic);
  }
}
