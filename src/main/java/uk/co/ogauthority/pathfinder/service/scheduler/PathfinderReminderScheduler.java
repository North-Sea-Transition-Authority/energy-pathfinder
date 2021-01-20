package uk.co.ogauthority.pathfinder.service.scheduler;

import static org.quartz.JobBuilder.newJob;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.scheduler.ReminderOffsetType;
import uk.co.ogauthority.pathfinder.model.enums.scheduler.ReminderType;

@Service
public class PathfinderReminderScheduler implements ReminderScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(PathfinderReminderScheduler.class);
  public static final String JOB_NAME_SUFFIX = "_JOB_";
  public static final String TRIGGER_NAME_SUFFIX = "_TR_";
  private final Scheduler scheduler;

  @Autowired
  public PathfinderReminderScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public void scheduleReminder(ProjectDetail detail, ReminderType reminderType, Class<? extends Job> jobClass) throws SchedulerException {
    ZonedDateTime firstTriggerDatetime = getFirstTriggerDateTime(reminderType);

    JobKey jobKey = createJobKey(detail, reminderType);
    JobDetail jobDetail = newJob(jobClass)
        .withIdentity(jobKey)
        .requestRecovery()
        .usingJobData("projectId", detail.getProject().getId())
        .storeDurably()
        .build();

    TriggerKey triggerKey = createTriggerKey(detail, reminderType);
    Trigger trigger = TriggerBuilder
        .newTrigger()
        .withIdentity(triggerKey)
        .startAt(Date.from(firstTriggerDatetime.toInstant()))
        .withSchedule(
            CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withIntervalInYears(1)
        )
        .build();

    // allow replacement of job to cater for cancellation of updates
    scheduler.scheduleJob(jobDetail, new HashSet<>(Collections.singletonList(trigger)), true);
  }

  @Override
  public void scheduleReminder(ProjectDetail detail, ReminderType reminderType, Class<? extends Job> jobClass,
                               LocalDateTime localDateTime) throws SchedulerException {
    //TODO PAT-242/243 Same as above but with relative date call to getFirstTriggerDateTime
  }

  @Override
  public void unscheduleReminder(ProjectDetail detail, ReminderType reminderType) throws SchedulerException {
    List<TriggerKey> triggerKeys = Collections.singletonList(createTriggerKey(detail, reminderType));
    // this only deletes the triggers, not the jobs themselves.
    // This allows some investigation of failed jobs after the fact instead of removing all traces
    scheduler.unscheduleJobs(triggerKeys);
    LOGGER.info(
        "Unscheduled jobs for project with id: {} and trigger group: {}",
        detail.getProject().getId(),
        reminderType.getTriggerGroupName()
    );
  }

  @Override
  public TriggerKey createTriggerKey(ProjectDetail detail, ReminderType reminderType) {
    return new TriggerKey(
        String.format("%S%S%S", reminderType.getTriggerNamePrefix(), TRIGGER_NAME_SUFFIX, detail.getProject().getId()),
        reminderType.getTriggerGroupName()
    );
  }

  @Override
  public JobKey createJobKey(ProjectDetail detail, ReminderType reminderType) {
    return new JobKey(
        String.format("%S%S%S", reminderType.getJobNamePrefix(), JOB_NAME_SUFFIX, detail.getProject().getId()),
        reminderType.getJobGroupName()
    );
  }

  ZonedDateTime getFirstTriggerDateTime(ReminderType reminderType) {
    var reminderOffset = reminderType.getReminderOffset();
    var scheduledDateTime = LocalDateTime.now();
    scheduledDateTime = reminderOffset.getReminderOffsetType().equals(ReminderOffsetType.PLUS)
        ? scheduledDateTime.plus(reminderOffset.getNumberOfUnits(), reminderOffset.getUnits())
        : scheduledDateTime.minus(reminderOffset.getNumberOfUnits(), reminderOffset.getUnits());

    return ZonedDateTime.ofLocal(
        LocalDateTime.of(
            scheduledDateTime.getYear(),
            scheduledDateTime.getMonth(),
            scheduledDateTime.getDayOfMonth(),
            scheduledDateTime.getHour(),
            scheduledDateTime.getMinute()
        ),
        ZoneId.systemDefault(),
        null);
  }

}
