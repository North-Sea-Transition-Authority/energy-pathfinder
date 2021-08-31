package uk.co.ogauthority.pathfinder.service.scheduler;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;

import java.util.Map;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.exception.JobSchedulingException;

@Service
public class SchedulerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerService.class);

  static final String IMMEDIATE_TRIGGER_SUFFIX = "_IMMEDIATE_TRIGGER";

  private final Scheduler scheduler;

  @Autowired
  public SchedulerService(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  /**
   * Schedule a job which starts immediately.
   * @param jobIdentifier A unique identifier for the job
   * @param jobDataMap A Map containing data required for the job
   * @param pathfinderJobClass A bean extending QuartzBeanClass to execute when the trigger fires
   */
  public void scheduleJobImmediately(String jobIdentifier,
                                     Map<? extends String, ?> jobDataMap,
                                     Class<? extends QuartzJobBean> pathfinderJobClass) {

    var jobDetail = buildDefaultJobDetail(jobKey(jobIdentifier), pathfinderJobClass);

    jobDetail.getJobDataMap().putAll(jobDataMap);

    var trigger = TriggerBuilder.newTrigger()
        .withIdentity(String.format("%s%s", jobIdentifier, IMMEDIATE_TRIGGER_SUFFIX))
        .startNow()
        .build();

    scheduleJob(jobDetail, trigger);
  }

  /**
   * Schedule a job if a job doesn't already exist with the same jobKey. The job will
   * be fired according to the provided cronSchedule.
   * @param jobKey A unique identifier for the job
   * @param triggerKey A unique key for the job trigger
   * @param pathfinderJobClass A bean extending QuartzBeanClass to execute when the trigger fires
   * @param cronSchedule A cron schedule representing the frequency of the job execution
   */
  public void scheduleJobIfNoJobExists(JobKey jobKey,
                                       TriggerKey triggerKey,
                                       Class<? extends QuartzJobBean> pathfinderJobClass,
                                       String cronSchedule) {

    var jobDetail = buildDefaultJobDetail(jobKey, pathfinderJobClass);

    var trigger = TriggerBuilder
        .newTrigger()
        .withIdentity(triggerKey)
        .withSchedule(CronScheduleBuilder.cronSchedule(cronSchedule))
        .build();

    scheduleJobIfNoJobExists(jobDetail, trigger);
  }

  /**
   * Schedule a job if a job doesn't already exist with the same jobKey. The job will
   * be fired according to the provided cronSchedule.
   * @param jobDetail The details of the job to fire
   * @param trigger The trigger to fire the job against
   */
  public void scheduleJobIfNoJobExists(JobDetail jobDetail, Trigger trigger) {

    var jobKey = jobDetail.getKey();
    var jobKeyString = jobKeyToString(jobKey);

    if (doesJobWithKeyExist(jobDetail.getKey())) {

      LOGGER.info(
          "Job {}, already exist. Job creation not required.",
          jobKeyString
      );

    } else {

      LOGGER.info(
          "Job {}, does not exist. Starting job creation...",
          jobKeyString
      );

      scheduleJob(jobDetail, trigger);

      LOGGER.info(
          "Job {} created successfully",
          jobKeyString
      );
    }
  }

  /**
   * Method to determine if a job with the provided JobKey already exists.
   * @param jobKey The job key to check exists
   * @return true if the jobKey exists, false otherwise
   */
  boolean doesJobWithKeyExist(JobKey jobKey) {
    try {
      return scheduler.checkExists(jobKey);
    } catch (SchedulerException se) {
      return false;
    }
  }

  /**
   * Method to schedule a job with the provided detail and trigger.
   * @param jobDetail The details of the job to fire
   * @param trigger The trigger to fire the job against
   */
  void scheduleJob(JobDetail jobDetail, Trigger trigger) {
    try {
      scheduler.scheduleJob(jobDetail, trigger);
    } catch (SchedulerException ex) {
      throw new JobSchedulingException(String.format("Error scheduling job with key %s", jobDetail.getKey()), ex);
    }
  }

  private String jobKeyToString(JobKey jobKey) {
    return String.format(
        "[name=%s, group=%s]",
        jobKey.getName(),
        jobKey.getGroup()
    );
  }

  private JobDetail buildDefaultJobDetail(JobKey jobKey,
                                          Class<? extends QuartzJobBean> pathfinderJobClass) {
    return newJob(pathfinderJobClass)
        .withIdentity(jobKey)
        .requestRecovery()
        .storeDurably()
        .build();
  }
}
