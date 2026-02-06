package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.Displayable;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum InputEntryType implements Displayable {
  ESTIMATED("Estimated"),
  ACTUAL("Actual");

  private final String displayName;

  InputEntryType(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, InputEntryType::getDisplayName));
  }
}
