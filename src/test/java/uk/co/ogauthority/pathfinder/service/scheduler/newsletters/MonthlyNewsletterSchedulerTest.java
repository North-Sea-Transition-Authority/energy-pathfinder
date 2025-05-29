package uk.co.ogauthority.pathfinder.service.scheduler.newsletters;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.service.scheduler.SchedulerService;

@RunWith(MockitoJUnitRunner.class)
public class MonthlyNewsletterSchedulerTest {

  private final String cron = "0 0 09 01 * ?";

  @Mock
  private SchedulerService schedulerService;

  private MonthlyNewsletterScheduler monthlyNewsletterScheduler;

  @Before
  public void setup() {
    monthlyNewsletterScheduler = new MonthlyNewsletterScheduler(schedulerService, cron);
  }

  @Test
  public void registerJob_verifyInteractions() {

    monthlyNewsletterScheduler.registerJob();

    verify(schedulerService, times(1)).scheduleJobIfNoJobExists(
        MonthlyNewsletterScheduler.JOB_KEY,
        MonthlyNewsletterScheduler.TRIGGER_KEY,
        MonthlyNewsletterJob.class,
        cron
    );
  }
}