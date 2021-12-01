package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.initialreminder;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly.QuarterlyUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly.initialreminder.InitialQuarterlyUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.QuarterlyUpdateReminder;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.QuarterlyUpdateReminderService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.RemindableProject;

@Service
class QuarterlyUpdateInitialReminderService implements QuarterlyUpdateReminder {

  private static final Logger LOGGER = LoggerFactory.getLogger(QuarterlyUpdateInitialReminderService.class);

  private final QuarterlyUpdateReminderService quarterlyUpdateReminderService;

  private final EmailLinkService emailLinkService;

  @Autowired
  QuarterlyUpdateInitialReminderService(QuarterlyUpdateReminderService quarterlyUpdateReminderService,
                                        EmailLinkService emailLinkService) {
    this.quarterlyUpdateReminderService = quarterlyUpdateReminderService;
    this.emailLinkService = emailLinkService;
  }

  void sendInitialQuarterlyReminder() {
    try {
      quarterlyUpdateReminderService.sendQuarterlyProjectUpdateReminderToOperators(this);
    } catch (Exception ex) {
      LOGGER.error("Failed to send initial quarterly update reminder to operators", ex);
    }
  }

  @Override
  public List<RemindableProject> getRemindableProjects() {
    return quarterlyUpdateReminderService.getAllRemindableProjects();
  }

  @Override
  public QuarterlyUpdateReminderEmailProperties getReminderEmailProperties(String recipientIdentifier,
                                                                           String operatorName,
                                                                           List<String> remindableProjects) {
    return new InitialQuarterlyUpdateReminderEmailProperties(
        recipientIdentifier,
        operatorName,
        remindableProjects,
        emailLinkService.getWorkAreaUrl()
    );
  }
}