package uk.co.ogauthority.pathfinder.util;

import org.apache.commons.lang3.StringUtils;

public class WellboreUtil {

  private WellboreUtil() {
    throw new IllegalStateException("WellboreUtil is a utility class and should not be instantiated");
  }

  public static String getSortKey(String quadrantNumber,
                                  String blockNumber,
                                  String blockSuffix,
                                  String platformLetter,
                                  String drillingSeqNumber,
                                  String wellSuffix) {
    return StringUtils.leftPad(quadrantNumber, 10, "0") +
        StringUtils.leftPad(blockNumber, 10, "0") +
        StringUtils.leftPad(blockSuffix, 10, "0") +
        StringUtils.leftPad(platformLetter, 10, "0") +
        StringUtils.leftPad(drillingSeqNumber, 10, "0") +
        StringUtils.leftPad(wellSuffix, 10, "0");
  }
}
