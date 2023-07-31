package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum FieldStage {
  DISCOVERY(
      "Discovery",
      "Exploration phase",
      Set.of(EnergyType.OIL_AND_GAS)
  ),
  DEVELOPMENT(
      "Development",
      "Industry engagement throughout project development cycle up to first oil",
      Set.of(EnergyType.OIL_AND_GAS)
  ),
  DECOMMISSIONING(
      "Decommissioning",
      "Decommissioning planning commenced either pre/post Cessation of Production",
      Set.of(EnergyType.OIL_AND_GAS)
  ),
  CARBON_CAPTURE_AND_STORAGE(
      "Carbon Capture and Storage (CCS)",
      Set.of(EnergyType.TRANSITION)
  ),
  HYDROGEN(
      "Hydrogen",
      Set.of(EnergyType.TRANSITION)
  ),
  OFFSHORE_ELECTRIFICATION(
      "Offshore electrification",
      Set.of(EnergyType.TRANSITION)
  ),
  OFFSHORE_WIND(
      "Offshore wind",
      Set.of(EnergyType.TRANSITION)
  );

  private final String displayName;

  private final String description;

  private final Set<EnergyType> energyType;

  FieldStage(String displayName, String description, Set<EnergyType> energyType) {
    this.displayName = displayName;
    this.description = description;
    this.energyType = energyType;
  }

  FieldStage(String displayName, Set<EnergyType> energyType) {
    this.displayName = displayName;
    this.energyType = energyType;
    this.description = "";
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }

  public Set<EnergyType> getEnergyType() {
    return energyType;
  }

  public static Map<String, String> getEntryAsMap(FieldStage fieldStage) {
    return Collections.singletonMap(fieldStage.name(), fieldStage.getDisplayName());
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, FieldStage::getDisplayName));
  }

  public static Map<String, String> getAllAsMapOrdered() {
    return Arrays.stream(values())
        .sorted(Comparator.comparing(FieldStage::getDisplayName))
        .collect(StreamUtil.toLinkedHashMap(Enum::name, FieldStage::getDisplayName));
  }

  public static List<FieldStage> getAllAsList() {
    return Arrays.stream(values())
        .collect(Collectors.toList());
  }

  public static List<FieldStage> getEnergyTransitionProjectFieldStages() {
    return Arrays.stream(values())
        .filter(fs -> fs.energyType.contains(EnergyType.TRANSITION))
        .collect(Collectors.toList());
  }
}
