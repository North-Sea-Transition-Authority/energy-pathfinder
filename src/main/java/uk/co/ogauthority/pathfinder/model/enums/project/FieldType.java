package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.Displayable;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum FieldType implements Displayable {

  OIL("Oil"),
  GAS("Gas"),
  OIL_GAS("Oil/gas"),
  CONDENSATE("Condensate"),
  GAS_STORAGE("Gas storage"),
  CARBON_STORAGE("Carbon storage");

  private final String displayName;

  FieldType(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, FieldType::getDisplayName));
  }
}
