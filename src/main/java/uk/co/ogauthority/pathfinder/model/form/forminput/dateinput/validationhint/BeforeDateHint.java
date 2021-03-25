package uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.ValidationHint;

public final class BeforeDateHint extends DateHint implements ValidationHint {

  public static final String BEFORE_DATE_CODE = FieldValidationErrorCodes.BEFORE_SOME_DATE.getCode();

  public BeforeDateHint(FormInputLabel formInputLabel, LocalDate date, String dateLabel) {
    super(formInputLabel, date, dateLabel);
  }

  @Override
  public boolean isValid(Object objectToTest) {
    var dateInput = castToDateInput(objectToTest);
    return dateInput.isBefore(getDateToTestAgainst());
  }

  @Override
  public String getErrorMessage() {
    return getInitCappedFormInputLabel() + " must be before " + getDateToTestAgainstLabel();
  }

  @Override
  public String getCode() {
    return BEFORE_DATE_CODE;
  }
}
