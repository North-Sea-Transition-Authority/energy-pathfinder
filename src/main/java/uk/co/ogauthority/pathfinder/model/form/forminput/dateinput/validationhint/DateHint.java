package uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.DateInput;

public abstract class DateHint {

  public static final String TODAY_DATE_LABEL = "today's date";

  private final FormInputLabel formInputLabel;
  private final LocalDate date;
  private final String dateLabel;

  public DateHint(FormInputLabel formInputLabel, LocalDate date, String dateLabel) {
    this.formInputLabel = formInputLabel;
    this.date = date;
    this.dateLabel = dateLabel;
  }

  public LocalDate getDateToTestAgainst() {
    return date;
  }

  public String getDateToTestAgainstLabel() {
    return dateLabel;
  }

  public String getInitCappedFormInputLabel() {
    return formInputLabel.getInitCappedLabel();
  }

  public DateInput castToDateInput(Object objectToTest) {
    return (DateInput) objectToTest;
  }
}
