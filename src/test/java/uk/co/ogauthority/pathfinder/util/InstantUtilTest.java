package uk.co.ogauthority.pathfinder.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InstantUtilTest {

  @Test
  public void formatInstant() {
    var testInstant = Instant.now();
    var expected = InstantUtil.DATE_FORMATTER.format(testInstant);
    assertThat(InstantUtil.formatInstant(testInstant)).isEqualTo(expected);
  }

  @Test
  public void formatInstant_whenNull() {
    var expected = "";
    assertThat(InstantUtil.formatInstant(null)).isEqualTo(expected);
  }
}
