package uk.co.ogauthority.pathfinder.service.scheduler;

import org.quartz.JobExecutionContext;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class TestJobBean extends QuartzJobBean {

  @Override
  protected void executeInternal(@NonNull JobExecutionContext context) {
    //TODO PAT_172: implement business logic
  }
}
