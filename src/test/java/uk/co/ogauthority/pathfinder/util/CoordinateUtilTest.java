package uk.co.ogauthority.pathfinder.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CoordinateUtilTest {

  @Test
  void formatCoordinate_withAllValuesNotNull() {
    var result = CoordinateUtil.formatCoordinate(51, 30, 25.3, "NORTH");
    assertThat(result).isEqualTo("51° 30' 25.3\" North");
  }

  @Test
  void formatCoordinate_withDegreesNull() {
    var result = CoordinateUtil.formatCoordinate(null, 30, 25.3, "NORTH");
    assertThat(result).isEqualTo("30' 25.3\" North");
  }

  @Test
  void formatCoordinate_withMinutesNull() {
    var result = CoordinateUtil.formatCoordinate(51, null, 25.3, "NORTH");
    assertThat(result).isEqualTo("51° 25.3\" North");
  }

  @Test
  void formatCoordinate_withSecondsNull() {
    var result = CoordinateUtil.formatCoordinate(51, 30, null, "NORTH");
    assertThat(result).isEqualTo("51° 30' North");
  }

  @Test
  void formatCoordinate_withHemisphereNull() {
    var result = CoordinateUtil.formatCoordinate(51, 30, 25.3, null);
    assertThat(result).isEqualTo("51° 30' 25.3\"");
  }

  @Test
  void formatCoordinate_withAllValuesNull() {
    var result = CoordinateUtil.formatCoordinate(null, null, null, null);
    assertThat(result).isEmpty();
  }
}
