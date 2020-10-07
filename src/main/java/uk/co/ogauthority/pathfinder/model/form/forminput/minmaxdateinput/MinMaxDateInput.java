package uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput;


import java.time.LocalDate;

/**
 * Store a minimum and maximum year for form input use.
 * Contains some utility methods to validate certain criteria about each year.
 */
public class MinMaxDateInput {

  private String minYear;

  private String maxYear;

  public MinMaxDateInput(String minYear,
                         String maxYear) {
    this.minYear = minYear;
    this.maxYear = maxYear;
  }

  public MinMaxDateInput() {
  }

  public String getMinYear() {
    return minYear;
  }

  public void setMinYear(String minYear) {
    this.minYear = minYear;
  }

  public String getMaxYear() {
    return maxYear;
  }

  public void setMaxYear(String maxYear) {
    this.maxYear = maxYear;
  }

  /**
   * Check if the minYear is < maxYear.
   * @return true if min is less than max. False if otherwise or neither are a valid year
   */
  public boolean minIsBeforeMax() {
    try {
      var min = Integer.parseInt(getMinYear());
      var max = Integer.parseInt(getMaxYear());
      return min < max;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Check if the maxYear is in the future.
   * @return true if maxYear is in the future, false if not or if maxYear is an invalid number
   */
  public boolean maxYearIsInFuture() {
    try {
      var max = Integer.parseInt(getMaxYear());
      return max > LocalDate.now().getYear();
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
