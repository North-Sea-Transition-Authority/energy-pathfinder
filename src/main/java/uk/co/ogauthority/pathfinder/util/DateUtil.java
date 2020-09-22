package uk.co.ogauthority.pathfinder.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

  private DateUtil() {
    throw new IllegalStateException("DateUtil is a utility class and should not be instantiated");
  }

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy");

  public static String formatDate(LocalDate localDate) {
    return localDate != null
        ? localDate.format(DATE_FORMATTER)
        : "";
  }

}
