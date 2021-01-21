package uk.co.ogauthority.pathfinder.model.entity.project.decommissionedwell;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.project.InputEntryType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell.DecommissionedWellType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell.WellMechanicalStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell.WellOperationalStatus;

@Entity
@Table(name = "decommissioned_wells")
public class DecommissionedWell extends ProjectDetailEntity {

  @Enumerated(EnumType.STRING)
  private DecommissionedWellType type;

  private String manualType;

  private Integer numberToBeDecommissioned;

  @Enumerated(EnumType.STRING)
  private Quarter plugAbandonmentDateQuarter;

  private Integer plugAbandonmentDateYear;

  @Enumerated(EnumType.STRING)
  private InputEntryType plugAbandonmentDateType;

  @Enumerated(EnumType.STRING)
  private WellOperationalStatus operationalStatus;

  private String manualOperationalStatus;

  @Enumerated(EnumType.STRING)
  private WellMechanicalStatus mechanicalStatus;

  private String manualMechanicalStatus;

  public DecommissionedWellType getType() {
    return type;
  }

  public void setType(DecommissionedWellType type) {
    this.type = type;
  }

  public String getManualType() {
    return manualType;
  }

  public void setManualType(String manualType) {
    this.manualType = manualType;
  }

  public Integer getNumberToBeDecommissioned() {
    return numberToBeDecommissioned;
  }

  public void setNumberToBeDecommissioned(Integer numberToBeDecommissioned) {
    this.numberToBeDecommissioned = numberToBeDecommissioned;
  }

  public Quarter getPlugAbandonmentDateQuarter() {
    return plugAbandonmentDateQuarter;
  }

  public void setPlugAbandonmentDateQuarter(Quarter plugAbandonmentDateQuarter) {
    this.plugAbandonmentDateQuarter = plugAbandonmentDateQuarter;
  }

  public Integer getPlugAbandonmentDateYear() {
    return plugAbandonmentDateYear;
  }

  public void setPlugAbandonmentDateYear(Integer plugAbandonmentDateYear) {
    this.plugAbandonmentDateYear = plugAbandonmentDateYear;
  }

  public InputEntryType getPlugAbandonmentDateType() {
    return plugAbandonmentDateType;
  }

  public void setPlugAbandonmentDateType(InputEntryType inputEntryType) {
    this.plugAbandonmentDateType = inputEntryType;
  }

  public WellOperationalStatus getOperationalStatus() {
    return operationalStatus;
  }

  public void setOperationalStatus(
      WellOperationalStatus operationalStatus) {
    this.operationalStatus = operationalStatus;
  }

  public String getManualOperationalStatus() {
    return manualOperationalStatus;
  }

  public void setManualOperationalStatus(String manualOperationalStatus) {
    this.manualOperationalStatus = manualOperationalStatus;
  }

  public WellMechanicalStatus getMechanicalStatus() {
    return mechanicalStatus;
  }

  public void setMechanicalStatus(
      WellMechanicalStatus mechanicalStatus) {
    this.mechanicalStatus = mechanicalStatus;
  }

  public String getManualMechanicalStatus() {
    return manualMechanicalStatus;
  }

  public void setManualMechanicalStatus(String manualMechanicalStatus) {
    this.manualMechanicalStatus = manualMechanicalStatus;
  }
}
