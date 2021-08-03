package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest;

import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.ProjectUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.scheduler.ReminderType;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.PathfinderReminderScheduler;

@Service
public class RegulatorUpdateRequestReminderService {

  public static final String JOB_GROUP_NAME = "REGULATOR_UPDATE_REMINDER_GROUP";
  public static final String TRIGGER_GROUP_NAME = "REGULATOR_UPDATE_REMINDER_TRIGGER";

  //TODO PAT-172 replace dummy service with actual service
  private final PathfinderReminderScheduler pathfinderReminderScheduler;
  private final EmailLinkService emailLinkService;
  private final EmailService emailService;

  public RegulatorUpdateRequestReminderService(PathfinderReminderScheduler pathfinderReminderScheduler,
                                               EmailLinkService emailLinkService,
                                               EmailService emailService) {
    this.pathfinderReminderScheduler = pathfinderReminderScheduler;
    this.emailLinkService = emailLinkService;
    this.emailService = emailService;
  }

  public void scheduleUpdateReminderJob(ProjectDetail detail) throws SchedulerException {
    pathfinderReminderScheduler.scheduleReminder(
        detail,
        ReminderType.REGULATOR_UPDATE_REQUEST_DEADLINE_REMINDER,
        RegulatorUpdateRequestReminderJob.class
    );
    pathfinderReminderScheduler.scheduleReminder(
        detail,
        ReminderType.REGULATOR_UPDATE_REQUEST_AFTER_DEADLINE_REMINDER,
        RegulatorUpdateRequestReminderJob.class //This should have it's own class
    );
    pathfinderReminderScheduler.unscheduleReminder(detail, ReminderType.REGULATOR_UPDATE_REQUEST_DEADLINE_REMINDER);
  }

  void sendReminderEmail(int projectId) {
    var properties = new ProjectUpdateEmailProperties(
        emailLinkService.getWorkAreaUrl()
    );
    emailService.sendEmail(properties, "dummy@address.com");
  }

}
