package uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum FuturePlans {
  REUSE("Reuse"),
  RECYCLE("Recycle");

  private final String displayName;

  FuturePlans(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, FuturePlans::getDisplayName));
  }
}
