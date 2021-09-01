package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.service.scheduler.JobRegistrationService;

@Service
class RegulatorUpdateRequestReminderScheduler {

  static final String JOB_DOMAIN_PREFIX = "REGULATOR_UPDATE_REQUEST_REMINDER";

  static final String EVERY_DAY_AT_9AM = "0 0 09 * * ?";

  private final JobRegistrationService jobRegistrationService;

  @Autowired
  RegulatorUpdateRequestReminderScheduler(JobRegistrationService jobRegistrationService) {
    this.jobRegistrationService = jobRegistrationService;
  }

  @EventListener(classes = ApplicationReadyEvent.class)
  public void registerJob() {
    jobRegistrationService.registerReoccurringSchedulerJob(
        JOB_DOMAIN_PREFIX,
        RegulatorUpdateRequestReminderJob.class,
        EVERY_DAY_AT_9AM
    );
  }
}
