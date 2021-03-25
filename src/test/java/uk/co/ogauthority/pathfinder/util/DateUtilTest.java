package uk.co.ogauthority.pathfinder.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;

@RunWith(MockitoJUnitRunner.class)
public class DateUtilTest {

  @Test
  public void formatDate() {
    var testDate = LocalDate.now();
    var expected = testDate.format(DateUtil.DATE_FORMATTER);
    assertThat(DateUtil.formatDate(testDate)).isEqualTo(expected);
  }

  @Test
  public void formatDate_whenNull() {
    var expected = "";
    assertThat(DateUtil.formatDate(null)).isEqualTo(expected);
  }

  @Test
  public void formatInstant() {
    var testInstant = Instant.now();
    var expected = DateUtil.DATE_TIME_FORMATTER.format(testInstant);
    assertThat(DateUtil.formatInstant(testInstant)).isEqualTo(expected);
  }

  @Test
  public void formatInstant_whenNull() {
    var expected = "";
    assertThat(DateUtil.formatInstant(null)).isEqualTo(expected);
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

  @Test
  public void isOnOrBefore_whenBefore_thenTrue() {
    final var maxInstant = Instant.now();
    final var instantToTest = maxInstant.minus(1, ChronoUnit.DAYS);
    assertThat(DateUtil.isOnOrBefore(instantToTest, maxInstant)).isTrue();
  }

  @Test
  public void isOnOrBefore_whenOn_thenTrue() {
    final var maxInstant = Instant.now();
    assertThat(DateUtil.isOnOrBefore(maxInstant, maxInstant)).isTrue();
  }

  @Test
  public void isOnOrBefore_whenAfter_thenFalse() {
    final var maxInstant = Instant.now();
    final var instantToTest = maxInstant.plus(1, ChronoUnit.DAYS);
    assertThat(DateUtil.isOnOrBefore(instantToTest, maxInstant)).isFalse();
  }

  @Test
  public void isOnOrAfter_whenAfter_thenTrue() {
    final var maxInstant = Instant.now();
    final var instantToTest = maxInstant.plus(1, ChronoUnit.DAYS);
    assertThat(DateUtil.isOnOrAfter(instantToTest, maxInstant)).isTrue();
  }

  @Test
  public void isOnOrAfter_whenOn_thenTrue() {
    final var maxInstant = Instant.now();
    assertThat(DateUtil.isOnOrAfter(maxInstant, maxInstant)).isTrue();
  }

  @Test
  public void isOnOrAfter_whenBefore_thenFalse() {
    final var maxInstant = Instant.now();
    final var instantToTest = maxInstant.minus(1, ChronoUnit.DAYS);
    assertThat(DateUtil.isOnOrAfter(instantToTest, maxInstant)).isFalse();
  }

  @Test
  public void getQuarterFromLocalDate() {
    final var firstQuarterDate = LocalDate.of(2021, 1, 1);
    assertThat(DateUtil.getQuarterFromLocalDate(firstQuarterDate)).isEqualTo(Quarter.Q1);

    final var secondQuarterDate = LocalDate.of(2021, 4, 1);
    assertThat(DateUtil.getQuarterFromLocalDate(secondQuarterDate)).isEqualTo(Quarter.Q2);

    final var thirdQuarterDate = LocalDate.of(2021, 7, 1);
    assertThat(DateUtil.getQuarterFromLocalDate(thirdQuarterDate)).isEqualTo(Quarter.Q3);

    final var fourthQuarterDate = LocalDate.of(2021, 10, 1);
    assertThat(DateUtil.getQuarterFromLocalDate(fourthQuarterDate)).isEqualTo(Quarter.Q4);
  }

  @Test
  public void isInCurrentQuarter_whenInCurrentQuarter() {
    assertThat(DateUtil.isInCurrentQuarter(Instant.now())).isTrue();
  }

  @Test
  public void isInCurrentQuarter_whenNotInCurrentQuarter() {
    assertThat(DateUtil.isInCurrentQuarter(Instant.now().minus(200, ChronoUnit.DAYS))).isFalse();
  }
}
