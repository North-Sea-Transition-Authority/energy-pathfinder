package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum FieldType {

  OIL("Oil"),
  GAS("Gas"),
  OIL_GAS("Oil/gas"),
  CONDENSATE("Condensate"),
  OIL_CONDENSATE("Oil condensate"),
  GAS_CONDENSATE("Gas condensate"),
  GAS_STORAGE("Gas storage"),
  CARBON_STORAGE("Carbon storage");

  private final String displayName;

  FieldType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, FieldType::getDisplayName));
  }
}
