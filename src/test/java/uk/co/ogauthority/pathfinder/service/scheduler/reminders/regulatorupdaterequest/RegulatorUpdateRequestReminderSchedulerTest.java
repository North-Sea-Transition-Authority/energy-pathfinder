package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.CronExpression;
import uk.co.ogauthority.pathfinder.service.scheduler.JobRegistrationService;

@RunWith(MockitoJUnitRunner.class)
public class RegulatorUpdateRequestReminderSchedulerTest {

  @Mock
  private JobRegistrationService jobRegistrationService;

  private RegulatorUpdateRequestReminderScheduler regulatorUpdateRequestReminderScheduler;

  @Before
  public void setup() {
    this.regulatorUpdateRequestReminderScheduler = new RegulatorUpdateRequestReminderScheduler(
        jobRegistrationService
    );
  }

  @Test
  public void registerJob_verifyInteractions() {

    regulatorUpdateRequestReminderScheduler.registerJob();

    verify(jobRegistrationService, times(1)).registerReoccurringSchedulerJob(
        RegulatorUpdateRequestReminderScheduler.JOB_DOMAIN_PREFIX,
        RegulatorUpdateRequestReminderJob.class,
        RegulatorUpdateRequestReminderScheduler.EVERY_DAY_AT_9AM
    );
  }

  @Test
  public void verifyCronExpression() throws ParseException {

    var currentYear = LocalDate.now().getYear();

    // given any date after 9am
    var lastExecutionDateTime = LocalDateTime.of(currentYear, 1, 1, 10, 0, 0);

    // expect the next schedule date to be the next day at 9am
    var expectedNextScheduleDate = LocalDateTime.of(currentYear, 1, 2, 9, 0, 0);

    var cronExpressionToTest = new CronExpression(RegulatorUpdateRequestReminderScheduler.EVERY_DAY_AT_9AM);

    var resultingNextScheduleDate = cronExpressionToTest.getNextValidTimeAfter(Timestamp.valueOf(lastExecutionDateTime))
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();

    assertThat(resultingNextScheduleDate).isEqualTo(expectedNextScheduleDate);
  }

}