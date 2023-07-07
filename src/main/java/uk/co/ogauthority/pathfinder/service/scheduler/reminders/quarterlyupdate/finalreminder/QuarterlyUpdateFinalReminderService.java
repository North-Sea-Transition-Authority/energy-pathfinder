package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.finalreminder;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly.QuarterlyUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly.finalreminder.FinalQuarterlyUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.QuarterlyUpdateReminder;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.QuarterlyUpdateReminderService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.RemindableProject;

@Service
class QuarterlyUpdateFinalReminderService implements QuarterlyUpdateReminder {

  private static final Logger LOGGER = LoggerFactory.getLogger(QuarterlyUpdateFinalReminderService.class);

  private final QuarterlyUpdateReminderService quarterlyUpdateReminderService;

  private final LinkService linkService;

  @Autowired
  QuarterlyUpdateFinalReminderService(QuarterlyUpdateReminderService quarterlyUpdateReminderService,
                                      LinkService linkService) {
    this.quarterlyUpdateReminderService = quarterlyUpdateReminderService;
    this.linkService = linkService;
  }

  void sendFinalQuarterlyUpdateReminder() {
    try {
      quarterlyUpdateReminderService.sendQuarterlyProjectUpdateReminderToOperators(this);
    } catch (Exception ex) {
      LOGGER.error("Failed to send final quarterly update reminder to operators", ex);
    }
  }

  @Override
  public List<RemindableProject> getRemindableProjects() {
    return quarterlyUpdateReminderService.getRemindableProjectsNotUpdatedInCurrentQuarter();
  }

  @Override
  public QuarterlyUpdateReminderEmailProperties getReminderEmailProperties(String recipientIdentifier,
                                                                           String operatorName,
                                                                           List<String> remindableProjects,
                                                                           List<String> projectsWithPastUpcomingTenders) {
    return new FinalQuarterlyUpdateReminderEmailProperties(
        recipientIdentifier,
        operatorName,
        remindableProjects,
        linkService.getWorkAreaUrl(),
        projectsWithPastUpcomingTenders
    );
  }
}
