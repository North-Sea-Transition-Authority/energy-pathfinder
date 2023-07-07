package uk.co.ogauthority.pathfinder.model.enums.project;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class FieldStageSubCategoryTest {

  @Test
  void getAllAsMap_offshoreWind() {
    var result = FieldStageSubCategory.getAllAsMap(FieldStage.OFFSHORE_WIND);

    assertThat(result.entrySet()).containsExactlyInAnyOrder(
        entry(FieldStageSubCategory.FLOATING_OFFSHORE_WIND.name(), FieldStageSubCategory.FLOATING_OFFSHORE_WIND.getDisplayName()),
        entry(FieldStageSubCategory.FIXED_BOTTOM_OFFSHORE_WIND.name(), FieldStageSubCategory.FIXED_BOTTOM_OFFSHORE_WIND.getDisplayName())
    );
  }

  @Test
  void getAllAsMap_carbonCaptureAndStorage() {
    var result = FieldStageSubCategory.getAllAsMap(FieldStage.CARBON_CAPTURE_AND_STORAGE);

    assertThat(result.entrySet()).containsExactlyInAnyOrder(
        entry(FieldStageSubCategory.CAPTURE_AND_ONSHORE.name(), FieldStageSubCategory.CAPTURE_AND_ONSHORE.getDisplayName()),
        entry(FieldStageSubCategory.TRANSPORTATION_AND_STORAGE.name(), FieldStageSubCategory.TRANSPORTATION_AND_STORAGE.getDisplayName())
    );
  }

  @Test
  void getAllAsMap_invalidFieldStage() {
    var result = FieldStageSubCategory.getAllAsMap(FieldStage.DISCOVERY);

    assertThat(result).isEmpty();
  }
}
