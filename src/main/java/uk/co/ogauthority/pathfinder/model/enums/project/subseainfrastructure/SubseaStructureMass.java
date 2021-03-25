package uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum SubseaStructureMass {

  LESS_THAN_400_TONNES("Less than 400 metric tonnes"),
  GREATER_THAN_OR_EQUAL_400_TONNES("Greater than or equal to 400 metric tonnes");

  private final String displayName;

  SubseaStructureMass(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, SubseaStructureMass::getDisplayName));
  }
}
