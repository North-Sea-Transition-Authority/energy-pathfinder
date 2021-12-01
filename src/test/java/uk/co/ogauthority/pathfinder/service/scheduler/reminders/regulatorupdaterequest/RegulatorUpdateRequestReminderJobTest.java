package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.service.scheduler.TestJobExecutionContext;

@RunWith(MockitoJUnitRunner.class)
public class RegulatorUpdateRequestReminderJobTest {

  @Mock
  private RegulatorUpdateReminderService regulatorUpdateReminderService;

  private RegulatorUpdateRequestReminderJob regulatorUpdateRequestReminderJob;

  @Before
  public void setup() {
    regulatorUpdateRequestReminderJob = new RegulatorUpdateRequestReminderJob(
        regulatorUpdateReminderService
    );
  }

  @Test
  public void executeInternal_verifyInteractions() {
    regulatorUpdateRequestReminderJob.executeInternal(new TestJobExecutionContext());
    verify(regulatorUpdateReminderService, times(1)).processDueReminders();
  }

}