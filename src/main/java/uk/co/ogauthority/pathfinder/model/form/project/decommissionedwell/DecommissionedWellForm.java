package uk.co.ogauthority.pathfinder.model.form.project.decommissionedwell;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.InputEntryType;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.positivewholenumber.PositiveWholeNumberGreaterThanZero;

public class DecommissionedWellForm {

  @NotEmpty(message = "Select a type", groups = FullValidation.class)
  private String type;

  @NotNull(message = "Enter the number of wells to be decommissioned", groups = FullValidation.class)
  @PositiveWholeNumberGreaterThanZero(messagePrefix = "Number to be decommissioned", groups = {
      FullValidation.class,
      PartialValidation.class
  })
  private Integer numberToBeDecommissioned;

  private QuarterYearInput plugAbandonmentDate;

  @NotNull(message = "Select if the P&A date is estimated or actual", groups = FullValidation.class)
  private InputEntryType plugAbandonmentDateType;

  @NotEmpty(message = "Enter the operational status", groups = FullValidation.class)
  private String operationalStatus;

  @NotEmpty(message = "Select the mechanical status", groups = FullValidation.class)
  private String mechanicalStatus;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getNumberToBeDecommissioned() {
    return numberToBeDecommissioned;
  }

  public void setNumberToBeDecommissioned(Integer numberToBeDecommissioned) {
    this.numberToBeDecommissioned = numberToBeDecommissioned;
  }

  public QuarterYearInput getPlugAbandonmentDate() {
    return plugAbandonmentDate;
  }

  public void setPlugAbandonmentDate(
      QuarterYearInput plugAbandonmentDate) {
    this.plugAbandonmentDate = plugAbandonmentDate;
  }

  public InputEntryType getPlugAbandonmentDateType() {
    return plugAbandonmentDateType;
  }

  public void setPlugAbandonmentDateType(InputEntryType plugAbandonmentDateType) {
    this.plugAbandonmentDateType = plugAbandonmentDateType;
  }

  public String getOperationalStatus() {
    return operationalStatus;
  }

  public void setOperationalStatus(String operationalStatus) {
    this.operationalStatus = operationalStatus;
  }

  public String getMechanicalStatus() {
    return mechanicalStatus;
  }

  public void setMechanicalStatus(String mechanicalStatus) {
    this.mechanicalStatus = mechanicalStatus;
  }
}
