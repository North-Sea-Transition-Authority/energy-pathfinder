package uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.ValidationHint;

public final class OnOrAfterDateHint extends DateHint implements ValidationHint {

  public OnOrAfterDateHint(FormInputLabel formInputLabel, LocalDate date, String dateLabel) {
    super(formInputLabel, date, dateLabel);
  }

  @Override
  public boolean isValid(Object objectToTest) {
    var dateInput = castToDateInput(objectToTest);
    return (dateInput.isAfter(getDateToTestAgainst()) || dateInput.isEqualTo(getDateToTestAgainst()));
  }

  @Override
  public String getErrorMessage() {
    return getInitCappedFormInputLabel() + " must be the same as or after " + getDateToTestAgainstLabel();
  }

  @Override
  public String getCode() {
    return FieldValidationErrorCodes.AFTER_SOME_DATE.getCode();
  }
}
