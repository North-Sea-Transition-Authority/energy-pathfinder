package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum InfrastructureStatus {

  IN_USE("Still in use"),
  READY_TO_DECOMMISSION("Ready for decommissioning");

  private final String displayName;

  InfrastructureStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, InfrastructureStatus::getDisplayName));
  }

}
