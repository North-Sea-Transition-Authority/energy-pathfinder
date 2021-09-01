package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.finalreminder;

import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerKey.triggerKey;

import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.service.scheduler.SchedulerService;

@Service
class QuarterlyUpdateFinalReminderScheduler {

  private static final String JOB_DOMAIN_PREFIX = "QUARTERLY_UPDATE_FINAL_REMINDER";

  private static final String JOB_NAME = String.format("%s_JOB", JOB_DOMAIN_PREFIX);
  private static final String JOB_GROUP_NAME = String.format("%s_JOB_GROUP", JOB_DOMAIN_PREFIX);

  private static final String TRIGGER_NAME = String.format("%s_TRIGGER", JOB_DOMAIN_PREFIX);
  private static final String TRIGGER_GROUP_NAME = String.format("%s_TRIGGER_GROUP", JOB_DOMAIN_PREFIX);

  static final JobKey JOB_KEY = jobKey(JOB_NAME, JOB_GROUP_NAME);
  static final TriggerKey TRIGGER_KEY = triggerKey(TRIGGER_NAME, TRIGGER_GROUP_NAME);

  static final String FIRST_DAY_OF_LAST_MONTH_OF_QUARTER_AT_9AM = "0 0 09 01 3/3 ?";

  private final SchedulerService schedulerService;

  @Autowired
  QuarterlyUpdateFinalReminderScheduler(SchedulerService schedulerService) {
    this.schedulerService = schedulerService;
  }

  @EventListener(classes = ApplicationReadyEvent.class)
  public void registerJob() {
    schedulerService.scheduleJobIfNoJobExists(
        JOB_KEY,
        TRIGGER_KEY,
        QuarterlyUpdateFinalReminderJob.class,
        FIRST_DAY_OF_LAST_MONTH_OF_QUARTER_AT_9AM
    );
  }
}
