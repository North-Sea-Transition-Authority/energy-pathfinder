package uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.pipeline.Pipeline;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.InfrastructureStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedpipeline.PipelineRemovalPremise;

@Entity
@Table(name = "decommissioned_pipelines")
public class DecommissionedPipeline extends ProjectDetailEntity {

  @ManyToOne
  @JoinColumn(name = "pipeline_id")
  private Pipeline pipeline;

  @Enumerated(EnumType.STRING)
  private InfrastructureStatus status;

  private String earliestRemovalYear;

  private String latestRemovalYear;

  @Enumerated(EnumType.STRING)
  private PipelineRemovalPremise removalPremise;

  public Pipeline getPipeline() {
    return pipeline;
  }

  public void setPipeline(Pipeline pipeline) {
    this.pipeline = pipeline;
  }

  public InfrastructureStatus getStatus() {
    return status;
  }

  public void setStatus(InfrastructureStatus status) {
    this.status = status;
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

  public PipelineRemovalPremise getRemovalPremise() {
    return removalPremise;
  }

  public void setRemovalPremise(
      PipelineRemovalPremise removalPremise) {
    this.removalPremise = removalPremise;
  }
}
