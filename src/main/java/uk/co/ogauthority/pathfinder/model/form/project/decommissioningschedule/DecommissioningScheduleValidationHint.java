package uk.co.ogauthority.pathfinder.model.form.project.decommissioningschedule;

import java.util.ArrayList;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint.EmptyQuarterYearAcceptableHint;

public class DecommissioningScheduleValidationHint {

  public static final FormInputLabel DECOMMISSIONING_START_DATE_LABEL = new FormInputLabel("decommissioning start date");
  public static final FormInputLabel CESSATION_OF_PRODUCTION_DATE_LABEL = new FormInputLabel("COP date");

  private final ValidationType validationType;
  private final EmptyDateAcceptableHint emptyDateAcceptableHint = new EmptyDateAcceptableHint();
  private final EmptyQuarterYearAcceptableHint emptyQuarterYearAcceptableHint = new EmptyQuarterYearAcceptableHint();

  public DecommissioningScheduleValidationHint(ValidationType validationType) {
    this.validationType = validationType;
  }

  public Object[] getExactDecommissioningStartDateValidationHints() {
    var hints = new ArrayList<>();
    hints.add(DECOMMISSIONING_START_DATE_LABEL);
    addEmptyDateAcceptableHint(hints, validationType);
    return hints.toArray();
  }

  public Object[] getEstimatedDecommissioningStartDateValidationHints() {
    var hints = new ArrayList<>();
    hints.add(DECOMMISSIONING_START_DATE_LABEL);
    addEmptyQuarterYearAcceptableHint(hints, validationType);
    return hints.toArray();
  }

  public Object[] getExactCessationOfProductionDateValidationHints() {
    var hints = new ArrayList<>();
    hints.add(CESSATION_OF_PRODUCTION_DATE_LABEL);
    addEmptyDateAcceptableHint(hints, validationType);
    return hints.toArray();
  }

  public Object[] getEstimatedCessationOfProductionDateValidationHints() {
    var hints = new ArrayList<>();
    hints.add(CESSATION_OF_PRODUCTION_DATE_LABEL);
    addEmptyQuarterYearAcceptableHint(hints, validationType);
    return hints.toArray();
  }

  private void addEmptyDateAcceptableHint(List<Object> validationHints, ValidationType validationType) {
    if (isPartialValidation(validationType)) {
      validationHints.add(emptyDateAcceptableHint);
    }
  }

  private void addEmptyQuarterYearAcceptableHint(List<Object> validationHints, ValidationType validationType) {
    if (isPartialValidation(validationType)) {
      validationHints.add(emptyQuarterYearAcceptableHint);
    }
  }

  private boolean isPartialValidation(ValidationType validationType) {
    return validationType.equals(ValidationType.PARTIAL);
  }

  public ValidationType getValidationType() {
    return validationType;
  }
}
