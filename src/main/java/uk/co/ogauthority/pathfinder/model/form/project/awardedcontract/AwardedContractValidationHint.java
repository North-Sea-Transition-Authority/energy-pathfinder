package uk.co.ogauthority.pathfinder.model.form.project.awardedcontract;

import java.time.LocalDate;
import java.util.ArrayList;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.DateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.OnOrBeforeDateHint;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;

public final class AwardedContractValidationHint {

  public static final FormInputLabel DATE_AWARDED_LABEL = new FormInputLabel("date awarded");

  private final OnOrBeforeDateHint dateAwardedHint;
  private final ValidationType validationType;

  public AwardedContractValidationHint(ValidationType validationType) {
    this.dateAwardedHint = new OnOrBeforeDateHint(DATE_AWARDED_LABEL, LocalDate.now(), DateHint.TODAY_DATE_LABEL);
    this.validationType = validationType;
  }

  public Object[] getDateAwardedValidationHints() {

    var hints = new ArrayList<>();

    hints.add(DATE_AWARDED_LABEL);
    hints.add(dateAwardedHint);
    DateInputValidator.addEmptyDateAcceptableHint(validationType, hints);

    return hints.toArray();
  }
}
