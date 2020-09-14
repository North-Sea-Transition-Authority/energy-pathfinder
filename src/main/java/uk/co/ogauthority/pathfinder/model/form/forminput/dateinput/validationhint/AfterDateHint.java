package uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.ValidationHint;

public final class AfterDateHint extends DateHint implements ValidationHint {

  public static final String AFTER_DATE_CODE = FieldValidationErrorCodes.AFTER_SOME_DATE.getCode();

  public AfterDateHint(FormInputLabel formInputLabel, LocalDate date, String dateLabel) {
    super(formInputLabel, date, dateLabel);
  }

  @Override
  public boolean isValid(Object objectToTest) {
    var dateInput = castToDateInput(objectToTest);
    return dateInput.isAfter(getDateToTestAgainst());
  }

  @Override
  public String getErrorMessage() {
    return getInitCappedFormInputLabel() + " must be after " + getDateToTestAgainstLabel();
  }

  @Override
  public String getCode() {
    return AFTER_DATE_CODE;
  }
}
