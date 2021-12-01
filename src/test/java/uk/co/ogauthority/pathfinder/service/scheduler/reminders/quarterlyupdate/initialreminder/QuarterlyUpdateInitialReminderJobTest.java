package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.initialreminder;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.service.scheduler.TestJobExecutionContext;

@RunWith(MockitoJUnitRunner.class)
public class QuarterlyUpdateInitialReminderJobTest {

  @Mock
  private QuarterlyUpdateInitialReminderService quarterlyUpdateInitialReminderService;

  private QuarterlyUpdateInitialReminderJob quarterlyUpdateInitialReminderJob;

  @Before
  public void setup() {
    quarterlyUpdateInitialReminderJob = new QuarterlyUpdateInitialReminderJob(quarterlyUpdateInitialReminderService);
  }

  @Test
  public void executeInternal_verifyInteractions() {
    quarterlyUpdateInitialReminderJob.executeInternal(new TestJobExecutionContext());
    verify(quarterlyUpdateInitialReminderService, times(1)).sendInitialQuarterlyReminder();
  }
}