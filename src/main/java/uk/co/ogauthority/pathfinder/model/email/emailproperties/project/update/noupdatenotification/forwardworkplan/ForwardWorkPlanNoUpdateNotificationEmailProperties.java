package uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.noupdatenotification.forwardworkplan;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.noupdatenotification.NoUpdateNotificationEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class ForwardWorkPlanNoUpdateNotificationEmailProperties extends NoUpdateNotificationEmailProperties {

  private final String projectOperatorName;

  public ForwardWorkPlanNoUpdateNotificationEmailProperties(String serviceLoginUrl,
                                                            String noUpdateReason,
                                                            String projectOperatorName) {
    super(serviceLoginUrl, noUpdateReason);
    this.projectOperatorName = projectOperatorName;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {

    final var emailPersonalisation = super.getEmailPersonalisation();

    final var projectTypeDisplayNameLowerCase = ProjectType.FORWARD_WORK_PLAN.getLowercaseDisplayName();

    emailPersonalisation.put(
        NoUpdateNotificationEmailProperties.NO_UPDATE_PROJECT_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s by %s for their %s.",
            NoUpdateNotificationEmailProperties.DEFAULT_NO_UPDATE_PROJECT_INTRO_TEXT,
            projectOperatorName,
            projectTypeDisplayNameLowerCase
        )
    );

    emailPersonalisation.put(
        NoUpdateNotificationEmailProperties.NO_UPDATE_NOTIFICATION_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s by %s for their %s",
            NoUpdateNotificationEmailProperties.DEFAULT_NO_UPDATE_SUBJECT_TEXT,
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
    ForwardWorkPlanNoUpdateNotificationEmailProperties that = (ForwardWorkPlanNoUpdateNotificationEmailProperties) o;
    return Objects.equals(projectOperatorName, that.projectOperatorName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), projectOperatorName);
  }

}
