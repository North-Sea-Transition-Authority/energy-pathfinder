package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CoordinateJsonTest {

  @Test
  void from_allValuesNull() {
    var coordinateJson = CoordinateJson.from(null, null, null, null);

    assertThat(coordinateJson).isNull();
  }

  @ParameterizedTest
  @CsvSource(
      value = {
        "null, 30, 32.4, NORTH",
        "51, null, 32.4, NORTH",
        "51, 30, null, NORTH",
        "51, 30, 32.4, null",
        "null, null, null, NORTH",
        "51, 30, 32.4, NORTH"
      },
      nullValues = "null"
  )
  void from(Integer degrees, Integer minutes, Double seconds, String hemisphere) {
    var coordinateJson = CoordinateJson.from(degrees, minutes, seconds, hemisphere);

    var expectedCoordinateJson = new CoordinateJson(degrees, minutes, seconds, hemisphere);

    assertThat(coordinateJson).isEqualTo(expectedCoordinateJson);
  }
}
