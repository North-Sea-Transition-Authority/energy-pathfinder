package uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections.CollectionUtils;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public abstract class QuarterlyUpdateReminderEmailProperties extends EmailProperties {

  private final String operatorName;
  private final List<String> remindableProjects;
  private final String serviceLoginUrl;
  private final List<String> projectsWithPastUpcomingTenders;

  public QuarterlyUpdateReminderEmailProperties(NotifyTemplate template,
                                                String recipientIdentifier,
                                                String operatorName,
                                                List<String> remindableProjects,
                                                String serviceLoginUrl,
                                                List<String> projectsWithPastUpcomingTenders) {
    super(template, recipientIdentifier);
    this.operatorName = operatorName;
    this.remindableProjects = remindableProjects;
    this.serviceLoginUrl = serviceLoginUrl;
    this.projectsWithPastUpcomingTenders = projectsWithPastUpcomingTenders;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var emailPersonalisation = super.getEmailPersonalisation();
    var showProjectsWithPastUpcomingTenders = CollectionUtils.isEmpty(projectsWithPastUpcomingTenders)  ? "no" : "yes";

    emailPersonalisation.put("OPERATOR_NAME", operatorName);
    emailPersonalisation.put("OPERATOR_PROJECTS", remindableProjects);
    emailPersonalisation.put("SERVICE_LOGIN_URL", serviceLoginUrl);
    emailPersonalisation.put("SHOW_PROJECTS_WITH_PAST_UPCOMING_TENDERS", showProjectsWithPastUpcomingTenders);
    emailPersonalisation.put("PROJECTS_WITH_PAST_UPCOMING_TENDERS", projectsWithPastUpcomingTenders);
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

    if (!(o instanceof QuarterlyUpdateReminderEmailProperties)) {
      return false;
    }

    QuarterlyUpdateReminderEmailProperties that = (QuarterlyUpdateReminderEmailProperties) o;
    return Objects.equals(operatorName, that.operatorName)
        && Objects.equals(remindableProjects, that.remindableProjects)
        && Objects.equals(serviceLoginUrl, that.serviceLoginUrl)
        && Objects.equals(projectsWithPastUpcomingTenders, that.projectsWithPastUpcomingTenders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        operatorName,
        remindableProjects,
        serviceLoginUrl,
        projectsWithPastUpcomingTenders
    );
  }
}
