package uk.co.ogauthority.pathfinder.model.entity.project.subseainfrastructure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkFacility;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.InfrastructureStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaInfrastructureType;
import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaStructureMass;

@Entity
@Table(name = "subsea_infrastructure")
public class SubseaInfrastructure extends ProjectDetailEntity {

  @ManyToOne
  @JoinColumn(name = "facility_id")
  private DevUkFacility facility;

  private String manualFacility;

  @Lob
  @Column(name = "description", columnDefinition = "CLOB")
  private String description;

  @Enumerated(EnumType.STRING)
  private InfrastructureStatus status;

  @Enumerated(EnumType.STRING)
  private SubseaInfrastructureType infrastructureType;

  private Integer numberOfMattresses;

  private Integer totalEstimatedMattressMass;

  @Enumerated(EnumType.STRING)
  private SubseaStructureMass totalEstimatedSubseaMass;

  private String otherInfrastructureType;

  private Integer totalEstimatedOtherMass;

  @Column(name = "earliest_decom_start_year")
  private Integer earliestDecommissioningStartYear;

  @Column(name = "latest_decom_completion_year")
  private Integer latestDecommissioningCompletionYear;

  public DevUkFacility getFacility() {
    return facility;
  }

  public void setFacility(DevUkFacility facility) {
    this.facility = facility;
  }

  public String getManualFacility() {
    return manualFacility;
  }

  public void setManualFacility(String manualFacility) {
    this.manualFacility = manualFacility;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public InfrastructureStatus getStatus() {
    return status;
  }

  public void setStatus(
      InfrastructureStatus status) {
    this.status = status;
  }

  public SubseaInfrastructureType getInfrastructureType() {
    return infrastructureType;
  }

  public void setInfrastructureType(
      SubseaInfrastructureType infrastructureType) {
    this.infrastructureType = infrastructureType;
  }

  public Integer getNumberOfMattresses() {
    return numberOfMattresses;
  }

  public void setNumberOfMattresses(Integer numberOfMattresses) {
    this.numberOfMattresses = numberOfMattresses;
  }

  public Integer getTotalEstimatedMattressMass() {
    return totalEstimatedMattressMass;
  }

  public void setTotalEstimatedMattressMass(Integer totalEstimatedMattressMass) {
    this.totalEstimatedMattressMass = totalEstimatedMattressMass;
  }

  public SubseaStructureMass getTotalEstimatedSubseaMass() {
    return totalEstimatedSubseaMass;
  }

  public void setTotalEstimatedSubseaMass(SubseaStructureMass totalEstimatedSubseaMass) {
    this.totalEstimatedSubseaMass = totalEstimatedSubseaMass;
  }

  public String getOtherInfrastructureType() {
    return otherInfrastructureType;
  }

  public void setOtherInfrastructureType(String otherInfrastructureType) {
    this.otherInfrastructureType = otherInfrastructureType;
  }

  public Integer getTotalEstimatedOtherMass() {
    return totalEstimatedOtherMass;
  }

  public void setTotalEstimatedOtherMass(Integer totalEstimatedOtherMass) {
    this.totalEstimatedOtherMass = totalEstimatedOtherMass;
  }

  public Integer getEarliestDecommissioningStartYear() {
    return earliestDecommissioningStartYear;
  }

  public void setEarliestDecommissioningStartYear(Integer earliestDecommissioningStartYear) {
    this.earliestDecommissioningStartYear = earliestDecommissioningStartYear;
  }

  public Integer getLatestDecommissioningCompletionYear() {
    return latestDecommissioningCompletionYear;
  }

  public void setLatestDecommissioningCompletionYear(Integer latestDecommissioningCompletionYear) {
    this.latestDecommissioningCompletionYear = latestDecommissioningCompletionYear;
  }
}
