package uk.co.ogauthority.pathfinder.service.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.scheduling.quartz.QuartzJobBean;
import uk.co.ogauthority.pathfinder.exception.JobSchedulingException;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerServiceTest {

  @Mock
  private Scheduler scheduler;

  private final static String JOB_IDENTIFIER = "JOB_IDENTIFIER";

  private SchedulerService schedulerService;

  @Before
  public void setup() {
    schedulerService = new SchedulerService(scheduler);
  }

  @Test
  public void scheduleJob_verifyInteractions() throws SchedulerException {
    final var jobData = getTestJobDataMap();
    schedulerService.scheduleJob(JOB_IDENTIFIER, jobData, TestJob.class);
    verify(scheduler, times(1)).scheduleJob(any(), any());
  }

  @Test
  public void scheduleJob_withJobDetailAndTrigger_verifyInteractions() throws SchedulerException {

    final var jobDetail = getTestJobDetail();
    final var trigger = getTestJobTrigger();

    schedulerService.scheduleJob(jobDetail, trigger);

    verify(scheduler, times(1)).scheduleJob(jobDetail, trigger);
  }

  @Test(expected = JobSchedulingException.class)
  public void scheduleJob_withJobDetailAndTrigger_whenExceptionThrown_verifyInteractions() throws SchedulerException {

    final var jobDetail = getTestJobDetail();
    final var trigger = getTestJobTrigger();

    when(scheduler.scheduleJob(jobDetail, trigger)).thenThrow(new SchedulerException());

    schedulerService.scheduleJob(jobDetail, trigger);

    verify(scheduler, times(1)).scheduleJob(jobDetail, trigger);
  }

  @Test
  public void doesJobWithKeyExist_whenExists_thenTrue() throws SchedulerException {
    final var jobKey = getTestJobKey();
    when(scheduler.checkExists(jobKey)).thenReturn(true);
    final var result = schedulerService.doesJobWithKeyExist(jobKey);
    assertThat(result).isTrue();
  }

  @Test
  public void doesJobWithKeyExist_whenDoesNotExists_thenFalse() throws SchedulerException {
    final var jobKey = getTestJobKey();
    when(scheduler.checkExists(jobKey)).thenReturn(false);
    final var result = schedulerService.doesJobWithKeyExist(jobKey);
    assertThat(result).isFalse();
  }

  @Test
  public void doesJobWithKeyExist_whenExceptionThrown_thenFalse() throws SchedulerException {
    final var jobKey = getTestJobKey();
    when(scheduler.checkExists(jobKey)).thenThrow(new SchedulerException());
    final var result = schedulerService.doesJobWithKeyExist(jobKey);
    assertThat(result).isFalse();
  }

  private JobDetail getTestJobDetail() {

    final var jobDetail = newJob(TestJob.class)
        .withIdentity(JOB_IDENTIFIER)
        .requestRecovery()
        .storeDurably()
        .build();

    final var jobData = getTestJobDataMap();
    jobDetail.getJobDataMap().putAll(jobData);

    return jobDetail;
  }

  private Trigger getTestJobTrigger() {
    return TriggerBuilder.newTrigger().startNow().build();
  }

  private JobKey getTestJobKey() {
    return jobKey(JOB_IDENTIFIER);
  }

  private Map<String, Object> getTestJobDataMap() {
    var jobData = new HashMap<String, Object>();
    jobData.put("VALUE", 1);
    return jobData;
  }

  static class TestJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) {}
  }


}