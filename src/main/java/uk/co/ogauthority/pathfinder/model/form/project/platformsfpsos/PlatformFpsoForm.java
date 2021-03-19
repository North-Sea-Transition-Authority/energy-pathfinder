package uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos;

import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.FuturePlans;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.PlatformFpsoInfrastructureType;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.SubstructureRemovalPremise;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.positivewholenumber.PositiveWholeNumber;

public class PlatformFpsoForm {

  @NotNull(message = "Select the type of infrastructure", groups = FullValidation.class)
  private PlatformFpsoInfrastructureType infrastructureType;

  private String platformStructure;

  private String fpsoStructure;

  private String fpsoType;

  private String fpsoDimensions;

  @NotNull(message = "Enter a topside/FPSO mass", groups = FullValidation.class)
  @PositiveWholeNumber(messagePrefix = "Topside/FPSO mass", groups = {FullValidation.class, PartialValidation.class})
  private Integer topsideFpsoMass;

  private MinMaxDateInput topsideRemovalYears;

  @NotNull(message = "Select if substructure removal expected to be within scope", groups = FullValidation.class)
  private Boolean substructureExpectedToBeRemoved;

  private SubstructureRemovalPremise substructureRemovalPremise;

  @PositiveWholeNumber(messagePrefix = "Estimated substructure removal mass", groups = {FullValidation.class, PartialValidation.class})
  private Integer substructureRemovalMass;

  private MinMaxDateInput substructureRemovalYears;

  @NotNull(message = "Select the future plans", groups = FullValidation.class)
  private FuturePlans futurePlans;

  public PlatformFpsoInfrastructureType getInfrastructureType() {
    return infrastructureType;
  }

  public void setInfrastructureType(
      PlatformFpsoInfrastructureType infrastructureType) {
    this.infrastructureType = infrastructureType;
  }

  public String getPlatformStructure() {
    return platformStructure;
  }

  public void setPlatformStructure(String platformStructure) {
    this.platformStructure = platformStructure;
  }

  public String getFpsoStructure() {
    return fpsoStructure;
  }

  public void setFpsoStructure(String fpsoStructure) {
    this.fpsoStructure = fpsoStructure;
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

  public Integer getTopsideFpsoMass() {
    return topsideFpsoMass;
  }

  public void setTopsideFpsoMass(Integer topsideFpsoMass) {
    this.topsideFpsoMass = topsideFpsoMass;
  }

  public MinMaxDateInput getTopsideRemovalYears() {
    return topsideRemovalYears;
  }

  public void setTopsideRemovalYears(
      MinMaxDateInput topsideRemovalYears) {
    this.topsideRemovalYears = topsideRemovalYears;
  }

  public Boolean getSubstructureExpectedToBeRemoved() {
    return substructureExpectedToBeRemoved;
  }

  public void setSubstructureExpectedToBeRemoved(Boolean substructureExpectedToBeRemoved) {
    this.substructureExpectedToBeRemoved = substructureExpectedToBeRemoved;
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

  public MinMaxDateInput getSubstructureRemovalYears() {
    return substructureRemovalYears;
  }

  public void setSubstructureRemovalYears(
      MinMaxDateInput substructureRemovalYears) {
    this.substructureRemovalYears = substructureRemovalYears;
  }

  public FuturePlans getFuturePlans() {
    return futurePlans;
  }

  public void setFuturePlans(FuturePlans futurePlans) {
    this.futurePlans = futurePlans;
  }
}
