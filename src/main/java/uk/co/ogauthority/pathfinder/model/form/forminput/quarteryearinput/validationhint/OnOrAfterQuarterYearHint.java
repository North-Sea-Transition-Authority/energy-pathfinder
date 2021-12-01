package uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint;

import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.OnOrAfterDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.ValidationHint;

public class OnOrAfterQuarterYearHint extends QuarterYearHint implements ValidationHint {

  public static final String ERROR_MESSAGE_TEXT = "%s must be the same as or after %s";
  public static final String ON_OR_AFTER_QUARTER_YEAR_CODE = FieldValidationErrorCodes.AFTER_SOME_DATE.getCode();

  public OnOrAfterQuarterYearHint(FormInputLabel formInputLabel,
                                  QuarterYearInput quarterYearInputToValidateAgainst,
                                  String quarterYearInputToValidateAgainstLabel) {
    super(
        formInputLabel,
        quarterYearInputToValidateAgainst,
        quarterYearInputToValidateAgainstLabel
    );
  }

  @Override
  public boolean isValid(Object objectToTest) {

    final var quarterYearInputToTest = castToQuarterYearInput(objectToTest);

    final var quarterYearToValidateAgainstAsLocalDate = getQuarterYearAsLocalDate(getQuarterYearInputToValidateAgainst());

    return new OnOrAfterDateHint(
        getFormInputLabel(),
        quarterYearToValidateAgainstAsLocalDate,
        getQuarterYearInputToValidateAgainstLabel()
    ).isValid(getQuarterYearAsDateInput(quarterYearInputToTest));
  }

  @Override
  public String getErrorMessage() {
    return String.format(
        ERROR_MESSAGE_TEXT,
        getInitCappedFormInputLabel(),
        getQuarterYearInputToValidateAgainstLabel()
    );
  }

  @Override
  public String getCode() {
    return ON_OR_AFTER_QUARTER_YEAR_CODE;
  }
}
