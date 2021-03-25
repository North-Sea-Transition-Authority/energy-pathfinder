package uk.co.ogauthority.pathfinder.model.view.decommissionedpipeline;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.view.ProjectSummaryItem;

public class DecommissionedPipelineView extends ProjectSummaryItem {

  private String pipeline;

  private String materialType;

  private String status;

  private String decommissioningEarliestYear;

  private String decommissioningLatestYear;

  private String removalPremise;

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
    DecommissionedPipelineView that = (DecommissionedPipelineView) o;
    return Objects.equals(pipeline, that.pipeline)
        && Objects.equals(materialType, that.materialType)
        && Objects.equals(status, that.status)
        && Objects.equals(decommissioningEarliestYear, that.decommissioningEarliestYear)
        && Objects.equals(decommissioningLatestYear, that.decommissioningLatestYear)
        && Objects.equals(removalPremise, that.removalPremise);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        pipeline,
        materialType,
        status,
        decommissioningEarliestYear,
        decommissioningLatestYear,
        removalPremise,
        summaryLinks
    );
  }
}
