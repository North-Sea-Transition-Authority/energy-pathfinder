package uk.co.ogauthority.pathfinder.service.scheduler.reminders;

import java.time.LocalDateTime;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.scheduler.ReminderType;

public interface ReminderScheduler {

  /**
   * Schedule a reminder for the given detail and reminder type.
   * The reminder will be scheduled relative to the current LocalDateTime and the ReminderType's ReminderOffset
   */
  void scheduleReminder(ProjectDetail detail, ReminderType reminderType, Class<?extends Job> jobClass) throws SchedulerException;

  /**
   * Schedule a reminder for the given detail and reminder type. The reminder will be scheduled relative to the provided localDateTime and
   * the ReminderType's ReminderOffset.
   */
  void scheduleReminder(
      ProjectDetail detail,
      ReminderType reminderType,
      Class<?extends Job> jobClass,
      LocalDateTime localDateTime
  ) throws SchedulerException;

  void unscheduleReminder(ProjectDetail detail, ReminderType reminderType) throws SchedulerException;

  TriggerKey createTriggerKey(ProjectDetail detail, ReminderType reminderType);

  JobKey createJobKey(ProjectDetail detail, ReminderType reminderType);

  //TODO PAT-242/243 add same methods as above but with organisationGroup rather than detail

}
