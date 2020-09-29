package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum UkcsArea {

  WOS("West of Shetland (WOS)"),
  NNS("Northern North Sea (NNS)"),
  CNS("Central North Sea (CNS)"),
  SNS("Southern North Sea (SNS)"),
  EIS("East Irish Sea (EIS)");

  private final String displayName;

  UkcsArea(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, UkcsArea::getDisplayName));
  }
}
