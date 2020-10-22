package uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkFacility;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.FuturePlans;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.SubstructureRemovalPremise;

@Entity
@Table(name = "platforms_fpsos")
public class PlatformFpso extends ProjectDetailEntity {

  @ManyToOne
  @JoinColumn(name = "facility_id")
  private DevUkFacility structure;

  @Column(name = "manual_facility_name")
  private String manualStructureName;

  @Column(name = "mass")
  private Integer topsideFpsoMass;

  private String earliestRemovalYear;

  private String latestRemovalYear;

  @Column(name = "substructures_to_be_removed")
  private Boolean substructuresExpectedToBeRemoved;

  @Enumerated(EnumType.STRING)
  private SubstructureRemovalPremise substructureRemovalPremise;

  private Integer substructureRemovalMass;

  @Column(name = "substructure_removal_earliest")
  private String subStructureRemovalEarliestYear;

  @Column(name = "substructure_removal_latest")
  private String subStructureRemovalLatestYear;

  private String fpsoType;

  @Lob
  @Column(name = "fpso_dimensions", columnDefinition = "CLOB")
  private String fpsoDimensions;

  @Enumerated(EnumType.STRING)
  private FuturePlans futurePlans;

  public PlatformFpso() {
  }

  public PlatformFpso(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public PlatformFpso(ProjectDetail projectDetail,
                      DevUkFacility structure) {
    this.projectDetail = projectDetail;
    this.structure = structure;
  }

  public PlatformFpso(ProjectDetail projectDetail, String manualStructureName) {
    this.projectDetail = projectDetail;
    this.manualStructureName = manualStructureName;
  }

  public DevUkFacility getStructure() {
    return structure;
  }

  public void setStructure(DevUkFacility structure) {
    this.structure = structure;
  }

  public String getManualStructureName() {
    return manualStructureName;
  }

  public void setManualStructureName(String manualStructureName) {
    this.manualStructureName = manualStructureName;
  }

  public Integer getTopsideFpsoMass() {
    return topsideFpsoMass;
  }

  public void setTopsideFpsoMass(Integer mass) {
    this.topsideFpsoMass = mass;
  }

  public String getEarliestRemovalYear() {
    return earliestRemovalYear;
  }

  public void setEarliestRemovalYear(String earliestRemovalYear) {
    this.earliestRemovalYear = earliestRemovalYear;
  }

  public String getLatestRemovalYear() {
    return latestRemovalYear;
  }

  public void setLatestRemovalYear(String latestRemovalYear) {
    this.latestRemovalYear = latestRemovalYear;
  }

  public Boolean getSubstructuresExpectedToBeRemoved() {
    return substructuresExpectedToBeRemoved;
  }

  public void setSubstructuresExpectedToBeRemoved(Boolean substructuresExpectedToBeRemoved) {
    this.substructuresExpectedToBeRemoved = substructuresExpectedToBeRemoved;
  }

  public SubstructureRemovalPremise getSubstructureRemovalPremise() {
    return substructureRemovalPremise;
  }

  public void setSubstructureRemovalPremise(
      SubstructureRemovalPremise substructureRemovalPremise) {
    this.substructureRemovalPremise = substructureRemovalPremise;
  }

  public Integer getSubstructureRemovalMass() {
    return substructureRemovalMass;
  }

  public void setSubstructureRemovalMass(Integer substructureRemovalMass) {
    this.substructureRemovalMass = substructureRemovalMass;
  }

  public String getSubStructureRemovalEarliestYear() {
    return subStructureRemovalEarliestYear;
  }

  public void setSubStructureRemovalEarliestYear(String subStructureRemovalEarliestYear) {
    this.subStructureRemovalEarliestYear = subStructureRemovalEarliestYear;
  }

  public String getSubStructureRemovalLatestYear() {
    return subStructureRemovalLatestYear;
  }

  public void setSubStructureRemovalLatestYear(String subStructureRemovalLatestYear) {
    this.subStructureRemovalLatestYear = subStructureRemovalLatestYear;
  }

  public String getFpsoType() {
    return fpsoType;
  }

  public void setFpsoType(String fpsoType) {
    this.fpsoType = fpsoType;
  }

  public String getFpsoDimensions() {
    return fpsoDimensions;
  }

  public void setFpsoDimensions(String fpsoDimensions) {
    this.fpsoDimensions = fpsoDimensions;
  }

  public FuturePlans getFuturePlans() {
    return futurePlans;
  }

  public void setFuturePlans(FuturePlans futurePlans) {
    this.futurePlans = futurePlans;
  }
}
