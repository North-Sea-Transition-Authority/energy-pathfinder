package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.finalreminder;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.service.scheduler.TestJobExecutionContext;

@RunWith(MockitoJUnitRunner.class)
public class QuarterlyUpdateFinalReminderJobTest {

  @Mock
  private QuarterlyUpdateFinalReminderService quarterlyUpdateFinalReminderService;

  private QuarterlyUpdateFinalReminderJob quarterlyUpdateFinalReminderJob;

  @Before
  public void setup() {
    quarterlyUpdateFinalReminderJob = new QuarterlyUpdateFinalReminderJob(quarterlyUpdateFinalReminderService);
  }

  @Test
  public void executeInternal_verifyInteractions() {
    quarterlyUpdateFinalReminderJob.executeInternal(new TestJobExecutionContext());
    verify(quarterlyUpdateFinalReminderService, times(1)).sendFinalQuarterlyUpdateReminder();
  }

}