package uk.co.ogauthority.pathfinder.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
  public static final ZoneOffset UTC_ZONE = ZoneOffset.UTC;

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
    final var currentQuarter = getCurrentQuarter();
    final var quarterStartInstant = currentQuarter.getStartDateAsInstant();
    final var quarterEndInstant = currentQuarter.getEndDateAsInstant();

    return DateUtil.isOnOrAfter(instantToCheck, quarterStartInstant)
        && DateUtil.isOnOrBefore(instantToCheck, quarterEndInstant);
  }

  /**
   * Returns an instant representing the start of the month for the month in dateToGetStartOfMonthFor.
   * @param dateToGetStartOfMonthFor a local date to get the start of the month for
   * @return an instant representing the start of the month dateToGetStartOfMonthFor is in
   */
  public static Instant getStartOfMonth(LocalDate dateToGetStartOfMonthFor) {
    // use UTC as zone as dates in the database don't include the timezone
    return getStartOfMonth(dateToGetStartOfMonthFor, UTC_ZONE);
  }

  private static Instant getStartOfMonth(LocalDate dateToGetStartOfMonthFor, ZoneOffset zoneOffset) {
    return YearMonth.from(dateToGetStartOfMonthFor).atDay(1).atStartOfDay(zoneOffset).toInstant();
  }

  /**
   * Returns an instant representing the end of the month for the month in dateToGetEndOfMonthFor.
   * @param dateToGetEndOfMonthFor a local date to get the end of the month for
   * @return an instant representing the end of the month dateToGetEndOfMonthFor is in
   */
  public static Instant getEndOfMonth(LocalDate dateToGetEndOfMonthFor) {
    // use UTC as zone as dates in the database don't include the timezone
    return getEndOfMonth(dateToGetEndOfMonthFor, UTC_ZONE);
  }

  private static Instant getEndOfMonth(LocalDate dateToGetEndOfMonthFor, ZoneOffset zoneOffset) {
    return YearMonth.from(dateToGetEndOfMonthFor).atEndOfMonth().atTime(LocalTime.MAX).atZone(zoneOffset).toInstant();
  }

  public static long daysBetween(LocalDate fromDate, LocalDate toDate) {
    return ChronoUnit.DAYS.between(fromDate, toDate);
  }

  public static Quarter getCurrentQuarter() {
    return getQuarterFromLocalDate(LocalDate.now());
  }

}
