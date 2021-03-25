package uk.co.ogauthority.pathfinder.model.form.project.projectinformation;

import java.util.ArrayList;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint.EmptyQuarterYearAcceptableHint;

public final class ProjectInformationValidationHint {

  public static final FormInputLabel FIRST_PRODUCTION_DATE_LABEL = new FormInputLabel("first production date");
  public static final FormInputLabel DECOM_WORK_START_DATE_LABEL = new FormInputLabel("decommissioning work start date");
  public static final FormInputLabel PRODUCTION_CESSATION_DATE_LABEL = new FormInputLabel("production cessation date");

  private final ValidationType validationType;
  private final EmptyQuarterYearAcceptableHint emptyQuarterYearAcceptableHint;
  private final EmptyDateAcceptableHint emptyDateAcceptableHint;

  public ProjectInformationValidationHint(ValidationType validationType) {
    this.validationType = validationType;
    this.emptyQuarterYearAcceptableHint = new EmptyQuarterYearAcceptableHint();
    this.emptyDateAcceptableHint = new EmptyDateAcceptableHint();
  }

  public Object[] getFirstProductionDateValidationHints() {
    var hints = new ArrayList<>();
    hints.add(FIRST_PRODUCTION_DATE_LABEL);
    addEmptyQuarterYearAcceptableHint(hints, validationType);
    return hints.toArray();
  }

  public Object[] getDecomWorkStartDateValidationHints() {
    var hints = new ArrayList<>();
    hints.add(DECOM_WORK_START_DATE_LABEL);
    addEmptyQuarterYearAcceptableHint(hints, validationType);
    return hints.toArray();
  }

  public Object[] getProductionCessationDateValidationHints() {
    var hints = new ArrayList<>();
    hints.add(PRODUCTION_CESSATION_DATE_LABEL);
    addEmptyDateAcceptableHint(hints, validationType);
    return hints.toArray();
  }

  private void addEmptyQuarterYearAcceptableHint(List<Object> validationHints, ValidationType validationType) {
    if (isPartialValidation(validationType)) {
      validationHints.add(emptyQuarterYearAcceptableHint);
    }
  }

  private void addEmptyDateAcceptableHint(List<Object> validationHints, ValidationType validationType) {
    if (isPartialValidation(validationType)) {
      validationHints.add(emptyDateAcceptableHint);
    }
  }

  private boolean isPartialValidation(ValidationType validationType) {
    return validationType.equals(ValidationType.PARTIAL);
  }

  public ValidationType getValidationType() {
    return validationType;
  }
}
