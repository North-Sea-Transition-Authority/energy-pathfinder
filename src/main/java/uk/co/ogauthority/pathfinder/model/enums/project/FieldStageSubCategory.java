package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum FieldStageSubCategory {

  CAPTURE_AND_ONSHORE("Capture and onshore", "Emitters and onshore pipelines", FieldStage.CARBON_CAPTURE_AND_STORAGE),
  TRANSPORTATION_AND_STORAGE("Transportation and storage",
      "Offshore pipelines and reservoir storage", FieldStage.CARBON_CAPTURE_AND_STORAGE),
  OFFSHORE_HYDROGEN("Offshore hydrogen", FieldStage.HYDROGEN),
  ONSHORE_HYDROGEN("Onshore hydrogen", FieldStage.HYDROGEN),
  OFFSHORE_ELECTRIFICATION("Offshore electrification", FieldStage.ELECTRIFICATION),
  ONSHORE_ELECTRIFICATION("Onshore electrification", FieldStage.ELECTRIFICATION),
  FIXED_BOTTOM_OFFSHORE_WIND("Fixed bottom offshore wind", FieldStage.WIND_ENERGY),
  FLOATING_OFFSHORE_WIND("Floating offshore wind", FieldStage.WIND_ENERGY),
  ONSHORE_WIND("Onshore wind", FieldStage.WIND_ENERGY);

  private final String displayName;

  private final String description;

  private final FieldStage fieldStage;

  FieldStageSubCategory(String displayName, String description, FieldStage fieldStage) {
    this.displayName = displayName;
    this.description = description;
    this.fieldStage = fieldStage;
  }

  FieldStageSubCategory(String displayName, FieldStage fieldStage) {
    this.displayName = displayName;
    this.fieldStage = fieldStage;
    this.description = "";
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }

  public FieldStage getFieldStage() {
    return fieldStage;
  }

  public static Map<String, String> getAllAsMap(FieldStage fieldStage) {
    return Arrays.stream(values())
        .filter(subCategory -> subCategory.fieldStage.equals(fieldStage))
        .sorted(Comparator.comparing(FieldStageSubCategory::getDisplayName))
        .collect(StreamUtil.toLinkedHashMap(Enum::name, FieldStageSubCategory::getDisplayName));
  }

  public static Map<String, String> getEntryAsMap(FieldStageSubCategory fieldStageSubCategory) {
    return Collections.singletonMap(fieldStageSubCategory.name(), fieldStageSubCategory.getDisplayName());
  }

  public static Set<FieldStage> getAllFieldStagesWithSubCategories() {
    return Arrays.stream(values())
        .map(FieldStageSubCategory::getFieldStage)
        .collect(Collectors.toSet());
  }

}
