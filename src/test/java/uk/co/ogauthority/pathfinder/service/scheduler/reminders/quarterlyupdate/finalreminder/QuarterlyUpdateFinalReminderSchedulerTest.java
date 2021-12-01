package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.finalreminder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.Date;
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
public class QuarterlyUpdateFinalReminderSchedulerTest {

  @Mock
  private JobRegistrationService jobRegistrationService;

  private QuarterlyUpdateFinalReminderScheduler quarterlyUpdateFinalReminderScheduler;

  @Before
  public void setup() {
    quarterlyUpdateFinalReminderScheduler = new QuarterlyUpdateFinalReminderScheduler(
        jobRegistrationService
    );
  }

  @Test
  public void registerJob_verifyInteractions() {

    quarterlyUpdateFinalReminderScheduler.registerJob();

    verify(jobRegistrationService, times(1)).registerReoccurringSchedulerJob(
        QuarterlyUpdateFinalReminderScheduler.JOB_DOMAIN_PREFIX,
        QuarterlyUpdateFinalReminderJob.class,
        QuarterlyUpdateFinalReminderScheduler.FIRST_DAY_OF_LAST_MONTH_OF_QUARTER_AT_9AM
    );
  }

  @Test
  public void verifyCronExpression() throws ParseException {

    var currentYear = LocalDate.now().getYear();

    // given a date in the first month of Q1
    var startOfQ1Date = LocalDate.of(currentYear, 1, 1);

    // expect the next schedule date to be the start of the final month of Q1 at 9am
    var expectedNextScheduleDate = LocalDateTime.of(currentYear, 3, 1, 9, 0, 0);

    var cronExpressionToTest = new CronExpression(QuarterlyUpdateFinalReminderScheduler.FIRST_DAY_OF_LAST_MONTH_OF_QUARTER_AT_9AM);

    var resultingNextScheduleDate = cronExpressionToTest.getNextValidTimeAfter(Date.valueOf(startOfQ1Date))
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();

    assertThat(resultingNextScheduleDate).isEqualTo(expectedNextScheduleDate);
  }

}