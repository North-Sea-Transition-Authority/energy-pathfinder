package uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint;

import uk.co.ogauthority.pathfinder.model.form.validation.ValidationHint;

public class MinMaxYearLabelsHint implements ValidationHint {

  private final String minYearLabel;
  private final String maxYearLabel;

  public MinMaxYearLabelsHint(String minYearLabel, String maxYearLabel) {
    this.minYearLabel = minYearLabel;
    this.maxYearLabel = maxYearLabel;
  }

  public String getMinYearLabel() {
    return minYearLabel;
  }

  public String getMaxYearLabel() {
    return maxYearLabel;
  }

  @Override
  public boolean isValid(Object objectToTest) {
    return true;
  }

  @Override
  public String getErrorMessage() {
    return null;
  }

  @Override
  public String getCode() {
    return null;
  }
}
