package uk.co.ogauthority.pathfinder.util;

import org.apache.commons.lang3.StringUtils;

public class CoordinateUtil {

  private CoordinateUtil() {
    throw new IllegalStateException("CoordinateUtil is a utility class and should not be instantiated");
  }

  public static String formatCoordinate(Integer degrees, Integer minutes, Double seconds, String hemisphere) {
    var result = new StringBuilder();
    if (degrees != null) {
      result.append(degrees).append("°");
    }
    if (minutes != null) {
      if (!result.isEmpty()) {
        result.append(" ");
      }
      result.append(minutes).append("'");
    }
    if (seconds != null) {
      if (!result.isEmpty()) {
        result.append(" ");
      }
      result.append(seconds).append("\"");
    }
    if (hemisphere != null) {
      if (!result.isEmpty()) {
        result.append(" ");
      }
      result.append(StringUtils.capitalize(hemisphere.toLowerCase()));
    }
    return result.toString();
  }
}
