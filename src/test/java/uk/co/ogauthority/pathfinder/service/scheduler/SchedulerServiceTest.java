package uk.co.ogauthority.pathfinder.service.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import uk.co.ogauthority.pathfinder.exception.JobSchedulingException;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerServiceTest {

  @Mock
  private Scheduler scheduler;

  @Captor
  private ArgumentCaptor<Trigger> triggerArgumentCaptor;

  private final static String JOB_IDENTIFIER = "JOB_IDENTIFIER";

  private final static String TRIGGER_IDENTIFIER = "TRIGGER_IDENTIFIER";

  private SchedulerService schedulerService;

  @Before
  public void setup() {
    schedulerService = new SchedulerService(scheduler);
  }

  @Test
  public void scheduleJobImmediately_verifyInteractions() throws SchedulerException {

    final var jobData = getTestJobDataMap();

    schedulerService.scheduleJobImmediately(JOB_IDENTIFIER, jobData, TestJob.class);

    var expectedJobDetail = getTestJobDetail(jobData);
    var expectedJobTrigger = getTestImmediateJobTrigger();

    verify(scheduler, times(1)).scheduleJob(
        eq(expectedJobDetail),
        triggerArgumentCaptor.capture()
    );

    assertThat(triggerArgumentCaptor.getValue()).isEqualTo(expectedJobTrigger);
  }

  @Test
  public void scheduleJobIfNoJobExists_cronScheduleVariation_whenJobNotExist_thenJobScheduled() throws SchedulerException {

    var jobData = getTestJobDataMap();

    var triggerKey = getTestTriggerKey();

    var jobKey = getTestJobKey();

    var cronSchedule = "0 0 09 01 * ?";

    var expectedJobDetail = getTestJobDetail(jobData);

    var expectedTrigger = TriggerBuilder
        .newTrigger()
        .withIdentity(triggerKey)
        .withSchedule(CronScheduleBuilder.cronSchedule(cronSchedule))
        .build();

    when(scheduler.checkExists(jobKey)).thenReturn(false);

    schedulerService.scheduleJobIfNoJobExists(
        getTestJobKey(),
        triggerKey,
        TestJob.class,
        cronSchedule
    );

    verify(scheduler, times(1)).scheduleJob(
        eq(expectedJobDetail),
        triggerArgumentCaptor.capture()
    );

    assertThat(triggerArgumentCaptor.getValue()).isEqualTo(expectedTrigger);
  }

  @Test
  public void scheduleJobIfNoJobExists_cronScheduleVariation_whenJobExist_thenNoJobScheduled() throws SchedulerException {

    var triggerKey = getTestTriggerKey();

    var jobKey = getTestJobKey();

    var cronSchedule = "0 0 09 01 * ?";

    var currentCronTrigger = TriggerBuilder.newTrigger()
        .withIdentity(triggerKey)
        .withSchedule(CronScheduleBuilder.cronSchedule(cronSchedule))
        .build();

    @SuppressWarnings("rawtypes")
    List triggers = List.of(currentCronTrigger);

    when(scheduler.checkExists(jobKey)).thenReturn(true);
    when(scheduler.getTriggersOfJob(jobKey)).thenReturn(triggers);

    schedulerService.scheduleJobIfNoJobExists(
        jobKey,
        triggerKey,
        TestJob.class,
        cronSchedule
    );

    verify(scheduler).rescheduleJob(
        eq(triggerKey),
        assertArg(trigger -> assertThat(trigger.getKey()).isEqualTo(triggerKey))
    );

    verify(scheduler, never()).scheduleJob(any(), any());
  }

  @Test
  public void scheduleJobIfNoJobExists_triggerVariation_whenJobNotExist_thenJobScheduled() throws SchedulerException {

    var jobDetail = getTestJobDetail(Map.of());

    var trigger = getTestImmediateJobTrigger();

    var jobKey = getTestJobKey();
    when(scheduler.checkExists(jobKey)).thenReturn(false);

    schedulerService.scheduleJobIfNoJobExists(
        jobDetail,
        trigger
    );

    verify(scheduler, times(1)).scheduleJob(jobDetail, trigger);
  }

  @Test
  public void scheduleJobIfNoJobExists_triggerVariation_whenJobExist_thenJobRescheduled() throws SchedulerException {
    var jobDetail = getTestJobDetail(Map.of());
    var trigger = getTestImmediateJobTrigger();
    var jobKey = getTestJobKey();

    var currentCronTrigger = TriggerBuilder.newTrigger()
        .withIdentity(trigger.getKey())
        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 9 * * ?"))
        .build();

    @SuppressWarnings("rawtypes")
    List triggers = List.of(currentCronTrigger);

    when(scheduler.checkExists(jobKey)).thenReturn(true);
    when(scheduler.getTriggersOfJob(jobKey)).thenReturn(triggers);

    schedulerService.scheduleJobIfNoJobExists(
        jobDetail,
        trigger
    );

    verify(scheduler).rescheduleJob(
        eq(trigger.getKey()),
        assertArg(capturedTrigger -> assertThat(trigger.getKey()).isEqualTo(capturedTrigger.getKey()))
    );

    verify(scheduler, never()).scheduleJob(any(), any());
  }

  @Test
  public void scheduleJob_withJobDetailAndTrigger_verifyInteractions() throws SchedulerException {

    final var jobDetail = getTestJobDetail(Map.of());
    final var trigger = getTestImmediateJobTrigger();

    schedulerService.scheduleJob(jobDetail, trigger);

    verify(scheduler, times(1)).scheduleJob(jobDetail, trigger);
  }

  @Test(expected = JobSchedulingException.class)
  public void scheduleJob_withJobDetailAndTrigger_whenExceptionThrown_verifyInteractions() throws SchedulerException {

    final var jobDetail = getTestJobDetail(Map.of());
    final var trigger = getTestImmediateJobTrigger();

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

  private JobDetail getTestJobDetail(Map<String, Object> jobData) {

    final var jobDetail = newJob(TestJob.class)
        .withIdentity(JOB_IDENTIFIER)
        .requestRecovery()
        .storeDurably()
        .build();

    jobDetail.getJobDataMap()
        .putAll(jobData);

    return jobDetail;
  }

  private Trigger getTestImmediateJobTrigger() {
    return TriggerBuilder.newTrigger()
        .withIdentity(String.format("%s%s", JOB_IDENTIFIER, SchedulerService.IMMEDIATE_TRIGGER_SUFFIX))
        .startNow()
        .build();
  }

  private JobKey getTestJobKey() {
    return jobKey(JOB_IDENTIFIER);
  }

  private TriggerKey getTestTriggerKey() {
    return TriggerKey.triggerKey(TRIGGER_IDENTIFIER);
  }

  private Map<String, Object> getTestJobDataMap() {
    var jobData = new HashMap<String, Object>();
    jobData.put("VALUE", 1);
    return jobData;
  }

  static class TestJob extends QuartzJobBean {
    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {}
  }


}