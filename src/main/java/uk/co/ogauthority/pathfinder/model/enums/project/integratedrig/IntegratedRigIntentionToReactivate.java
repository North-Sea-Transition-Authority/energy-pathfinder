package uk.co.ogauthority.pathfinder.model.enums.project.integratedrig;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum IntegratedRigIntentionToReactivate {

  YES("Yes"),
  NO("No"),
  UNKNOWN("Unknown");

  private final String displayName;

  IntegratedRigIntentionToReactivate(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, IntegratedRigIntentionToReactivate::getDisplayName));
  }
}
