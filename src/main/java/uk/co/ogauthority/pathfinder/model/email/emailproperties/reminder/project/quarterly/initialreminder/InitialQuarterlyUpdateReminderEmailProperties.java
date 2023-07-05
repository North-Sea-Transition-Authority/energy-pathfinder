package uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly.initialreminder;

import java.util.List;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly.QuarterlyUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class InitialQuarterlyUpdateReminderEmailProperties extends QuarterlyUpdateReminderEmailProperties {

  public InitialQuarterlyUpdateReminderEmailProperties(String recipientIdentifier,
                                                       String operatorName,
                                                       List<String> remindableProjects,
                                                       String serviceLoginUrl,
                                                       List<String> pastUpcomingTenders) {
    super(
        NotifyTemplate.INITIAL_QUARTERLY_PROJECT_UPDATE_REMINDER,
        recipientIdentifier,
        operatorName,
        remindableProjects,
        serviceLoginUrl,
        pastUpcomingTenders
    );
  }
}
