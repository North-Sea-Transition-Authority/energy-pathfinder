package uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos;

import java.util.ArrayList;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MaxYearMustBeInFutureHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MinMaxYearLabelsHint;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;

public class PlatformFpsoValidationHint {

  public static final FormInputLabel TOPSIDES_REMOVAL_LABEL = new FormInputLabel("Topside/FPSO removal");
  public static final FormInputLabel SUBSTRUCTURE_REMOVAL_LABEL = new FormInputLabel("substructure removal");

  public static final MinMaxYearLabelsHint TOPSIDES_YEAR_LABELS = new MinMaxYearLabelsHint("earliest", "latest");
  public static final MinMaxYearLabelsHint SUBSTRUCTURE_YEAR_LABELS = new MinMaxYearLabelsHint("earliest", "latest");

  public static final MaxYearMustBeInFutureHint TOPSIDES_MAX_YEAR_HINT = new MaxYearMustBeInFutureHint(
      TOPSIDES_REMOVAL_LABEL,
      TOPSIDES_YEAR_LABELS
  );
  public static final MaxYearMustBeInFutureHint SUBSTRUCTURE_MAX_YEAR_HINT = new MaxYearMustBeInFutureHint(
      SUBSTRUCTURE_REMOVAL_LABEL,
      SUBSTRUCTURE_YEAR_LABELS
  );

  private final ValidationType validationType;

  public PlatformFpsoValidationHint(ValidationType validationType) {
    this.validationType = validationType;
  }

  public Object[] getTopsidesRemovalHints() {
    var hints = new ArrayList<>();
    hints.add(TOPSIDES_REMOVAL_LABEL);
    hints.add(TOPSIDES_YEAR_LABELS);
    hints.add(TOPSIDES_MAX_YEAR_HINT);

    addEmptyMinMaxDateAcceptableHint(hints);

    return hints.toArray();
  }

  public Object[] getSubstructureRemovalHints() {
    var hints = new ArrayList<>();
    hints.add(SUBSTRUCTURE_REMOVAL_LABEL);
    hints.add(SUBSTRUCTURE_YEAR_LABELS);
    hints.add(SUBSTRUCTURE_MAX_YEAR_HINT);

    addEmptyMinMaxDateAcceptableHint(hints);

    return hints.toArray();
  }

  public ValidationType getValidationType() {
    return validationType;
  }

  private void addEmptyMinMaxDateAcceptableHint(List<Object> validationHints) {
    MinMaxDateInputValidator.addEmptyMinMaxDateAcceptableHint(validationType, validationHints);
  }

}
