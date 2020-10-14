package uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum SubseaInfrastructureStatus {

  IN_USE("Still in use"),
  READY_TO_DECOMMISSION("Ready for decommissioning");

  private final String displayName;

  SubseaInfrastructureStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, SubseaInfrastructureStatus::getDisplayName));
  }

}
