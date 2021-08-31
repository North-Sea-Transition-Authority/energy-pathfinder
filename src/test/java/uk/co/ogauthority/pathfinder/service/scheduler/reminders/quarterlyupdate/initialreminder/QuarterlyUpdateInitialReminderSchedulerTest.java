package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.initialreminder;

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
import uk.co.ogauthority.pathfinder.service.scheduler.SchedulerService;

@RunWith(MockitoJUnitRunner.class)
public class QuarterlyUpdateInitialReminderSchedulerTest {

  @Mock
  private SchedulerService schedulerService;

  private QuarterlyUpdateInitialReminderScheduler quarterlyUpdateInitialReminderScheduler;

  @Before
  public void setup() {
    quarterlyUpdateInitialReminderScheduler = new QuarterlyUpdateInitialReminderScheduler(schedulerService);
  }

  @Test
  public void registerJob_verifyInteractions() {

    quarterlyUpdateInitialReminderScheduler.registerJob();

    verify(schedulerService, times(1)).scheduleJobIfNoJobExists(
        QuarterlyUpdateInitialReminderScheduler.JOB_KEY,
        QuarterlyUpdateInitialReminderScheduler.TRIGGER_KEY,
        QuarterlyUpdateInitialReminderJob.class,
        QuarterlyUpdateInitialReminderScheduler.FIRST_OF_EVERY_QUARTER_AT_9AM
    );
  }

  @Test
  public void verifyCronExpression() throws ParseException {

    var currentYear = LocalDate.now().getYear();

    // given a date in Q1
    var q1Date = LocalDate.of(currentYear, 2, 1);

    // expect the next schedule date to be the start of Q2 at 9am
    var expectedNextScheduleDate = LocalDateTime.of(currentYear, 4, 1, 9, 0, 0);

    var cronExpressionToTest = new CronExpression(QuarterlyUpdateInitialReminderScheduler.FIRST_OF_EVERY_QUARTER_AT_9AM);

    var resultingNextScheduleDate = cronExpressionToTest.getNextValidTimeAfter(Date.valueOf(q1Date))
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();

    assertThat(resultingNextScheduleDate).isEqualTo(expectedNextScheduleDate);
  }

}