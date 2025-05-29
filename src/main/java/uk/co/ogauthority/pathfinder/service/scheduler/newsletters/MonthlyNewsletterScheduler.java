package uk.co.ogauthority.pathfinder.service.scheduler.newsletters;

import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerKey.triggerKey;

import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.service.scheduler.SchedulerService;

@Service
public class MonthlyNewsletterScheduler {

  static final JobKey JOB_KEY = jobKey("MONTHLY_NEWSLETTER_JOB", "NEWSLETTER_SCHEDULER_JOBS");
  static final TriggerKey TRIGGER_KEY = triggerKey("MONTHLY_NEWSLETTER_JOB_TRIGGER_KEY", "NEWSLETTER_SCHEDULER_TRIGGERS");

  private final SchedulerService schedulerService;
  private final String cron;

  @Autowired
  public MonthlyNewsletterScheduler(
      SchedulerService schedulerService,
      @Value("${monthly-newsletter.cron}") String cron
  ) {
    this.schedulerService = schedulerService;
    this.cron = cron;
  }

  @EventListener(classes = ApplicationReadyEvent.class)
  public void registerJob() {
    schedulerService.scheduleJobIfNoJobExists(
        JOB_KEY,
        TRIGGER_KEY,
        MonthlyNewsletterJob.class,
        cron
    );
  }
}
