package uk.co.ogauthority.pathfinder.model.enums.scheduler;

import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateRequestReminderService;

public enum ReminderType {
  REGULATOR_UPDATE_REQUEST_DEADLINE_REMINDER(
      "REG_UPDATE_DEADLINE_TR_",
      "REG_UPDATE_DEADLINE_JOB_",
      RegulatorUpdateRequestReminderService.JOB_GROUP_NAME,
      RegulatorUpdateRequestReminderService.TRIGGER_GROUP_NAME,
      ReminderOffset.ONE_WEEK_FROM_DATE
  ),
  REGULATOR_UPDATE_REQUEST_AFTER_DEADLINE_REMINDER(
      "REG_UPDATE_AFTER_DEADLINE_TR_",
      "REG_UPDATE_AFTER_DEADLINE_JOB_",
      RegulatorUpdateRequestReminderService.JOB_GROUP_NAME,
      RegulatorUpdateRequestReminderService.TRIGGER_GROUP_NAME,
      ReminderOffset.ONE_DAY_FROM_DATE
  );


  private final String triggerNamePrefix;
  private final String jobNamePrefix;
  private final String jobGroupName;
  private final String triggerGroupName;
  private final ReminderOffset reminderOffset;


  ReminderType(String triggerNamePrefix,
               String jobNamePrefix,
               String jobGroupName,
               String triggerGroupName,
               ReminderOffset reminderOffset) {
    this.triggerNamePrefix = triggerNamePrefix;
    this.jobNamePrefix = jobNamePrefix;
    this.jobGroupName = jobGroupName;
    this.triggerGroupName = triggerGroupName;
    this.reminderOffset = reminderOffset;
  }

  public String getTriggerNamePrefix() {
    return triggerNamePrefix;
  }

  public String getJobNamePrefix() {
    return jobNamePrefix;
  }

  public String getJobGroupName() {
    return jobGroupName;
  }

  public String getTriggerGroupName() {
    return triggerGroupName;
  }

  public ReminderOffset getReminderOffset() {
    return reminderOffset;
  }
}
