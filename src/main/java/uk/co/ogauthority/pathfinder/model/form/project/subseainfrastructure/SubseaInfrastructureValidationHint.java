package uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure;

import java.util.ArrayList;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MaxYearMustBeInFutureHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MinMaxYearLabelsHint;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;

public final class SubseaInfrastructureValidationHint {

  public static final FormInputLabel DECOM_DATE_LABEL = new FormInputLabel("decommissioning date");
  public static final MinMaxYearLabelsHint DECOM_YEAR_LABELS = new MinMaxYearLabelsHint("earliest", "latest");
  public static final MaxYearMustBeInFutureHint DECOM_MAX_YEAR_HINT = new MaxYearMustBeInFutureHint(
      DECOM_DATE_LABEL,
      DECOM_YEAR_LABELS
  );

  private final ValidationType validationType;

  public SubseaInfrastructureValidationHint(ValidationType validationType) {
    this.validationType = validationType;
  }

  public ValidationType getValidationType() {
    return validationType;
  }

  public Object[] getDecommissioningDateHints() {
    var hints = new ArrayList<>();
    hints.add(DECOM_DATE_LABEL);
    hints.add(DECOM_YEAR_LABELS);
    hints.add(DECOM_MAX_YEAR_HINT);

    MinMaxDateInputValidator.addEmptyMinMaxDateAcceptableHint(validationType, hints);

    return hints.toArray();
  }
}
