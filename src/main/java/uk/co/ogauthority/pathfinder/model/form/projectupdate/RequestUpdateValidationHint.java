package uk.co.ogauthority.pathfinder.model.form.projectupdate;

import java.time.LocalDate;
import java.util.ArrayList;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.AfterDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.DateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.EmptyDateAcceptableHint;

public class RequestUpdateValidationHint {

  public static final FormInputLabel DEADLINE_LABEL = new FormInputLabel("deadline");
  public static final String DATE_ERROR_LABEL = DateHint.TODAY_DATE_LABEL;

  private final AfterDateHint deadlineDateHint;

  public RequestUpdateValidationHint() {
    this.deadlineDateHint = new AfterDateHint(
        DEADLINE_LABEL,
        LocalDate.now(),
        DATE_ERROR_LABEL
    );
  }

  public Object[] getDeadlineDateHint() {
    var hints = new ArrayList<>();
    hints.add(deadlineDateHint);
    hints.add(DEADLINE_LABEL);
    hints.add(new EmptyDateAcceptableHint());
    return hints.toArray();
  }
}
