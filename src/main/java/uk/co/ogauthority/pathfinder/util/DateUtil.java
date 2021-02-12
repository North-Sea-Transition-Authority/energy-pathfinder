package uk.co.ogauthority.pathfinder.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.Temporal;
import java.util.Arrays;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;

public class DateUtil {

  private DateUtil() {
    throw new IllegalStateException("DateUtil is a utility class and should not be instantiated");
  }

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy");
  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")
      .withZone(ZoneId.systemDefault());

  private static String format(Temporal temporal, DateTimeFormatter dateTimeFormatter) {
    return temporal != null
        ? dateTimeFormatter.format(temporal)
        : "";
  }

  public static String formatDate(LocalDate localDate) {
    return format(localDate, DATE_FORMATTER);
  }

  public static String formatInstant(Instant instant) {
    return format(instant, DATE_TIME_FORMATTER);
  }

  public static String getDateFromQuarterYear(Quarter quarter, Integer year) {
    if (quarter != null && year != null) {
      return String.format("Q%s %d", quarter.getDisplayValue(), year);
    }
    return "";
  }

  public static boolean isOnOrBefore(Instant instantToCheck, Instant maxInstant) {
    return instantToCheck.isBefore(maxInstant) || instantToCheck.equals(maxInstant);
  }

  public static boolean isOnOrAfter(Instant instantToCheck, Instant minInstant) {
    return instantToCheck.isAfter(minInstant) || instantToCheck.equals(minInstant);
  }

  public static Quarter getQuarterFromLocalDate(LocalDate localDate) {
    var quarterIndex = localDate.get(IsoFields.QUARTER_OF_YEAR);
    return Arrays.stream(Quarter.values())
        .filter(quarter -> quarter.getDisplayValue().equals(quarterIndex))
        .findFirst()
        .orElseThrow(() -> new RuntimeException(
            String.format("Could not determine current quarter using index %s", quarterIndex))
        );
  }

  public static boolean isInCurrentQuarter(Instant instantToCheck) {
    final var currentQuarter = getQuarterFromLocalDate(LocalDate.now());
    final var quarterStartInstant = currentQuarter.getStartDateAsInstant();
    final var quarterEndInstant = currentQuarter.getEndDateAsInstant();

    return DateUtil.isOnOrAfter(instantToCheck, quarterStartInstant)
        && DateUtil.isOnOrBefore(instantToCheck, quarterEndInstant);
  }

}
