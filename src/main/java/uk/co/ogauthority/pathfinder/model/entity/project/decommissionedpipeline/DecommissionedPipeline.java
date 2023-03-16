package uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
