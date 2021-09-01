package uk.co.ogauthority.pathfinder.service.scheduler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerKey.triggerKey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

@RunWith(MockitoJUnitRunner.class)
public class JobRegistrationServiceTest {

  private static final String CRON_EXPRESSION = "* * * * *";
  private static final String JOB_PREFIX = "job_prefix";
  private static final String TRIGGER_PREFIX = "trigger_prefix";

  @Mock
  private SchedulerService schedulerService;

  private JobRegistrationService jobRegistrationService;

  @Before
  public void setup() {
    jobRegistrationService = new JobRegistrationService(
        schedulerService
    );
  }

  @Test
  public void registerReoccurringSchedulerJob_jobPrefixOnlyVariant_verifyInteractions() {

    jobRegistrationService.registerReoccurringSchedulerJob(
        JOB_PREFIX,
        TestJobBean.class,
        CRON_EXPRESSION
    );

    verify(schedulerService, times(1)).scheduleJobIfNoJobExists(
        getJobKey(),
        getTriggerKey(JOB_PREFIX),
        TestJobBean.class,
        CRON_EXPRESSION
    );
  }

  @Test
  public void registerReoccurringSchedulerJob_jobAndTriggerPrefixVariant_verifyInteractions() {

    jobRegistrationService.registerReoccurringSchedulerJob(
        JOB_PREFIX,
        TRIGGER_PREFIX,
        TestJobBean.class,
        CRON_EXPRESSION
    );

    verify(schedulerService, times(1)).scheduleJobIfNoJobExists(
        getJobKey(),
        getTriggerKey(TRIGGER_PREFIX),
        TestJobBean.class,
        CRON_EXPRESSION
    );
  }

  private JobKey getJobKey() {
    return jobKey(getJobName(), getJobGroupName());
  }

  private String getJobName() {
    return String.format("%s%s", JOB_PREFIX, JobRegistrationService.JOB_NAME_SUFFIX);
  }

  private String getJobGroupName() {
    return String.format("%s%s", JOB_PREFIX, JobRegistrationService.JOB_GROUP_NAME_SUFFIX);
  }

  private TriggerKey getTriggerKey(String triggerPrefix) {
    return triggerKey(getTriggerName(triggerPrefix), getTriggerGroupName(triggerPrefix));
  }

  private String getTriggerName(String triggerPrefix) {
    return String.format("%s%s", triggerPrefix, JobRegistrationService.TRIGGER_NAME_SUFFIX);
  }

  private String getTriggerGroupName(String triggerPrefix) {
    return String.format("%s%s", triggerPrefix, JobRegistrationService.TRIGGER_GROUP_NAME_SUFFIX);
  }

}