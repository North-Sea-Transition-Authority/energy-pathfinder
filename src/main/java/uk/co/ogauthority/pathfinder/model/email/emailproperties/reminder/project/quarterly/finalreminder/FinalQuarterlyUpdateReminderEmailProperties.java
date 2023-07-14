package uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly.finalreminder;

import java.util.List;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly.QuarterlyUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class FinalQuarterlyUpdateReminderEmailProperties extends QuarterlyUpdateReminderEmailProperties {


  public FinalQuarterlyUpdateReminderEmailProperties(String recipientIdentifier,
                                                     String operatorName,
                                                     List<String> remindableProjects,
                                                     String serviceLoginUrl,
                                                     List<String> projectsWithPastUpcomingTenders) {
    super(
        NotifyTemplate.FINAL_QUARTERLY_PROJECT_UPDATE_REMINDER,
        recipientIdentifier,
        operatorName,
        remindableProjects,
        serviceLoginUrl,
        projectsWithPastUpcomingTenders
    );
  }
}
