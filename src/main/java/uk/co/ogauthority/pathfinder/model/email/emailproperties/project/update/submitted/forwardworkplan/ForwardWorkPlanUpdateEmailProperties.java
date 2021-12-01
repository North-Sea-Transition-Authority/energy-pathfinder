package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.forwardworkplan;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.ProjectUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class ForwardWorkPlanUpdateEmailProperties extends ProjectUpdateEmailProperties {

  private final String projectOperatorName;

  public ForwardWorkPlanUpdateEmailProperties(String serviceLoginUrl,
                                              String projectOperatorName) {
    super(serviceLoginUrl);
    this.projectOperatorName = projectOperatorName;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {

    final var emailPersonalisation = super.getEmailPersonalisation();

    final var projectTypeDisplayNameLowerCase = ProjectType.FORWARD_WORK_PLAN.getLowercaseDisplayName();

    emailPersonalisation.put(
        ProjectUpdateEmailProperties.UPDATE_SUBMITTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s by %s for their %s.",
            ProjectUpdateEmailProperties.DEFAULT_UPDATE_SUBMITTED_INTRO_TEXT,
            projectOperatorName,
            projectTypeDisplayNameLowerCase
        )
    );

    emailPersonalisation.put(
        ProjectUpdateEmailProperties.UPDATE_SUBMITTED_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s by %s for their %s",
            ProjectUpdateEmailProperties.DEFAULT_UPDATE_SUBJECT_TEXT,
            projectOperatorName,
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
    ForwardWorkPlanUpdateEmailProperties that = (ForwardWorkPlanUpdateEmailProperties) o;
    return Objects.equals(projectOperatorName, that.projectOperatorName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), projectOperatorName);
  }
}
