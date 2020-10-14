package uk.co.ogauthority.pathfinder.model.view.platformfpso;

import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;

public class PlatformFpsoView extends ProjectSummaryItem {

  private String platformFpso;

  private String topsideFpsoMass;

  private String topsideRemovalYears;

  private Boolean substructuresExpectedToBeRemoved;

  private String substructureRemovalPremise;

  private String substructureRemovalMass;

  private String substructureRemovalYears;

  private String fpsoType;

  private String fpsoDimensions;

  private String futurePlans;

  private SummaryLink editLink;

  private SummaryLink deleteLink;


  public PlatformFpsoView(Integer id,
                          Integer displayOrder,
                          Integer projectId

  ) {
    this.id = id;
    this.displayOrder = displayOrder;
    this.projectId = projectId;
  }

  public String getPlatformFpso() {
    return platformFpso;
  }

  public void setPlatformFpso(String platformFpso) {
    this.platformFpso = platformFpso;
  }

  public String getTopsideFpsoMass() {
    return topsideFpsoMass;
  }

  public void setTopsideFpsoMass(String topsideFpsoMass) {
    this.topsideFpsoMass = topsideFpsoMass;
  }

  public String getTopsideRemovalYears() {
    return topsideRemovalYears;
  }

  public void setTopsideRemovalYears(String topsideRemovalYears) {
    this.topsideRemovalYears = topsideRemovalYears;
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

  public String getSubstructureRemovalYears() {
    return substructureRemovalYears;
  }

  public void setSubstructureRemovalYears(String substructureRemovalYears) {
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

  public String getFuturePlans() {
    return futurePlans;
  }

  public void setFuturePlans(String futurePlans) {
    this.futurePlans = futurePlans;
  }

  public SummaryLink getEditLink() {
    return editLink;
  }

  public void setEditLink(SummaryLink editLink) {
    this.editLink = editLink;
  }

  public SummaryLink getDeleteLink() {
    return deleteLink;
  }

  public void setDeleteLink(SummaryLink deleteLink) {
    this.deleteLink = deleteLink;
  }
}
