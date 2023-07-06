package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum FieldStage {
  DISCOVERY("Discovery", "Exploration phase", false, Set.of(EnergyType.OIL_AND_GAS)),
  DEVELOPMENT("Development", "Industry engagement throughout project development cycle up to first oil", true, Set.of(EnergyType.OIL_AND_GAS)),
  DECOMMISSIONING("Decommissioning", "Decommissioning planning commenced either pre/post Cessation of Production", false, Set.of(EnergyType.OIL_AND_GAS)),
  CARBON_CAPTURE_AND_STORAGE("Carbon Capture and Storage (CCS)", "", true, Set.of(EnergyType.TRANSITION)),
  HYDROGEN("Hydrogen", "", false, Set.of(EnergyType.TRANSITION)),
  OFFSHORE_ELECTRIFICATION("Offshore electrification", "", false, Set.of(EnergyType.TRANSITION)),
  OFFSHORE_WIND("Offshore wind", "", true, Set.of(EnergyType.TRANSITION));

  private final String displayName;

  private final String description;

  private final boolean hasHiddenInputs;

  private final Set<EnergyType> energyType;

  FieldStage(String displayName, String description, boolean hasHiddenInputs, Set<EnergyType> energyType) {
    this.displayName = displayName;
    this.description = description;
    this.hasHiddenInputs = hasHiddenInputs;
    this.energyType = energyType;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }

  public boolean hasHiddenInputs() {
    return hasHiddenInputs;
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

  public static List<FieldStage> getAllWithHiddenInputs() {
    return Arrays.stream(values())
        .filter(FieldStage::hasHiddenInputs)
        .collect(Collectors.toList());
  }

  public static List<FieldStage> getEnergyTransitionProjectFieldStages() {
    return Arrays.stream(values())
        .filter(fs -> fs.energyType.contains(EnergyType.TRANSITION))
        .collect(Collectors.toList());
  }
}
