package uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint.EmptyQuarterYearAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint.OnOrAfterQuarterYearHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint.QuarterYearHint;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class WorkPlanUpcomingTenderValidationHint {

  public static final FormInputLabel ESTIMATED_TENDER_LABEL = new FormInputLabel("estimated tender date");

  private final ValidationType validationType;

  public WorkPlanUpcomingTenderValidationHint(ValidationType validationType) {
    this.validationType = validationType;
  }

  public Object[] getEstimatedTenderDateHint() {
    var hints = new ArrayList<>();
    hints.add(ESTIMATED_TENDER_LABEL);
    addEmptyQuarterYearAcceptableHint(hints, validationType);

    final var currentDate = LocalDate.now();
    final var estimatedTenderDateHint = new OnOrAfterQuarterYearHint(
        ESTIMATED_TENDER_LABEL,
        new QuarterYearInput(DateUtil.getQuarterFromLocalDate(currentDate), String.valueOf(currentDate.getYear())),
        QuarterYearHint.CURRENT_QUARTER_YEAR_LABEL
    );

    hints.add(estimatedTenderDateHint);

    return hints.toArray();
  }

  private void addEmptyQuarterYearAcceptableHint(List<Object> validationHints, ValidationType validationType) {
    if (isPartialValidation(validationType)) {
      validationHints.add(new EmptyQuarterYearAcceptableHint());
    }
  }

  private boolean isPartialValidation(ValidationType validationType) {
    return validationType.equals(ValidationType.PARTIAL);
  }

  protected ValidationType getValidationType() {
    return validationType;
  }
}