package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class RegulatorUpdateRequestReminderJob extends QuartzJobBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(RegulatorUpdateRequestReminderJob.class);
  private final RegulatorUpdateRequestReminderService regulatorUpdateRequestReminderService;

  public RegulatorUpdateRequestReminderJob(RegulatorUpdateRequestReminderService regulatorUpdateRequestReminderService) {
    this.regulatorUpdateRequestReminderService = regulatorUpdateRequestReminderService;
  }


  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    try {
      var projectId = context.getJobDetail().getJobDataMap().getInt("projectId");
      executeJob(projectId);
    } catch (Exception e) {
      LOGGER.error("Regulator update request reminder Job execution failed", e);
      throw new JobExecutionException(e);
    }
  }

  private void executeJob(int projectId) {
    regulatorUpdateRequestReminderService.sendReminderEmail(projectId);
  }
}
