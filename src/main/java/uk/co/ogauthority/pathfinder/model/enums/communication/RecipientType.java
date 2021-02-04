package uk.co.ogauthority.pathfinder.model.enums.communication;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum RecipientType {

  OPERATORS("Operators"),
  SUBSCRIBERS("Subscribers");

  private final String displayName;

  RecipientType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, RecipientType::getDisplayName));
  }
}
