package uk.co.ogauthority.pathfinder.model.enums.project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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

  @Test
  void isEnergyTransition_fieldStageEnergyTypeIsNotEnergyTransition() {
    assertThat(FieldStage.isEnergyTransition(FieldStage.OIL_AND_GAS)).isFalse();
  }

  @ParameterizedTest
  @EnumSource(
      value = FieldStage.class,
      names = {"CARBON_CAPTURE_AND_STORAGE", "HYDROGEN", "ELECTRIFICATION", "WIND_ENERGY"}
  )
  void isEnergyTransition_fieldStageEnergyTypeIsEnergyTransition(FieldStage fieldStage) {
    assertThat(FieldStage.isEnergyTransition(fieldStage)).isTrue();
  }
}
