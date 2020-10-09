package uk.co.ogauthority.pathfinder.model.form.forminput.dateinput;

import com.google.common.annotations.VisibleForTesting;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a three field date commonly used on forms and provides access to common operations that might be
 * applied to that date.
 * e.g testing if a given date is before or after etc.
 */
public class ThreeFieldDateInput extends DateInputCommonAccessor implements DateInput {

  private static final Logger LOGGER = LoggerFactory.getLogger(ThreeFieldDateInput.class);

  private String day;

  public ThreeFieldDateInput() {
  }

  public ThreeFieldDateInput(LocalDate localDate) {
    this.year = localDate != null ? String.valueOf(localDate.getYear()) : null;
    this.month = localDate != null ? String.valueOf(localDate.getMonthValue()) : null;
    this.day = localDate != null ? String.valueOf(localDate.getDayOfMonth()) : null;
  }

  @VisibleForTesting
  public ThreeFieldDateInput(Integer year, Integer month, Integer day) {
    this.year = year != null ? String.valueOf(year) : null;
    this.month = month != null ? String.valueOf(month) : null;
    this.day = day != null ? String.valueOf(day) : null;
  }

  @Override
  public String getDay() {
    return day;
  }

  public void setDay(String day) {
    this.day = day;
  }

  public void setDay(int day) {
    this.day = String.valueOf(day);
  }

  @Override
  public DateInputType getType() {
    return DateInputType.THREE_FIELD;
  }

  @Override
  public LocalDate createDateOrNull() {
    return this.createDate()
        .orElse(null);
  }

  @Override
  public Optional<LocalDate> createDate() {

    try {
      var createdDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
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
    return this.createDate()
        .filter(date -> date.isBefore(testDate))
        .isPresent();

  }

  @Override
  public boolean isAfter(LocalDate testDate) {
    return this.createDate()
        .filter(date -> date.isAfter(testDate))
        .isPresent();
  }

  @Override
  public boolean isEqualTo(LocalDate testDate) {
    return this.createDate()
        .filter(date -> date.equals(testDate))
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
    ThreeFieldDateInput that = (ThreeFieldDateInput) o;
    return Objects.equals(day, that.day)
        && Objects.equals(month, that.month)
        && Objects.equals(year, that.year);
  }

  @Override
  public int hashCode() {
    return Objects.hash(day, month, year);
  }

  @Override
  public String toString() {
    return "ThreeFieldDateInput{" +
        "day='" + day + '\'' +
        ", month='" + month + '\'' +
        ", year='" + year + '\'' +
        '}';
  }
}
