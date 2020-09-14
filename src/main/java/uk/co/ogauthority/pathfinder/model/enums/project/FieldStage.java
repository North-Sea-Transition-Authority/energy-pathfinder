package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Collections;
import java.util.Map;

public enum FieldStage {
  DISCOVERY("Discovery", "Early phase before FDP approval"),
  DEVELOPMENT("Development", "FDP has been approved"),
  OPERATIONS("Operations", "Field now operational"),
  DECOMMISSIONING("Decommissioning", "Decommissioning planning commenced either pre / post COP"),
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

  public String getNameAndDescription() {
    return !description.equals("") ? displayName + " - " + description : displayName;
  }

  public static Map<String, String> getEntryAsMap(FieldStage fieldStage) {
    return Collections.singletonMap(fieldStage.name(), fieldStage.getNameAndDescription());
  }
}
