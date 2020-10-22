package uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint;

import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.ValidationHint;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;

public class MaxYearMustBeInFutureHint implements ValidationHint {

  public static final String MAX_YEAR_IN_FUTURE_ERROR = "%s's %s year must be the current year or in the future";

  private final FormInputLabel inputLabel;
  private final MinMaxYearLabelsHint minMaxYearLabelsHint;

  public MaxYearMustBeInFutureHint(FormInputLabel inputLabel, MinMaxYearLabelsHint yearLabels) {
    this.inputLabel = inputLabel;
    this.minMaxYearLabelsHint = yearLabels;
  }

  @Override
  public boolean isValid(Object objectToTest) {
    var minMaxDateInput = (MinMaxDateInput) objectToTest;
    return minMaxDateInput.getMaxYear() == null || minMaxDateInput.maxYearIsInFuture();
  }

  @Override
  public String getErrorMessage() {
    return getMaxYearMustBeInFutureErrorMessage(inputLabel, minMaxYearLabelsHint);
  }

  @Override
  public String getCode() {
    return MinMaxDateInputValidator.MAX_YEAR + FieldValidationErrorCodes.INVALID.getCode();
  }

  private String getMaxYearMustBeInFutureErrorMessage(FormInputLabel inputLabel, MinMaxYearLabelsHint yearLabels) {
    return String.format(MAX_YEAR_IN_FUTURE_ERROR, inputLabel.getInitCappedLabel(), yearLabels.getMaxYearLabel());
  }
}
