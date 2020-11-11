package uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.FuturePlans;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.SubstructureRemovalPremise;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.positivewholenumber.PositiveWholeNumber;

public class PlatformFpsoForm {

  @NotEmpty(message = "Select a platform or FPSO", groups = FullValidation.class)
  private String structure;

  @NotNull(message = "Enter a topside/FPSO mass", groups = FullValidation.class)
  @PositiveWholeNumber(messagePrefix = "Topside/FPSO mass", groups = {FullValidation.class, PartialValidation.class})
  private Integer topsideFpsoMass;

  private MinMaxDateInput topsideRemovalYears;

  @NotNull(message = "Select if substructures are expected to be removed", groups = FullValidation.class)
  private Boolean substructureExpectedToBeRemoved;

  private SubstructureRemovalPremise substructureRemovalPremise;

  @PositiveWholeNumber(messagePrefix = "Substructure removal mass", groups = {FullValidation.class, PartialValidation.class})
  private Integer substructureRemovalMass;

  private MinMaxDateInput substructureRemovalYears;

  @NotEmpty(message = "Enter an FPSO type", groups = FullValidation.class)
  private String fpsoType;

  @NotEmpty(message = "Enter the FPSO dimensions", groups = FullValidation.class)
  private String fpsoDimensions;

  @NotNull(message = "Select the future plans", groups = FullValidation.class)
  private FuturePlans futurePlans;

  public String getStructure() {
    return structure;
  }

  public void setStructure(String structure) {
    this.structure = structure;
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
