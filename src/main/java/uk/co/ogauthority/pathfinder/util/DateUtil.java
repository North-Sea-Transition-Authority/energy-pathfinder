package uk.co.ogauthority.pathfinder.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {

  public DateUtil() {
    throw new AssertionError();
  }

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy");
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")
      .withLocale(Locale.UK)
      .withZone(ZoneId.systemDefault());

  public static String formatDate(LocalDate localDate) {
    return localDate != null
        ? localDate.format(DATE_FORMATTER)
        : "";
  }

}
