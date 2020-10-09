package uk.co.ogauthority.pathfinder.model.form.forminput.dateinput;

import com.google.common.annotations.VisibleForTesting;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a two field date commonly used on forms and provides access to common operations that might be applied to that date.
 * e.g testing if a given date is before or after etc.
 */
public class TwoFieldDateInput extends DateInputCommonAccessor implements DateInput {
  private static final Logger LOGGER = LoggerFactory.getLogger(TwoFieldDateInput.class);

  private static final int DEFAULT_DAY = 1;

  public TwoFieldDateInput() {
  }

  public TwoFieldDateInput(LocalDate localDate) {
    this.year = localDate != null ? String.valueOf(localDate.getYear()) : null;
    this.month = localDate != null ? String.valueOf(localDate.getMonthValue()) : null;
  }

  @VisibleForTesting
  public TwoFieldDateInput(Integer year, Integer month) {
    this.year = year != null ? String.valueOf(year) : null;
    this.month = month != null ? String.valueOf(month) : null;
  }

  @Override
  public DateInputType getType() {
    return DateInputType.TWO_FIELD;
  }

  @Override
  public String getDay() {
    return String.valueOf(DEFAULT_DAY);
  }

  @Override
  public LocalDate createDateOrNull() {
    return this.createDate()
        .orElse(null);
  }

  @Override
  public Optional<LocalDate> createDate() {

    try {
      var createdDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), DEFAULT_DAY);
      return Optional.of(createdDate);
    } catch (NumberFormatException e) {
      LOGGER.debug(String.format("Could not convert date values to valid numbers. %s", this.toString()), e);
      return Optional.empty();
    } catch (DateTimeException e) {
      LOGGER.debug(String.format("Could not convert date values to valid date. %s", this.toString()), e);
      return Optional.empty();
    }
  }

  @Override
  public boolean isBefore(LocalDate testDate) {
    var testableDate = testDate.withDayOfMonth(DEFAULT_DAY);

    return this.createDate()
        .filter(date -> date.isBefore(testableDate))
        .isPresent();

  }

  @Override
  public boolean isAfter(LocalDate testDate) {
    var testableDate = testDate.withDayOfMonth(DEFAULT_DAY);
    return this.createDate()
        .filter(date -> date.isAfter(testableDate))
        .isPresent();
  }

  @Override
  public boolean isEqualTo(LocalDate testDate) {
    var testableDate = testDate.withDayOfMonth(DEFAULT_DAY);
    return this.createDate()
        .filter(date -> date.equals(testableDate))
        .isPresent();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TwoFieldDateInput that = (TwoFieldDateInput) o;
    return Objects.equals(month, that.month)
        && Objects.equals(year, that.year);
  }

  @Override
  public int hashCode() {
    return Objects.hash(month, year);
  }

  @Override
  public String toString() {
    return "TwoFieldDateInput{" +
        "month='" + month + '\'' +
        ", year='" + year + '\'' +
        '}';
  }
}
