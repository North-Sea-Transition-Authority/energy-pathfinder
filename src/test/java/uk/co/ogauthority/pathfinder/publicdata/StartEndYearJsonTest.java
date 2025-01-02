package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StartEndYearJsonTest {

  @Test
  void from() {
    var startEndYearJson = StartEndYearJson.from("2024", "2025");

    var expectedStartEndYearJson = new StartEndYearJson(2024, 2025);

    assertThat(startEndYearJson).isEqualTo(expectedStartEndYearJson);
  }
}
