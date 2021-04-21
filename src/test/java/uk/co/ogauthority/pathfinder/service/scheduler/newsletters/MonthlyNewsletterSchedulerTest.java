package uk.co.ogauthority.pathfinder.service.scheduler.newsletters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.service.scheduler.SchedulerService;

@RunWith(MockitoJUnitRunner.class)
public class MonthlyNewsletterSchedulerTest {

  @Mock
  private SchedulerService schedulerService;

  private MonthlyNewsletterScheduler monthlyNewsletterScheduler;

  @Before
  public void setup() {
    monthlyNewsletterScheduler = new MonthlyNewsletterScheduler(schedulerService);
  }

  @Test
  public void registerJob_whenJobAlreadyRegistered_verifyInteractions() {

    when(schedulerService.doesJobWithKeyExist(any())).thenReturn(true);

    monthlyNewsletterScheduler.registerJob();

    verify(schedulerService, never()).scheduleJob(any(), any());
  }

  @Test
  public void registerJob_whenJobNotRegistered_verifyInteractions() {

    when(schedulerService.doesJobWithKeyExist(any())).thenReturn(false);

    monthlyNewsletterScheduler.registerJob();

    verify(schedulerService, times(1)).scheduleJob(any(), any());
  }

}