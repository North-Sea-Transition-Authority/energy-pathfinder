package uk.co.ogauthority.pathfinder.model.enums.project;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class FieldStageSubCategoryTest {

  @Test
  void getAllAsMap_carbonCaptureAndStorage() {
    var result = FieldStageSubCategory.getAllAsMap(FieldStage.CARBON_CAPTURE_AND_STORAGE);

    assertThat(result.entrySet()).containsExactlyInAnyOrder(
        entry(FieldStageSubCategory.CAPTURE_AND_ONSHORE.name(), FieldStageSubCategory.CAPTURE_AND_ONSHORE.getDisplayName()),
        entry(FieldStageSubCategory.TRANSPORTATION_AND_STORAGE.name(), FieldStageSubCategory.TRANSPORTATION_AND_STORAGE.getDisplayName())
    );
  }

  @Test
  void getAllAsMap_hydrogen() {
    var result = FieldStageSubCategory.getAllAsMap(FieldStage.HYDROGEN);

    assertThat(result.entrySet()).containsExactlyInAnyOrder(
        entry(FieldStageSubCategory.OFFSHORE_HYDROGEN.name(), FieldStageSubCategory.OFFSHORE_HYDROGEN.getDisplayName()),
        entry(FieldStageSubCategory.ONSHORE_HYDROGEN.name(), FieldStageSubCategory.ONSHORE_HYDROGEN.getDisplayName())
    );
  }

  @Test
  void getAllAsMap_electrification() {
    var result = FieldStageSubCategory.getAllAsMap(FieldStage.ELECTRIFICATION);

    assertThat(result.entrySet()).containsExactlyInAnyOrder(
        entry(FieldStageSubCategory.OFFSHORE_ELECTRIFICATION.name(), FieldStageSubCategory.OFFSHORE_ELECTRIFICATION.getDisplayName()),
        entry(FieldStageSubCategory.ONSHORE_ELECTRIFICATION.name(), FieldStageSubCategory.ONSHORE_ELECTRIFICATION.getDisplayName())
    );
  }

  @Test
  void getAllAsMap_windEnergy() {
    var result = FieldStageSubCategory.getAllAsMap(FieldStage.WIND_ENERGY);

    assertThat(result.entrySet()).containsExactlyInAnyOrder(
        entry(FieldStageSubCategory.FLOATING_OFFSHORE_WIND.name(), FieldStageSubCategory.FLOATING_OFFSHORE_WIND.getDisplayName()),
        entry(FieldStageSubCategory.FIXED_BOTTOM_OFFSHORE_WIND.name(), FieldStageSubCategory.FIXED_BOTTOM_OFFSHORE_WIND.getDisplayName()),
        entry(FieldStageSubCategory.ONSHORE_WIND.name(), FieldStageSubCategory.ONSHORE_WIND.getDisplayName())
    );
  }

  @Test
  void getAllAsMap_invalidFieldStage() {
    var result = FieldStageSubCategory.getAllAsMap(FieldStage.DISCOVERY);

    assertThat(result).isEmpty();
  }

  @Test
  void getAllFieldStagesWithSubCategories() {
    var result = FieldStageSubCategory.getAllFieldStagesWithSubCategories();
    assertThat(result).containsExactlyInAnyOrder(
        FieldStage.CARBON_CAPTURE_AND_STORAGE,
        FieldStage.HYDROGEN,
        FieldStage.ELECTRIFICATION,
        FieldStage.WIND_ENERGY
    );
  }

  @ParameterizedTest
  @EnumSource(value = FieldStageSubCategory.class)
  void getEntryAsMap(FieldStageSubCategory fieldStageSubCategory) {
    var result = FieldStageSubCategory.getEntryAsMap(fieldStageSubCategory);
    assertThat(result).containsEntry(fieldStageSubCategory.name(), fieldStageSubCategory.getDisplayName());
  }
}
