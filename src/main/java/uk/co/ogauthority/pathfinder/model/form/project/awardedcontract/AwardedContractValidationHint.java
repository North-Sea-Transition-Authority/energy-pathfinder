package uk.co.ogauthority.pathfinder.model.form.project.awardedcontract;

import java.time.LocalDate;
import java.util.ArrayList;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.DateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.OnOrBeforeDateHint;

public final class AwardedContractValidationHint {

  public static final FormInputLabel DATE_AWARDED_LABEL = new FormInputLabel("date awarded");
  private final OnOrBeforeDateHint dateAwardedHint;

  public AwardedContractValidationHint() {
    this.dateAwardedHint = new OnOrBeforeDateHint(DATE_AWARDED_LABEL, LocalDate.now(), DateHint.TODAY_DATE_LABEL);
  }

  public Object[] getDateAwardedValidationHints() {

    var hints = new ArrayList<>();

    hints.add(DATE_AWARDED_LABEL);
    hints.add(dateAwardedHint);
    // question is optional so always add EmptyDateAcceptableHint
    hints.add(new EmptyDateAcceptableHint());

    return hints.toArray();
  }
}
