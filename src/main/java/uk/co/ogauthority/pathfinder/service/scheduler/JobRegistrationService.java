package uk.co.ogauthority.pathfinder.service.scheduler;

import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerKey.triggerKey;

import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
public class JobRegistrationService {

  static final String JOB_NAME_SUFFIX = "_JOB";
  static final String JOB_GROUP_NAME_SUFFIX = "_JOB_GROUP";

  static final String TRIGGER_NAME_SUFFIX = "_TRIGGER";
  static final String TRIGGER_GROUP_NAME_SUFFIX = "_TRIGGER_GROUP";

  private final SchedulerService schedulerService;

  @Autowired
  public JobRegistrationService(SchedulerService schedulerService) {
    this.schedulerService = schedulerService;
  }

  public void registerReoccurringSchedulerJob(String jobDomainPrefix,
                                              Class<? extends QuartzJobBean> jobClass,
                                              String cronScheduleExpression) {
    registerReoccurringSchedulerJob(
        jobDomainPrefix,
        jobDomainPrefix,
        jobClass,
        cronScheduleExpression
    );
  }

  public void registerReoccurringSchedulerJob(String jobDomainPrefix,
                                              String triggerDomainPrefix,
                                              Class<? extends QuartzJobBean> jobClass,
                                              String cronScheduleExpression) {
    schedulerService.scheduleJobIfNoJobExists(
        getJobKey(jobDomainPrefix),
        getTriggerKey(triggerDomainPrefix),
        jobClass,
        cronScheduleExpression
    );
  }

  private JobKey getJobKey(String jobPrefix) {
    return jobKey(getJobName(jobPrefix), getJobGroupName(jobPrefix));
  }

  private TriggerKey getTriggerKey(String triggerPrefix) {
    return triggerKey(getTriggerName(triggerPrefix), getTriggerGroupName(triggerPrefix));
  }

  private String getJobName(String jobPrefix) {
    return String.format("%s%s", jobPrefix, JOB_NAME_SUFFIX);
  }

  private String getJobGroupName(String jobPrefix) {
    return String.format("%s%s", jobPrefix, JOB_GROUP_NAME_SUFFIX);
  }

  private String getTriggerName(String triggerPrefix) {
    return String.format("%s%s", triggerPrefix, TRIGGER_NAME_SUFFIX);
  }

  private String getTriggerGroupName(String triggerPrefix) {
    return String.format("%s%s", triggerPrefix, TRIGGER_GROUP_NAME_SUFFIX);
  }
}
