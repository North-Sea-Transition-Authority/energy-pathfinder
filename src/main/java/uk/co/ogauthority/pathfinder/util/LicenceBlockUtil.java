package uk.co.ogauthority.pathfinder.util;

import org.apache.commons.lang3.StringUtils;

public class LicenceBlockUtil {

  private LicenceBlockUtil() {
    throw new IllegalStateException("LicenceBlockUtil is a utility class and should not be instantiated");
  }

  public static String getSortKey(String quadrantNumber, String blockNumber, String blockSuffix) {
    return StringUtils.leftPad(quadrantNumber, 10, "0") +
        StringUtils.leftPad(blockNumber, 10, "0") +
        StringUtils.leftPad(blockSuffix, 10, "0");
  }
}
