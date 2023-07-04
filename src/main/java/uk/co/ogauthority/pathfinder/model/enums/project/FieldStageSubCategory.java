package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum FieldStageSubCategory {

  FIXED_BOTTOM_OFFSHORE_WIND("Fixed bottom offshore wind", "", FieldStage.OFFSHORE_WIND),
  FLOATING_OFFSHORE_WIND("Floating offshore wind", "", FieldStage.OFFSHORE_WIND),
  CAPTURE_AND_ONSHORE("Capture and onshore", "Emitters and onshore pipelines", FieldStage.CARBON_CAPTURE_AND_STORAGE),
  TRANSPORTATION_AND_STORAGE("Transportation and storage",
      "Offshore pipelines and reservoir storage", FieldStage.CARBON_CAPTURE_AND_STORAGE);

  private final String displayName;

  private final String description;

  private final FieldStage fieldStage;

  FieldStageSubCategory(String displayName, String description, FieldStage fieldStage) {
    this.displayName = displayName;
    this.description = description;
    this.fieldStage = fieldStage;
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

  public static List<FieldStage> getAllFieldStagesWithSubCategories() {
    return Arrays.stream(values())
        .map(FieldStageSubCategory::getFieldStage)
        .distinct()
        .collect(Collectors.toList());
  }

  public static List<String> getAllFieldStagesWithSubCategoriesAsStrings() {
    return getAllFieldStagesWithSubCategories()
        .stream()
        .map(FieldStage::getDisplayName)
        .collect(Collectors.toList());
  }

}
