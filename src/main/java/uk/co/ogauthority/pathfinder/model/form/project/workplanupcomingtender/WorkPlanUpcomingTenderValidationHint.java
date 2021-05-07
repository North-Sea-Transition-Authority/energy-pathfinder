package uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender;

import java.util.ArrayList;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint.EmptyQuarterYearAcceptableHint;

public class WorkPlanUpcomingTenderValidationHint {

  public static final FormInputLabel ESTIMATED_TENDER_LABEL = new FormInputLabel("estimated tender date");

  private final ValidationType validationType;
  private final EmptyQuarterYearAcceptableHint emptyQuarterYearAcceptableHint;

  public WorkPlanUpcomingTenderValidationHint(ValidationType validationType) {
    this.validationType = validationType;
    this.emptyQuarterYearAcceptableHint = new EmptyQuarterYearAcceptableHint();
  }

  public Object[] getEstimatedTenderDateHint() {
    var hints = new ArrayList<>();
    hints.add(ESTIMATED_TENDER_LABEL);
    addEmptyQuarterYearAcceptableHint(hints, validationType);
    return hints.toArray();
  }

  private void addEmptyQuarterYearAcceptableHint(List<Object> validationHints, ValidationType validationType) {
    if (isPartialValidation(validationType)) {
      validationHints.add(emptyQuarterYearAcceptableHint);
    }
  }

  private boolean isPartialValidation(ValidationType validationType) {
    return validationType.equals(ValidationType.PARTIAL);
  }

  protected ValidationType getValidationType() {
    return validationType;
  }
}