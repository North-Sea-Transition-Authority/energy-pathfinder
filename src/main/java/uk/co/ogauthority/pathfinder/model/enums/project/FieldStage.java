package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum FieldStage {
  CARBON_CAPTURE_AND_STORAGE(
      "Carbon Capture and Storage (CCS)",
      Set.of(EnergyType.TRANSITION)
  ),
  ELECTRIFICATION(
      "Electrification",
      Set.of(EnergyType.TRANSITION)
  ),
  HYDROGEN(
      "Hydrogen",
      Set.of(EnergyType.TRANSITION)
  ),
  OIL_AND_GAS(
      "Oil and Gas",
      Set.of(EnergyType.OIL_AND_GAS)
  ),
  WIND_ENERGY(
      "Wind energy",
      Set.of(EnergyType.TRANSITION)
  );

  private final String displayName;

  private final Set<EnergyType> energyType;

  FieldStage(String displayName, Set<EnergyType> energyType) {
    this.displayName = displayName;
    this.energyType = energyType;
  }

  public String getDisplayName() {
    return displayName;
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

  public static List<FieldStage> getAllAsList() {
    return Arrays.stream(values())
        .collect(Collectors.toList());
  }

  public static List<FieldStage> getEnergyTransitionProjectFieldStages() {
    return Arrays.stream(values())
        .filter(fs -> fs.energyType.contains(EnergyType.TRANSITION))
        .collect(Collectors.toList());
  }

  public static boolean isEnergyTransition(FieldStage fieldStage) {
    return fieldStage.getEnergyType().contains(EnergyType.TRANSITION);
  }
}
