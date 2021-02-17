package uk.co.ogauthority.pathfinder.service.scheduler;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;

import java.util.Map;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {

  private final Scheduler scheduler;

  @Autowired
  public SchedulerService(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public void scheduleJob(String jobIdentifier,
                          Map<? extends String, ?> jobDataMap,
                          Class<? extends QuartzJobBean> pathfinderJobClass) {

    JobKey jobKey = jobKey(jobIdentifier);

    JobDetail jobDetail = newJob(pathfinderJobClass)
        .withIdentity(jobKey)
        .requestRecovery()
        .storeDurably()
        .build();

    jobDetail.getJobDataMap().putAll(jobDataMap);

    Trigger trigger = TriggerBuilder.newTrigger().startNow().build();

    scheduleJob(jobDetail, trigger);

  }

  private void scheduleJob(JobDetail jobDetail, Trigger trigger) {
    try {
      scheduler.scheduleJob(jobDetail, trigger);
    } catch (SchedulerException ex) {
      throw new RuntimeException(String.format("Error scheduling job with key %s", jobDetail.getKey()), ex);
    }
  }
}
