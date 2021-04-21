package uk.co.ogauthority.pathfinder.service.scheduler.newsletters;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerKey.triggerKey;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobKey;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.service.scheduler.SchedulerService;

@Service
public class MonthlyNewsletterScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(MonthlyNewsletterScheduler.class);
  private static final JobKey JOB_KEY = jobKey("MONTHLY_NEWSLETTER_JOB", "NEWSLETTER_SCHEDULER_JOBS");
  private static final TriggerKey TRIGGER_KEY = triggerKey("MONTHLY_NEWSLETTER_JOB_TRIGGER_KEY", "NEWSLETTER_SCHEDULER_TRIGGERS");
  private static final String FIRST_OF_EVERY_MONTH_AT_6AM = "0 0 06 01 * ?";

  private final SchedulerService schedulerService;

  @Autowired
  public MonthlyNewsletterScheduler(SchedulerService schedulerService) {
    this.schedulerService = schedulerService;
  }

  @EventListener(classes = ApplicationReadyEvent.class)
  public void registerJob() {
    if (schedulerService.doesJobWithKeyExist(JOB_KEY)) {
      LOGGER.info("Monthly newsletter sending job found");
    } else {
      LOGGER.info("Monthly newsletter job does not exist. Creating...");
      var jobDetail = newJob(MonthlyNewsletterJob.class)
          .withIdentity(JOB_KEY)
          .requestRecovery()
          .storeDurably()
          .build();

      var trigger = TriggerBuilder
          .newTrigger()
          .withIdentity(TRIGGER_KEY)
          .withSchedule(CronScheduleBuilder.cronSchedule(FIRST_OF_EVERY_MONTH_AT_6AM))
          .build();

      schedulerService.scheduleJob(jobDetail, trigger);

      LOGGER.info("Monthly newsletter job creation complete");
    }
  }
}
