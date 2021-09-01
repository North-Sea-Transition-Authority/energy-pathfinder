package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
class RegulatorUpdateRequestReminderJob extends QuartzJobBean {

  private final RegulatorUpdateReminderService regulatorUpdateReminderService;

  @Autowired
  RegulatorUpdateRequestReminderJob(RegulatorUpdateReminderService regulatorUpdateReminderService) {
    this.regulatorUpdateReminderService = regulatorUpdateReminderService;
  }

  @Override
  protected void executeInternal(@NonNull JobExecutionContext context) {
    regulatorUpdateReminderService.processDueReminders();
  }
}
