package uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline;

import java.util.ArrayList;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MaxYearMustBeInFutureHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MinMaxYearLabelsHint;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;

public class DecommissionedPipelineValidationHint {

  public static final FormInputLabel DECOMMISSIONING_DATE_LABEL = new FormInputLabel("decommissioning date");

  public static final MinMaxYearLabelsHint DECOMMISSIONING_YEAR_LABELS = new MinMaxYearLabelsHint("earliest", "latest");

  public static final MaxYearMustBeInFutureHint DECOMMISSIONING_MAX_YEAR_HINT = new MaxYearMustBeInFutureHint(
      DECOMMISSIONING_DATE_LABEL,
      DECOMMISSIONING_YEAR_LABELS
  );

  private final ValidationType validationType;

  public DecommissionedPipelineValidationHint(ValidationType validationType) {
    this.validationType = validationType;
  }

  public Object[] getDecommissioningDateHints() {
    var hints = new ArrayList<>();
    hints.add(DECOMMISSIONING_DATE_LABEL);
    hints.add(DECOMMISSIONING_YEAR_LABELS);
    hints.add(DECOMMISSIONING_MAX_YEAR_HINT);

    addEmptyMinMaxDateAcceptableHint(hints);

    return hints.toArray();
  }

  private void addEmptyMinMaxDateAcceptableHint(List<Object> validationHints) {
    MinMaxDateInputValidator.addEmptyMinMaxDateAcceptableHint(validationType, validationHints);
  }
}
