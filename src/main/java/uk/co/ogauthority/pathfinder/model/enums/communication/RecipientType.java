package uk.co.ogauthority.pathfinder.model.enums.communication;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.Displayable;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum RecipientType implements Displayable {

  OPERATORS("Operators/Developers"),
  SUBSCRIBERS("Subscribers");

  private final String displayName;

  RecipientType(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, RecipientType::getDisplayName));
  }
}
