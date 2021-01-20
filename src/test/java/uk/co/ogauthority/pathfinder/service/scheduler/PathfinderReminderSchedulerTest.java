package uk.co.ogauthority.pathfinder.service.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.scheduler.ReminderType;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateRequestReminderJob;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class PathfinderReminderSchedulerTest {

  @Mock
  private Scheduler scheduler;

  private PathfinderReminderScheduler pathfinderReminderScheduler;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();
  private final ReminderType reminderType = ReminderType.REGULATOR_UPDATE_REQUEST_AFTER_DEADLINE_REMINDER;

  @Before
  public void setUp() throws Exception {
    pathfinderReminderScheduler = new PathfinderReminderScheduler(scheduler);
  }


  @Test
  public void createTriggerKey() {
    var triggerKey = pathfinderReminderScheduler.createTriggerKey(detail, reminderType);
    assertThat(triggerKey.getGroup()).isEqualTo(reminderType.getTriggerGroupName());
    assertThat(triggerKey.getName()).isEqualTo(
        String.format("%S%S%S", reminderType.getTriggerNamePrefix(), PathfinderReminderScheduler.TRIGGER_NAME_SUFFIX, detail.getProject().getId())
    );
  }

  @Test
  public void createJobKey() {
    var jobKey = pathfinderReminderScheduler.createJobKey(detail, reminderType);
    assertThat(jobKey.getGroup()).isEqualTo(reminderType.getJobGroupName());
    assertThat(jobKey.getName()).isEqualTo(
        String.format("%S%S%S", reminderType.getJobNamePrefix(), PathfinderReminderScheduler.JOB_NAME_SUFFIX, detail.getProject().getId())
    );
  }

  @Test
  public void unscheduleReminder() throws SchedulerException {
    var triggerKey = new TriggerKey(
        String.format("%S%S%S", reminderType.getTriggerNamePrefix(), PathfinderReminderScheduler.TRIGGER_NAME_SUFFIX, detail.getProject().getId()),
        reminderType.getTriggerGroupName()
    );
    pathfinderReminderScheduler.unscheduleReminder(detail, reminderType);
    verify(scheduler, times(1)).unscheduleJobs(Collections.singletonList(triggerKey));
  }

  @Test
  public void getFirstTriggerDateTime_positiveIncrement() {
    var zoneDatetime = pathfinderReminderScheduler.getFirstTriggerDateTime(reminderType);
    var schedulerDatetime = LocalDateTime.now().plus(
        reminderType.getReminderOffset().getNumberOfUnits(),
        reminderType.getReminderOffset().getUnits()
    );
    assertThat(zoneDatetime.getDayOfMonth()).isEqualTo(schedulerDatetime.getDayOfMonth());
    assertThat(zoneDatetime.getMonth()).isEqualTo(schedulerDatetime.getMonth());
    assertThat(zoneDatetime.getYear()).isEqualTo(schedulerDatetime.getYear());
    assertThat(zoneDatetime.getHour()).isEqualTo(schedulerDatetime.getHour());
  }

  @Test
  public void scheduleReminder() throws SchedulerException {
    pathfinderReminderScheduler.scheduleReminder(detail, reminderType, RegulatorUpdateRequestReminderJob.class);
    verify(scheduler, times(1)).scheduleJob(
        any(), any(), eq(true)
    );
  }
}
