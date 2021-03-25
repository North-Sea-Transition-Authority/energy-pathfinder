package uk.co.ogauthority.pathfinder.model.enums.project.integratedrig;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum IntegratedRigStatus {

  IN_USE("In use"),
  WARM("Warm"),
  COLD("Cold");

  private final String displayName;

  IntegratedRigStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, IntegratedRigStatus::getDisplayName));
  }
}
