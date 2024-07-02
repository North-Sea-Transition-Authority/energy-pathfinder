package uk.co.ogauthority.pathfinder.model.entity.project.decommissioningschedule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDate;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.CessationOfProductionDateType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.DecommissioningStartDateType;

@Entity
@Table(name = "decommissioning_schedules")
public class DecommissioningSchedule extends ProjectDetailEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "start_date_type")
  private DecommissioningStartDateType decommissioningStartDateType;

  @Column(name = "exact_start_date")
  private LocalDate exactDecommissioningStartDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "estimated_start_date_quarter")
  private Quarter estimatedDecommissioningStartDateQuarter;

  @Column(name = "estimated_start_date_year")
  private Integer estimatedDecommissioningStartDateYear;

  @Lob
  @Column(name = "start_date_not_provided_reason", columnDefinition = "CLOB")
  private String decommissioningStartDateNotProvidedReason;

  @Enumerated(EnumType.STRING)
  @Column(name = "cop_date_type")
  private CessationOfProductionDateType cessationOfProductionDateType;

  @Column(name = "exact_cop_date")
  private LocalDate exactCessationOfProductionDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "estimated_cop_date_quarter")
  private Quarter estimatedCessationOfProductionDateQuarter;

  @Column(name = "estimated_cop_date_year")
  private Integer estimatedCessationOfProductionDateYear;

  @Lob
  @Column(name = "cop_date_not_provided_reason", columnDefinition = "CLOB")
  private String cessationOfProductionDateNotProvidedReason;

  public DecommissioningStartDateType getDecommissioningStartDateType() {
    return decommissioningStartDateType;
  }

  public void setDecommissioningStartDateType(
      DecommissioningStartDateType decommissioningStartDateType) {
    this.decommissioningStartDateType = decommissioningStartDateType;
  }

  public LocalDate getExactDecommissioningStartDate() {
    return exactDecommissioningStartDate;
  }

  public void setExactDecommissioningStartDate(LocalDate exactDecommissioningStartDate) {
    this.exactDecommissioningStartDate = exactDecommissioningStartDate;
  }

  public Quarter getEstimatedDecommissioningStartDateQuarter() {
    return estimatedDecommissioningStartDateQuarter;
  }

  public void setEstimatedDecommissioningStartDateQuarter(
      Quarter estimatedDecommissioningStartDateQuarter) {
    this.estimatedDecommissioningStartDateQuarter = estimatedDecommissioningStartDateQuarter;
  }

  public Integer getEstimatedDecommissioningStartDateYear() {
    return estimatedDecommissioningStartDateYear;
  }

  public void setEstimatedDecommissioningStartDateYear(Integer estimatedDecommissioningStartDateYear) {
    this.estimatedDecommissioningStartDateYear = estimatedDecommissioningStartDateYear;
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

  public LocalDate getExactCessationOfProductionDate() {
    return exactCessationOfProductionDate;
  }

  public void setExactCessationOfProductionDate(LocalDate exactCessationOfProductionDate) {
    this.exactCessationOfProductionDate = exactCessationOfProductionDate;
  }

  public Quarter getEstimatedCessationOfProductionDateQuarter() {
    return estimatedCessationOfProductionDateQuarter;
  }

  public void setEstimatedCessationOfProductionDateQuarter(
      Quarter estimatedCessationOfProductionDateQuarter) {
    this.estimatedCessationOfProductionDateQuarter = estimatedCessationOfProductionDateQuarter;
  }

  public Integer getEstimatedCessationOfProductionDateYear() {
    return estimatedCessationOfProductionDateYear;
  }

  public void setEstimatedCessationOfProductionDateYear(Integer estimatedCessationOfProductionDateYear) {
    this.estimatedCessationOfProductionDateYear = estimatedCessationOfProductionDateYear;
  }

  public String getCessationOfProductionDateNotProvidedReason() {
    return cessationOfProductionDateNotProvidedReason;
  }

  public void setCessationOfProductionDateNotProvidedReason(String cessationOfProductionDateNotProvidedReason) {
    this.cessationOfProductionDateNotProvidedReason = cessationOfProductionDateNotProvidedReason;
  }
}
