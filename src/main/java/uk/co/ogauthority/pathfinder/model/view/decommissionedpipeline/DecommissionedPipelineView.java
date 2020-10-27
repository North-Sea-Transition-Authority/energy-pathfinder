package uk.co.ogauthority.pathfinder.model.view.decommissionedpipeline;

import java.util.List;
import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;

public class DecommissionedPipelineView extends ProjectSummaryItem {

  private String pipeline;

  private String materialType;

  private String status;

  private String decommissioningEarliestYear;

  private String decommissioningLatestYear;

  private String removalPremise;

  private List<SummaryLink> summaryLinks;

  public String getPipeline() {
    return pipeline;
  }

  public void setPipeline(String pipeline) {
    this.pipeline = pipeline;
  }

  public String getMaterialType() {
    return materialType;
  }

  public void setMaterialType(String materialType) {
    this.materialType = materialType;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDecommissioningEarliestYear() {
    return decommissioningEarliestYear;
  }

  public void setDecommissioningEarliestYear(String decommissioningEarliestYear) {
    this.decommissioningEarliestYear = decommissioningEarliestYear;
  }

  public String getDecommissioningLatestYear() {
    return decommissioningLatestYear;
  }

  public void setDecommissioningLatestYear(String decommissioningLatestYear) {
    this.decommissioningLatestYear = decommissioningLatestYear;
  }

  public String getRemovalPremise() {
    return removalPremise;
  }

  public void setRemovalPremise(String removalPremise) {
    this.removalPremise = removalPremise;
  }

  public List<SummaryLink> getSummaryLinks() {
    return summaryLinks;
  }

  public void setSummaryLinks(List<SummaryLink> summaryLinks) {
    this.summaryLinks = summaryLinks;
  }
}
