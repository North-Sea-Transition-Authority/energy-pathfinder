package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly.QuarterlyUpdateReminderEmailProperties;

@Service
public class TestQuarterlyUpdateReminderService implements QuarterlyUpdateReminder {

  @Override
  public List<RemindableProject> getRemindableProjects() {
    return null;
  }

  @Override
  public QuarterlyUpdateReminderEmailProperties getReminderEmailProperties(String recipientIdentifier,
                                                                           String operatorName,
                                                                           List<String> remindableProjects,
                                                                           List<String> projectsWithPastUpcomingTenders) {
    return null;
  }
}
