package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum EnergyTransitionCategory {

  CARBON_CAPTURE_UTILISATION_AND_STORAGE("Carbon capture and storage (CCS)"),
  ELECTRIFICATION("Low carbon power"),
  HYDROGEN("Hydrogen"),
  OFFSHORE_POWER_GENERATION("Offshore power generation");

  private final String displayName;

  EnergyTransitionCategory(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .sorted(Comparator.comparing(EnergyTransitionCategory::getDisplayName))
        .collect(StreamUtil.toLinkedHashMap(Enum::name, EnergyTransitionCategory::getDisplayName));
  }
}
