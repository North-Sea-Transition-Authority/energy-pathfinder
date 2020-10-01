package uk.co.ogauthority.pathfinder.model.form.project.decommissionedwell;

import java.util.ArrayList;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.validation.quarteryear.QuarterYearInputValidator;

public final class DecommissionedWellValidationHint {

  public static final FormInputLabel PLUG_ABANDONMENT_DATE_LABEL = new FormInputLabel("plug and abandonment date");

  private final ValidationType validationType;

  public DecommissionedWellValidationHint(ValidationType validationType) {
    this.validationType = validationType;
  }

  public Object[] getPlugAbandonmentDateHints() {
    var hints = new ArrayList<>();

    hints.add(PLUG_ABANDONMENT_DATE_LABEL);
    QuarterYearInputValidator.addEmptyQuarterYearAcceptableHint(validationType, hints);

    return hints.toArray();
  }

}
