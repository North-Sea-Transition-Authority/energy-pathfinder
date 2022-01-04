package uk.co.ogauthority.pathfinder.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.Arrays;
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
    final var timeInCurrentQuarter = DateUtil.getQuarterFromLocalDate(LocalDate.now()).getEndDateAsInstant();
    assertThat(DateUtil.isInCurrentQuarter(timeInCurrentQuarter)).isTrue();
  }

  @Test
  public void isInCurrentQuarter_lastDayOfQuarter_atEndOfDay() {
    final var timeInCurrentQuarter = LocalDateTime.of(LocalDate.now().getYear(), 6, 30, 23, 59, 59, 999999999).atZone(ZoneId.systemDefault()).toInstant();
    assertThat(Quarter.Q2.getEndDateAsInstant()).isEqualTo(timeInCurrentQuarter);
  }

  @Test
  public void isInCurrentQuarter_whenNotInCurrentQuarter() {
    assertThat(DateUtil.isInCurrentQuarter(Instant.now().minus(200, ChronoUnit.DAYS))).isFalse();
  }

  @Test
  public void getStartOfMonth_assertCorrectReturnValue() {
    final var localDateToTest = LocalDate.of(2021, 4, 20);

    final var expectedLocalDate = LocalDate.of(2021, 4, 1);
    final var expectedLocalTime = LocalTime.MIN;
    final var expectedReturnValue = LocalDateTime.of(expectedLocalDate, expectedLocalTime).atZone(DateUtil.UTC_ZONE).toInstant();

    final var result = DateUtil.getStartOfMonth(localDateToTest);
    assertThat(result).isEqualTo(expectedReturnValue);
  }

  @Test
  public void getEndOfMonth_assertCorrectReturnValue() {
    final var localDateToTest = LocalDate.of(2021, 4, 20);

    final var expectedLocalDate = LocalDate.of(2021, 4, 30);
    final var expectedLocalTime = LocalTime.MAX;
    final var expectedReturnValue = LocalDateTime.of(expectedLocalDate, expectedLocalTime).atZone(DateUtil.UTC_ZONE).toInstant();

    final var result = DateUtil.getEndOfMonth(localDateToTest);
    assertThat(result).isEqualTo(expectedReturnValue);
  }

  @Test
  public void daysBetween_whenToDateInFuture_thenPositiveReturnValue() {
    var fromDate = LocalDate.now();
    var expectedDaysBetween = 5;
    var toDate = fromDate.plusDays(expectedDaysBetween);
    var resultingDaysBetween = DateUtil.daysBetween(fromDate, toDate);

    assertThat(resultingDaysBetween).isEqualTo(expectedDaysBetween);
  }

  @Test
  public void daysBetween_whenToDateInPast_thenNegativeReturnValue() {
    var fromDate = LocalDate.now();
    var expectedDaysBetween = -5;
    var toDate = fromDate.plusDays(expectedDaysBetween);
    var resultingDaysBetween = DateUtil.daysBetween(fromDate, toDate);

    assertThat(resultingDaysBetween).isEqualTo(expectedDaysBetween);
  }

  // This test is to ensure that when the deadline date is more than 1 month in the future
  // that the days between today and the deadline are correctly returned. A bug was spotted in
  // a previous implementation whereby the Period.class was being used and that resulted in a value in months being
  // returned instead of a value in days. This test is to ensure this erroneous behaviour is never reintroduced
  @Test
  public void daysBetween_whenToDateMoreThanAMonthInFuture_thenPositiveReturnValue() {
    var fromDate = LocalDate.now();
    var expectedDaysBetween = 50;
    var toDate = fromDate.plusDays(expectedDaysBetween);
    var resultingDaysBetween = DateUtil.daysBetween(fromDate, toDate);

    assertThat(resultingDaysBetween).isEqualTo(expectedDaysBetween);
  }

  @Test
  public void getCurrentQuarter_verifyExpectedReturnValue() {

    var expectedCurrentQuarter = deriveCurrentQuarter();

    var resultingCurrentQuarter = DateUtil.getCurrentQuarter();

    assertThat(resultingCurrentQuarter).isEqualTo(expectedCurrentQuarter);
  }

  private Quarter deriveCurrentQuarter() {

    var currentQuarterIndex = LocalDate.now().get(IsoFields.QUARTER_OF_YEAR);

    return Arrays.stream(Quarter.values())
        .filter(quarter -> quarter.getDisplayValue().equals(currentQuarterIndex))
        .findFirst()
        .orElseThrow(() -> new RuntimeException(
            String.format("Could not determine current quarter using index %s", currentQuarterIndex))
        );
  }
}
