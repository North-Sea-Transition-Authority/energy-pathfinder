package uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;

public class QuarterYearInput {

  private static final Logger LOGGER = LoggerFactory.getLogger(QuarterYearInput.class);

  private Quarter quarter;
  private String year;

  public Quarter getQuarter() {
    return quarter;
  }

  public void setQuarter(Quarter quarter) {
    this.quarter = quarter;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public void setYear(Integer year) {
    this.year = (year == null) ? null : String.valueOf(year);
  }

  public QuarterYearInput() {}

  @VisibleForTesting
  public QuarterYearInput(Quarter quarter, String year) {
    this.quarter = quarter;
    this.year = year;
  }

  public QuarterYearInput createOrNull() {
    return create().orElse(null);
  }

  public Optional<QuarterYearInput> create() {

    if (quarter == null || year == null) {
      return Optional.empty();
    } else {
      try {
        Integer.parseInt(year);
      } catch (NumberFormatException nfe) {
        LOGGER.debug(String.format("Could not convert year value to a valid number. %s", this.toString()), nfe);
        return Optional.empty();
      }
    }

    return Optional.of(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(quarter, year);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    QuarterYearInput that = (QuarterYearInput) obj;
    return Objects.equals(quarter, that.quarter)
        && Objects.equals(year, that.year);
  }

  @Override
  public String toString() {
    return "QuarterYearInput{" +
        "quarter='" + quarter + '\'' +
        ", year='" + year + '\'' +
        '}';
  }
}
