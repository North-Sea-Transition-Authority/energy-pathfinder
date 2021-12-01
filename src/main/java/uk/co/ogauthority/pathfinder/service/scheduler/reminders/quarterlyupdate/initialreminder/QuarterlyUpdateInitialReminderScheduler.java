package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.initialreminder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.service.scheduler.JobRegistrationService;

@Service
class QuarterlyUpdateInitialReminderScheduler {

  static final String JOB_DOMAIN_PREFIX = "QUARTERLY_UPDATE_INITIAL_REMINDER";

  static final String FIRST_OF_EVERY_QUARTER_AT_9AM = "0 0 09 01 */3 ?";

  private final JobRegistrationService jobRegistrationService;

  @Autowired
  QuarterlyUpdateInitialReminderScheduler(JobRegistrationService jobRegistrationService) {
    this.jobRegistrationService = jobRegistrationService;
  }

  @EventListener(classes = ApplicationReadyEvent.class)
  public void registerJob() {
    jobRegistrationService.registerReoccurringSchedulerJob(
        JOB_DOMAIN_PREFIX,
        QuarterlyUpdateInitialReminderJob.class,
        FIRST_OF_EVERY_QUARTER_AT_9AM
    );
  }
}
