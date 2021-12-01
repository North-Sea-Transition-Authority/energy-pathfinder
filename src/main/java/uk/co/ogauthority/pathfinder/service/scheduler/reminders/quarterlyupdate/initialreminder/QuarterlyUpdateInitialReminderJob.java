package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.initialreminder;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
class QuarterlyUpdateInitialReminderJob extends QuartzJobBean {

  private final QuarterlyUpdateInitialReminderService quarterlyUpdateInitialReminderService;

  @Autowired
  QuarterlyUpdateInitialReminderJob(QuarterlyUpdateInitialReminderService quarterlyUpdateInitialReminderService) {
    this.quarterlyUpdateInitialReminderService = quarterlyUpdateInitialReminderService;
  }

  @Override
  protected void executeInternal(@NonNull JobExecutionContext context) {
    quarterlyUpdateInitialReminderService.sendInitialQuarterlyReminder();
  }
}
