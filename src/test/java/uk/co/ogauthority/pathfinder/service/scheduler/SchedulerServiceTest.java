package uk.co.ogauthority.pathfinder.service.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerServiceTest {

  @Mock
  private Scheduler scheduler;

  private SchedulerService schedulerService;

  @Before
  public void setup() {
    schedulerService = new SchedulerService(scheduler);
  }

  @Test
  public void scheduleJob_verifyInteractions() throws SchedulerException {
    final var jobKey = "JOB_KEY";

    var jobData = new HashMap<String, Object>();
    jobData.put("VALUE", 1);

    schedulerService.scheduleJob(jobKey, jobData, TestJob.class);

    verify(scheduler, times(1)).scheduleJob(any(), any());
  }

  static class TestJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) {}
  }


}