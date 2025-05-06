package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;

class QuarterYearJsonTest {

  @Test
  void from() {
    var quarterYearJson = QuarterYearJson.from(Quarter.Q1, 2025);

    var expectedQuarterYearJson = new QuarterYearJson(
        Quarter.Q1.name(),
        2025
    );

    assertThat(quarterYearJson).isEqualTo(expectedQuarterYearJson);
  }
}
