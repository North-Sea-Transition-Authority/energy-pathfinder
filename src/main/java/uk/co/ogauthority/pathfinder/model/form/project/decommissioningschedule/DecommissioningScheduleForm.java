package uk.co.ogauthority.pathfinder.model.form.project.decommissioningschedule;

import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.CessationOfProductionDateType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.DecommissioningStartDateType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class DecommissioningScheduleForm {

  @NotNull(message = "Select if you know the decommissioning start date", groups = FullValidation.class)
  private DecommissioningStartDateType decommissioningStartDateType;

  private ThreeFieldDateInput exactDecommissioningStartDate;

  private QuarterYearInput estimatedDecommissioningStartDate;

  private String decommissioningStartDateNotProvidedReason;

  @NotNull(message = "Select if you know the Cessation of Production date", groups = FullValidation.class)
  private CessationOfProductionDateType cessationOfProductionDateType;

  private ThreeFieldDateInput exactCessationOfProductionDate;

  private QuarterYearInput estimatedCessationOfProductionDate;

  private String cessationOfProductionDateNotProvidedReason;

  public DecommissioningStartDateType getDecommissioningStartDateType() {
    return decommissioningStartDateType;
  }

  public void setDecommissioningStartDateType(
      DecommissioningStartDateType decommissioningStartDateType) {
    this.decommissioningStartDateType = decommissioningStartDateType;
  }

  public ThreeFieldDateInput getExactDecommissioningStartDate() {
    return exactDecommissioningStartDate;
  }

  public void setExactDecommissioningStartDate(
      ThreeFieldDateInput exactDecommissioningStartDate) {
    this.exactDecommissioningStartDate = exactDecommissioningStartDate;
  }

  public QuarterYearInput getEstimatedDecommissioningStartDate() {
    return estimatedDecommissioningStartDate;
  }

  public void setEstimatedDecommissioningStartDate(
      QuarterYearInput estimatedDecommissioningStartDate) {
    this.estimatedDecommissioningStartDate = estimatedDecommissioningStartDate;
  }

  public String getDecommissioningStartDateNotProvidedReason() {
    return decommissioningStartDateNotProvidedReason;
  }

  public void setDecommissioningStartDateNotProvidedReason(String decommissioningStartDateNotProvidedReason) {
    this.decommissioningStartDateNotProvidedReason = decommissioningStartDateNotProvidedReason;
  }

  public CessationOfProductionDateType getCessationOfProductionDateType() {
    return cessationOfProductionDateType;
  }

  public void setCessationOfProductionDateType(
      CessationOfProductionDateType cessationOfProductionDateType) {
    this.cessationOfProductionDateType = cessationOfProductionDateType;
  }

  public ThreeFieldDateInput getExactCessationOfProductionDate() {
    return exactCessationOfProductionDate;
  }

  public void setExactCessationOfProductionDate(
      ThreeFieldDateInput exactCessationOfProductionDate) {
    this.exactCessationOfProductionDate = exactCessationOfProductionDate;
  }

  public QuarterYearInput getEstimatedCessationOfProductionDate() {
    return estimatedCessationOfProductionDate;
  }

  public void setEstimatedCessationOfProductionDate(
      QuarterYearInput estimatedCessationOfProductionDate) {
    this.estimatedCessationOfProductionDate = estimatedCessationOfProductionDate;
  }

  public String getCessationOfProductionDateNotProvidedReason() {
    return cessationOfProductionDateNotProvidedReason;
  }

  public void setCessationOfProductionDateNotProvidedReason(String cessationOfProductionDateNotProvidedReason) {
    this.cessationOfProductionDateNotProvidedReason = cessationOfProductionDateNotProvidedReason;
  }
}
