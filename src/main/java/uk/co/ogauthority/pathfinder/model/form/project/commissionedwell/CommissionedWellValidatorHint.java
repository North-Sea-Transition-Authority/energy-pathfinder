package uk.co.ogauthority.pathfinder.model.form.project.commissionedwell;

import java.util.ArrayList;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MaxYearMustBeInFutureHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MinMaxYearLabelsHint;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;

public final class CommissionedWellValidatorHint {

  static final FormInputLabel COMMISSIONING_SCHEDULE_LABEL = new FormInputLabel("well commissioning period");
  static final MinMaxYearLabelsHint COMMISSIONING_SCHEDULE_YEAR_LABELS = new MinMaxYearLabelsHint("earliest", "latest");

  private static final MaxYearMustBeInFutureHint COMMISSIONING_SCHEDULE_MAX_YEAR_HINT = new MaxYearMustBeInFutureHint(
      COMMISSIONING_SCHEDULE_LABEL,
      COMMISSIONING_SCHEDULE_YEAR_LABELS
  );

  private final ValidationType validationType;

  public CommissionedWellValidatorHint(ValidationType validationType) {
    this.validationType = validationType;
  }

  public Object[] getCommissioningScheduleHints() {
    var hints = new ArrayList<>();
    hints.add(COMMISSIONING_SCHEDULE_LABEL);
    hints.add(COMMISSIONING_SCHEDULE_YEAR_LABELS);
    hints.add(COMMISSIONING_SCHEDULE_MAX_YEAR_HINT);

    MinMaxDateInputValidator.addEmptyMinMaxDateAcceptableHint(validationType, hints);

    return hints.toArray();
  }

  public ValidationType getValidationType() {
    return validationType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CommissionedWellValidatorHint)) {
      return false;
    }
    CommissionedWellValidatorHint that = (CommissionedWellValidatorHint) o;
    return validationType == that.validationType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(validationType);
  }
}
