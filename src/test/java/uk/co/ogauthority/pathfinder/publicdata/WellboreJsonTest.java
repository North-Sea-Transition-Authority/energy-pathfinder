package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.WellboreTestUtil;

class WellboreJsonTest {

  @Test
  void from() {
    var wellbore = WellboreTestUtil.createWellbore();

    var wellboreJson = WellboreJson.from(wellbore);

    var expectedWellboreJson = new WellboreJson(wellboreJson.registrationNumber(), wellboreJson.mechanicalStatus());

    assertThat(wellboreJson).isEqualTo(expectedWellboreJson);
  }
}
