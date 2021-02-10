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
    var projectId = context.getJobDetail().getJobDataMap().getInt("projectId");
    try {
      executeJob(projectId);
    } catch (Exception e) {
      final var errorMessage = String.format(
          "Regulator update request reminder Job execution failed for project with id: %d",
          projectId
      );
      LOGGER.error(errorMessage, e);
      throw new JobExecutionException(errorMessage, e);
    }
  }

  private void executeJob(int projectId) {
    regulatorUpdateRequestReminderService.sendReminderEmail(projectId);
  }
}
