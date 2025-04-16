package uk.co.ogauthority.pathfinder.model.enums.project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FieldStageTest {

  @Test
  void getEnergyTransitionProjectFieldStages() {
    var result = FieldStage.getEnergyTransitionProjectFieldStages();
    assertThat(result).containsOnly(
        FieldStage.CARBON_CAPTURE_AND_STORAGE,
        FieldStage.HYDROGEN,
        FieldStage.ELECTRIFICATION,
        FieldStage.WIND_ENERGY
    );
  }
}
