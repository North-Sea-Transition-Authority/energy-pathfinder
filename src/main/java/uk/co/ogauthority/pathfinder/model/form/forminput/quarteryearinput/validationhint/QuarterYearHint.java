package uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint;

import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.DateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;

public abstract class QuarterYearHint {

  public static final String CURRENT_QUARTER_YEAR_LABEL = "the current quarter";

  private final FormInputLabel formInputLabel;
  private final QuarterYearInput quarterYearInputToValidateAgainst;
  private final String quarterYearInputToValidateAgainstLabel;

  protected QuarterYearHint(FormInputLabel formInputLabel,
                            QuarterYearInput quarterYearInputToValidateAgainst,
                            String quarterYearInputToValidateAgainstLabel) {
    this.formInputLabel = formInputLabel;
    this.quarterYearInputToValidateAgainst = quarterYearInputToValidateAgainst;
    this.quarterYearInputToValidateAgainstLabel = quarterYearInputToValidateAgainstLabel;
  }

  public FormInputLabel getFormInputLabel() {
    return formInputLabel;
  }

  public QuarterYearInput getQuarterYearInputToValidateAgainst() {
    return quarterYearInputToValidateAgainst;
  }

  public String getQuarterYearInputToValidateAgainstLabel() {
    return quarterYearInputToValidateAgainstLabel;
  }

  public String getInitCappedFormInputLabel() {
    return formInputLabel.getInitCappedLabel();
  }

  public QuarterYearInput castToQuarterYearInput(Object object) {
    return (QuarterYearInput) object;
  }

  public LocalDate getQuarterYearAsLocalDate(QuarterYearInput quarterYearInput) {

    final var startDateOfQuarter = quarterYearInput.getQuarter().getStartDate();

    return LocalDate.of(
        Integer.parseInt(quarterYearInput.getYear()),
        startDateOfQuarter.getMonthValue(),
        startDateOfQuarter.getDayOfMonth()
    );
  }

  public DateInput getQuarterYearAsDateInput(QuarterYearInput quarterYearInput) {
    return new ThreeFieldDateInput(getQuarterYearAsLocalDate(quarterYearInput));
  }
}
