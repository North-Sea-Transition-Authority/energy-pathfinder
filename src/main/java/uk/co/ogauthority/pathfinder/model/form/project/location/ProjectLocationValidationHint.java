package uk.co.ogauthority.pathfinder.model.form.project.location;

import java.time.LocalDate;
import java.util.ArrayList;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.DateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.OnOrBeforeDateHint;

public final class ProjectLocationValidationHint {

  public static final FormInputLabel APPROVED_FDP_LABEL = new FormInputLabel("approved Field Development Plan date");
  public static final FormInputLabel APPROVED_DECOM_LABEL = new FormInputLabel("approved Decommissioning Program date");

  private final OnOrBeforeDateHint fdpApprovalDateHint;
  private final OnOrBeforeDateHint decomProgramApprovalDateHint;
  private final EmptyDateAcceptableHint emptyDateAcceptableHint;
  private final ValidationType validationType;

  public ProjectLocationValidationHint(ValidationType validationType) {

    this.fdpApprovalDateHint = new OnOrBeforeDateHint(
        APPROVED_FDP_LABEL,
        LocalDate.now(),
        DateHint.TODAY_DATE_LABEL
    );
    this.decomProgramApprovalDateHint = new OnOrBeforeDateHint(
        APPROVED_DECOM_LABEL,
        LocalDate.now(),
        DateHint.TODAY_DATE_LABEL
    );

    this.emptyDateAcceptableHint = new EmptyDateAcceptableHint();

    this.validationType = validationType;
  }

  public Object[] getFdpApprovalDateValidationHints() {
    var hints = new ArrayList<>();

    hints.add(APPROVED_FDP_LABEL);
    hints.add(fdpApprovalDateHint);

    addEmptyDateAcceptableHint(hints);

    return hints.toArray();
  }

  public Object[] getDecomProgramApprovalDateValidationHints() {
    var hints = new ArrayList<>();

    hints.add(APPROVED_DECOM_LABEL);
    hints.add(decomProgramApprovalDateHint);

    addEmptyDateAcceptableHint(hints);

    return hints.toArray();
  }

  private void addEmptyDateAcceptableHint(ArrayList<Object> validationHints) {
    if (validationType.equals(ValidationType.PARTIAL)) {
      validationHints.add(emptyDateAcceptableHint);
    }
  }
}
