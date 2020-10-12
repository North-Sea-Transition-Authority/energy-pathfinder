package uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum SubstructureRemovalPremise {
  FULL("Full"),
  PARTIAL("Partial");

  private final String displayName;

  SubstructureRemovalPremise(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, SubstructureRemovalPremise::getDisplayName));
  }
}
