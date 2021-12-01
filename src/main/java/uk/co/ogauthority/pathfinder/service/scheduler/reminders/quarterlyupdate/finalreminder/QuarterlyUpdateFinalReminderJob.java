package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.finalreminder;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
class QuarterlyUpdateFinalReminderJob extends QuartzJobBean {

  private final QuarterlyUpdateFinalReminderService quarterlyUpdateFinalReminderService;

  @Autowired
  QuarterlyUpdateFinalReminderJob(QuarterlyUpdateFinalReminderService quarterlyUpdateFinalReminderService) {
    this.quarterlyUpdateFinalReminderService = quarterlyUpdateFinalReminderService;
  }

  @Override
  protected void executeInternal(@NonNull JobExecutionContext context) {
    quarterlyUpdateFinalReminderService.sendFinalQuarterlyUpdateReminder();
  }
}
