package uk.co.ogauthority.pathfinder.model.enums;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum Quarter {
  Q1(1,
      LocalDate.of(LocalDate.now().getYear(), 1, 1),
      LocalDate.of(LocalDate.now().getYear(), 3, 31)
  ),
  Q2(2,
      LocalDate.of(LocalDate.now().getYear(), 4, 1),
      LocalDate.of(LocalDate.now().getYear(), 6, 30)
  ),
  Q3(3,
      LocalDate.of(LocalDate.now().getYear(), 7, 1),
      LocalDate.of(LocalDate.now().getYear(), 9, 30)
  ),
  Q4(4,
      LocalDate.of(LocalDate.now().getYear(), 10, 1),
      LocalDate.of(LocalDate.now().getYear(), 12, 31)
  );

  private final Integer displayValue;
  private final LocalDate startDate;
  private final LocalDate endDate;

  Quarter(Integer displayValue, LocalDate startDate, LocalDate endDate) {
    this.displayValue = displayValue;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public Integer getDisplayValue() {
    return displayValue;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public static Map<String, Integer> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, Quarter::getDisplayValue));
  }

  public Instant getStartDateAsInstant() {
    return getDateAsInstant(getStartDate());
  }

  public Instant getEndDateAsInstant() {
    return getDateAsInstant(getEndDate());
  }

  private Instant getDateAsInstant(LocalDate localDate) {
    return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
  }
}
