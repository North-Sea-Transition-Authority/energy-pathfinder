package uk.co.ogauthority.pathfinder.service.scheduler.communication;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationSendingService;

@Component
public class CommunicationJob extends QuartzJobBean {

  private final CommunicationSendingService communicationSendingService;

  @Autowired
  public CommunicationJob(CommunicationSendingService communicationSendingService) {
    this.communicationSendingService = communicationSendingService;
  }

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

    Integer communicationId = null;

    try {
      communicationId = context.getJobDetail().getJobDataMap().getInt("communicationId");
      communicationSendingService.sendCommunication(communicationId);
    } catch (Exception e) {
      final var errorMessage = String.format(
          "Communication job execution failed for communication with id: %d",
          communicationId
      );
      throw new JobExecutionException(errorMessage, e);
    }
  }
}
