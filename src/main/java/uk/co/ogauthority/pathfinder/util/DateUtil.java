package uk.co.ogauthority.pathfinder.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.Quarter;

public class DateUtil {

  private DateUtil() {
    throw new IllegalStateException("DateUtil is a utility class and should not be instantiated");
  }

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy");

  public static String formatDate(LocalDate localDate) {
    return localDate != null
        ? localDate.format(DATE_FORMATTER)
        : "";
  }

  public static String getDateFromQuarterYear(Quarter quarter, Integer year) {
    if (quarter != null && year != null) {
      return String.format("Q%s %d", quarter.getDisplayValue(), year);
    }
    return "";
  }

}
