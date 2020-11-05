package uk.co.ogauthority.pathfinder.model.view.platformfpso;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;

public class PlatformFpsoView extends ProjectSummaryItem {

  private StringWithTag platformFpso;

  private String topsideFpsoMass;

  private String topsideRemovalEarliestYear;

  private String topsideRemovalLatestYear;

  private Boolean substructuresExpectedToBeRemoved;

  private String substructureRemovalPremise;

  private String substructureRemovalMass;

  private String substructureRemovalEarliestYear;

  private String substructureRemovalLatestYear;

  private String fpsoType;

  private String fpsoDimensions;

  private String futurePlans;

  public PlatformFpsoView(Integer id,
                          Integer displayOrder,
                          Integer projectId

  ) {
    this.id = id;
    this.displayOrder = displayOrder;
    this.projectId = projectId;
  }

  public StringWithTag getPlatformFpso() {
    return platformFpso;
  }

  public void setPlatformFpso(StringWithTag platformFpso) {
    this.platformFpso = platformFpso;
  }

  public String getTopsideFpsoMass() {
    return topsideFpsoMass;
  }

  public void setTopsideFpsoMass(String topsideFpsoMass) {
    this.topsideFpsoMass = topsideFpsoMass;
  }

  public String getTopsideRemovalEarliestYear() {
    return topsideRemovalEarliestYear;
  }

  public void setTopsideRemovalEarliestYear(String topsideRemovalEarliestYear) {
    this.topsideRemovalEarliestYear = topsideRemovalEarliestYear;
  }

  public String getTopsideRemovalLatestYear() {
    return topsideRemovalLatestYear;
  }

  public void setTopsideRemovalLatestYear(String topsideRemovalLatestYear) {
    this.topsideRemovalLatestYear = topsideRemovalLatestYear;
  }

  public Boolean getSubstructuresExpectedToBeRemoved() {
    return substructuresExpectedToBeRemoved;
  }

  public void setSubstructuresExpectedToBeRemoved(Boolean substructuresExpectedToBeRemoved) {
    this.substructuresExpectedToBeRemoved = substructuresExpectedToBeRemoved;
  }

  public String getSubstructureRemovalPremise() {
    return substructureRemovalPremise;
  }

  public void setSubstructureRemovalPremise(String substructureRemovalPremise) {
    this.substructureRemovalPremise = substructureRemovalPremise;
  }

  public String getSubstructureRemovalMass() {
    return substructureRemovalMass;
  }

  public void setSubstructureRemovalMass(String substructureRemovalMass) {
    this.substructureRemovalMass = substructureRemovalMass;
  }

  public String getSubstructureRemovalEarliestYear() {
    return substructureRemovalEarliestYear;
  }

  public void setSubstructureRemovalEarliestYear(String substructureRemovalEarliestYear) {
    this.substructureRemovalEarliestYear = substructureRemovalEarliestYear;
  }

  public String getSubstructureRemovalLatestYear() {
    return substructureRemovalLatestYear;
  }

  public void setSubstructureRemovalLatestYear(String substructureRemovalLatestYear) {
    this.substructureRemovalLatestYear = substructureRemovalLatestYear;
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

  public String getFuturePlans() {
    return futurePlans;
  }

  public void setFuturePlans(String futurePlans) {
    this.futurePlans = futurePlans;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    PlatformFpsoView that = (PlatformFpsoView) o;
    return Objects.equals(platformFpso, that.platformFpso)
        && Objects.equals(topsideFpsoMass, that.topsideFpsoMass)
        && Objects.equals(topsideRemovalEarliestYear, that.topsideRemovalEarliestYear)
        && Objects.equals(topsideRemovalLatestYear, that.topsideRemovalLatestYear)
        && Objects.equals(substructuresExpectedToBeRemoved, that.substructuresExpectedToBeRemoved)
        && Objects.equals(substructureRemovalPremise, that.substructureRemovalPremise)
        && Objects.equals(substructureRemovalMass, that.substructureRemovalMass)
        && Objects.equals(substructureRemovalEarliestYear, that.substructureRemovalEarliestYear)
        && Objects.equals(substructureRemovalLatestYear, that.substructureRemovalLatestYear)
        && Objects.equals(fpsoType, that.fpsoType)
        && Objects.equals(fpsoDimensions, that.fpsoDimensions)
        && Objects.equals(futurePlans, that.futurePlans);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        platformFpso,
        topsideFpsoMass,
        topsideRemovalEarliestYear,
        topsideRemovalLatestYear,
        substructuresExpectedToBeRemoved,
        substructureRemovalPremise,
        substructureRemovalMass,
        substructureRemovalEarliestYear,
        substructureRemovalLatestYear,
        fpsoType,
        fpsoDimensions,
        futurePlans
    );
  }
}
