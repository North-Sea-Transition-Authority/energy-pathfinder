package uk.co.ogauthority.pathfinder.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.Quarter;

@RunWith(MockitoJUnitRunner.class)
public class DateUtilTest {

  @Test
  public void format() {
    var testDate = LocalDate.now();
    var expected = testDate.format(DateUtil.DATE_FORMATTER);
    assertThat(DateUtil.format(testDate)).isEqualTo(expected);
  }

  @Test
  public void format_whenNull() {
    var expected = "";
    assertThat(DateUtil.format(null)).isEqualTo(expected);
  }

  @Test
  public void format_withDateTimeFormatter() {
    var testInstant = Instant.now();
    var expected = DateUtil.DATE_TIME_FORMATTER.format(testInstant);
    assertThat(DateUtil.format(testInstant, DateUtil.DATE_TIME_FORMATTER)).isEqualTo(expected);
  }

  @Test
  public void format_withDateTimeFormatter_whenNull() {
    var expected = "";
    assertThat(DateUtil.format(null, DateUtil.DATE_TIME_FORMATTER)).isEqualTo(expected);
  }

  @Test
  public void getDateFromQuarterYear() {
    var quarter = Quarter.Q1;
    var year = 2020;
    var expected = "Q1 2020";
    assertThat(DateUtil.getDateFromQuarterYear(quarter, year)).isEqualTo(expected);
  }

  @Test
  public void getDateFromQuarterYear_whenNull() {
    var year = 2020;
    var expected = "";
    assertThat(DateUtil.getDateFromQuarterYear(null, year)).isEqualTo(expected);
  }
}
