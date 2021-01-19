package uk.co.ogauthority.pathfinder.model.enums.scheduler;

import java.time.temporal.ChronoUnit;

public enum ReminderOffset {
  ONE_WEEK_FROM_DATE(7, ChronoUnit.DAYS, ReminderOffsetType.PLUS),
  ONE_WEEK_BEFORE_DATE(7, ChronoUnit.DAYS, ReminderOffsetType.MINUS),
  ONE_DAY_FROM_DATE(1, ChronoUnit.DAYS, ReminderOffsetType.PLUS),
  THREE_DAYS_FROM_DATE(3, ChronoUnit.DAYS, ReminderOffsetType.PLUS);

  private final int numberOfUnits;
  private final ChronoUnit units;
  private final ReminderOffsetType reminderOffsetType;

  ReminderOffset(int numberOfUnits, ChronoUnit units,
                 ReminderOffsetType reminderOffsetType) {
    this.numberOfUnits = numberOfUnits;
    this.units = units;
    this.reminderOffsetType = reminderOffsetType;
  }


  public int getNumberOfUnits() {
    return numberOfUnits;
  }

  public ChronoUnit getUnits() {
    return units;
  }

  public ReminderOffsetType getReminderOffsetType() {
    return reminderOffsetType;
  }
}
