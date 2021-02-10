package uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule;

import java.util.ArrayList;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MaxYearMustBeInFutureHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MinMaxYearLabelsHint;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;
import uk.co.ogauthority.pathfinder.model.form.validation.quarteryear.QuarterYearInputValidator;

public final class PlugAbandonmentScheduleValidationHint {

  public static final FormInputLabel PLUG_ABANDONMENT_DATE_LABEL = new FormInputLabel("plug and abandonment date");
  public static final MinMaxYearLabelsHint PLUG_ABANDONMENT_YEAR_LABELS = new MinMaxYearLabelsHint("earliest", "latest");
  public static final MaxYearMustBeInFutureHint PLUG_ABANDONMENT_MAX_YEAR_HINT = new MaxYearMustBeInFutureHint(
      PLUG_ABANDONMENT_DATE_LABEL,
      PLUG_ABANDONMENT_YEAR_LABELS
  );

  private final ValidationType validationType;

  public PlugAbandonmentScheduleValidationHint(ValidationType validationType) {
    this.validationType = validationType;
  }

  public Object[] getPlugAbandonmentDateHints() {
    var hints = new ArrayList<>();
    hints.add(PLUG_ABANDONMENT_DATE_LABEL);
    hints.add(PLUG_ABANDONMENT_YEAR_LABELS);
    hints.add(PLUG_ABANDONMENT_MAX_YEAR_HINT);

    MinMaxDateInputValidator.addEmptyMinMaxDateAcceptableHint(validationType, hints);

    return hints.toArray();
  }

}
