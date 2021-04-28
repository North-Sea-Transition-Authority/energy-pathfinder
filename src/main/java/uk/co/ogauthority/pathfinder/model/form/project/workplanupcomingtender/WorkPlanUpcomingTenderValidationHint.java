package uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender;

import java.time.LocalDate;
import java.util.ArrayList;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.AfterDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.DateHint;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;

public class WorkPlanUpcomingTenderValidationHint {

  public static final FormInputLabel ESTIMATED_TENDER_LABEL = new FormInputLabel("estimated tender date");
  public static final String DATE_ERROR_LABEL = DateHint.TODAY_DATE_LABEL;

  private final ValidationType validationType;
  private final AfterDateHint estimatedTenderDateHint;

  public WorkPlanUpcomingTenderValidationHint(ValidationType validationType) {
    this.validationType = validationType;
    this.estimatedTenderDateHint = new AfterDateHint(
        ESTIMATED_TENDER_LABEL,
        LocalDate.now(),
        DATE_ERROR_LABEL
    );
  }

  public Object[] getEstimatedTenderDateHint() {
    var hints = new ArrayList<>();
    hints.add(estimatedTenderDateHint);
    hints.add(ESTIMATED_TENDER_LABEL);
    DateInputValidator.addEmptyDateAcceptableHint(validationType, hints);
    return hints.toArray();
  }

  protected ValidationType getValidationType() {
    return validationType;
  }
}