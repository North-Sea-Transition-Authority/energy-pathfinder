package uk.co.ogauthority.pathfinder.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.Quarter;

public class DateUtil {

  private DateUtil() {
    throw new IllegalStateException("DateUtil is a utility class and should not be instantiated");
  }

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy");
  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss")
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

}
