package uk.co.ogauthority.pathfinder.model.form.project.location;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.DateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.OnOrBeforeDateHint;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;

public final class ProjectLocationValidationHint {

  public static final FormInputLabel APPROVED_FDP_LABEL = new FormInputLabel("approved Field Development Plan date");
  public static final FormInputLabel APPROVED_DECOM_LABEL = new FormInputLabel("approved Decommissioning Program date");

  private final ProjectDetail projectDetail;
  private final OnOrBeforeDateHint fdpApprovalDateHint;
  private final OnOrBeforeDateHint decomProgramApprovalDateHint;
  private final ValidationType validationType;

  public ProjectLocationValidationHint(ProjectDetail projectDetail, ValidationType validationType) {
    this.projectDetail = projectDetail;

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

    this.validationType = validationType;
  }

  public ProjectDetail getProjectDetail() {
    return projectDetail;
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

  private void addEmptyDateAcceptableHint(List<Object> validationHints) {
    DateInputValidator.addEmptyDateAcceptableHint(validationType, validationHints);
  }

  public ValidationType getValidationType() {
    return validationType;
  }
}
