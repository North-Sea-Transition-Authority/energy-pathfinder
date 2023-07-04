package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum FieldStage {
  DISCOVERY("Discovery", "Exploration phase", false, false),
  DEVELOPMENT("Development", "Industry engagement throughout project development cycle up to first oil", true, false),
  DECOMMISSIONING("Decommissioning", "Decommissioning planning commenced either pre/post Cessation of Production", false, false),
  CARBON_CAPTURE_AND_STORAGE("Carbon Capture and Storage (CCS)", "", true, true),
  HYDROGEN("Hydrogen", "", false, true),
  OFFSHORE_ELECTRIFICATION("Offshore electrification", "", false, true),
  OFFSHORE_WIND("Offshore Wind", "", true, true);

  private final String displayName;

  private final String description;

  private final boolean hasHiddenInputs;

  private final boolean isEnergyTransitionProject;

  FieldStage(String displayName, String description, boolean hasHiddenInputs, boolean isEnergyTransitionProject) {
    this.displayName = displayName;
    this.description = description;
    this.hasHiddenInputs = hasHiddenInputs;
    this.isEnergyTransitionProject = isEnergyTransitionProject;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }

  public boolean isHasHiddenInputs() {
    return hasHiddenInputs;
  }

  public boolean isEnergyTransitionProject() {
    return isEnergyTransitionProject;
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
        .filter(FieldStage::isHasHiddenInputs)
        .collect(Collectors.toList());
  }

  public static List<FieldStage> getEnergyTransitionProjectFieldStages() {
    return Arrays.stream(values())
        .filter(FieldStage::isEnergyTransitionProject)
        .collect(Collectors.toList());
  }
}
