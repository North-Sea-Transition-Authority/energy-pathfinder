package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum FieldStage {
  DISCOVERY("Discovery", "Exploration phase"),
  DEVELOPMENT("Development", "Industry engagement throughout project development cycle up to first oil"),
  DECOMMISSIONING("Decommissioning", "Decommissioning planning commenced either pre/post Cessation of Production"),
  ENERGY_TRANSITION("Energy transition", "");

  private final String displayName;

  private final String description;

  FieldStage(String displayName, String description) {
    this.displayName = displayName;
    this.description = description;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }

  public static Map<String, String> getEntryAsMap(FieldStage fieldStage) {
    return Collections.singletonMap(fieldStage.name(), fieldStage.getDisplayName());
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, FieldStage::getDisplayName));
  }
}
