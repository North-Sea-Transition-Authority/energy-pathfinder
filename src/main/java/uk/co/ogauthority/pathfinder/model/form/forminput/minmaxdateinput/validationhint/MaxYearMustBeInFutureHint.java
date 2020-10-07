package uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint;

import uk.co.ogauthority.pathfinder.model.form.validation.ValidationHint;

public class MaxYearMustBeInFutureHint implements ValidationHint {
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
