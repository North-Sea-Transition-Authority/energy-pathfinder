package uk.co.ogauthority.pathfinder.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class InstantUtil {

  private InstantUtil() {
    throw new IllegalStateException("InstantUtil is a utility class and should not be instantiated");
  }

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
      .withZone(ZoneId.systemDefault());

  public static String formatInstant(Instant instant) {
    return instant != null
        ? DATE_FORMATTER.format(instant)
        : "";
  }
}
