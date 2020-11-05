package uk.co.ogauthority.pathfinder.model.view.subseainfrastructure;

import java.util.List;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;

public class SubseaInfrastructureView extends ProjectSummaryItem {

  private StringWithTag structure;

  private String description;

  private String status;

  private String infrastructureType;

  private Integer numberOfMattresses;

  private String totalEstimatedMattressMass;

  private String totalEstimatedSubseaMass;

  private String otherInfrastructureType;

  private String totalEstimatedOtherMass;

  private String earliestDecommissioningStartYear;

  private String latestDecommissioningCompletionYear;

  private List<SummaryLink> summaryLinks;

  private boolean isConcreteMattress;

  private boolean isSubseaStructure;

  private boolean isOtherInfrastructure;

  public StringWithTag getStructure() {
    return structure;
  }

  public void setStructure(StringWithTag structure) {
    this.structure = structure;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getInfrastructureType() {
    return infrastructureType;
  }

  public void setInfrastructureType(String infrastructureType) {
    this.infrastructureType = infrastructureType;
  }

  public Integer getNumberOfMattresses() {
    return numberOfMattresses;
  }

  public void setNumberOfMattresses(Integer numberOfMattresses) {
    this.numberOfMattresses = numberOfMattresses;
  }

  public String getTotalEstimatedMattressMass() {
    return totalEstimatedMattressMass;
  }

  public void setTotalEstimatedMattressMass(String totalEstimatedMattressMass) {
    this.totalEstimatedMattressMass = totalEstimatedMattressMass;
  }

  public String getTotalEstimatedSubseaMass() {
    return totalEstimatedSubseaMass;
  }

  public void setTotalEstimatedSubseaMass(String totalEstimatedSubseaMass) {
    this.totalEstimatedSubseaMass = totalEstimatedSubseaMass;
  }

  public String getOtherInfrastructureType() {
    return otherInfrastructureType;
  }

  public void setOtherInfrastructureType(String otherInfrastructureType) {
    this.otherInfrastructureType = otherInfrastructureType;
  }

  public String getTotalEstimatedOtherMass() {
    return totalEstimatedOtherMass;
  }

  public void setTotalEstimatedOtherMass(String totalEstimatedOtherMass) {
    this.totalEstimatedOtherMass = totalEstimatedOtherMass;
  }

  public String getEarliestDecommissioningStartYear() {
    return earliestDecommissioningStartYear;
  }

  public void setEarliestDecommissioningStartYear(String earliestDecommissioningStartYear) {
    this.earliestDecommissioningStartYear = earliestDecommissioningStartYear;
  }

  public String getLatestDecommissioningCompletionYear() {
    return latestDecommissioningCompletionYear;
  }

  public void setLatestDecommissioningCompletionYear(String latestDecommissioningCompletionYear) {
    this.latestDecommissioningCompletionYear = latestDecommissioningCompletionYear;
  }

  public List<SummaryLink> getSummaryLinks() {
    return summaryLinks;
  }

  public void setSummaryLinks(List<SummaryLink> summaryLinks) {
    this.summaryLinks = summaryLinks;
  }

  public boolean getConcreteMattress() {
    return isConcreteMattress;
  }

  public void setConcreteMattress(boolean concreteMattress) {
    isConcreteMattress = concreteMattress;
  }

  public boolean getSubseaStructure() {
    return isSubseaStructure;
  }

  public void setSubseaStructure(boolean subseaStructure) {
    isSubseaStructure = subseaStructure;
  }

  public boolean getOtherInfrastructure() {
    return isOtherInfrastructure;
  }

  public void setOtherInfrastructure(boolean otherInfrastructure) {
    isOtherInfrastructure = otherInfrastructure;
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
    SubseaInfrastructureView that = (SubseaInfrastructureView) o;
    return isConcreteMattress == that.isConcreteMattress
        && isSubseaStructure == that.isSubseaStructure
        && isOtherInfrastructure == that.isOtherInfrastructure
        && Objects.equals(structure, that.structure)
        && Objects.equals(description, that.description)
        && Objects.equals(status, that.status)
        && Objects.equals(infrastructureType, that.infrastructureType)
        && Objects.equals(numberOfMattresses, that.numberOfMattresses)
        && Objects.equals(totalEstimatedMattressMass, that.totalEstimatedMattressMass)
        && Objects.equals(totalEstimatedSubseaMass, that.totalEstimatedSubseaMass)
        && Objects.equals(otherInfrastructureType, that.otherInfrastructureType)
        && Objects.equals(totalEstimatedOtherMass, that.totalEstimatedOtherMass)
        && Objects.equals(earliestDecommissioningStartYear, that.earliestDecommissioningStartYear)
        && Objects.equals(latestDecommissioningCompletionYear, that.latestDecommissioningCompletionYear)
        && Objects.equals(summaryLinks, that.summaryLinks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        structure,
        description,
        status,
        infrastructureType,
        numberOfMattresses,
        totalEstimatedMattressMass,
        totalEstimatedSubseaMass,
        otherInfrastructureType,
        totalEstimatedOtherMass,
        earliestDecommissioningStartYear,
        latestDecommissioningCompletionYear,
        summaryLinks,
        isConcreteMattress,
        isSubseaStructure,
        isOtherInfrastructure
    );
  }
}
