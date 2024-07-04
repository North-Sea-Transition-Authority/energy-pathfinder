package uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.InfrastructureStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedpipeline.PipelineRemovalPremise;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class DecommissionedPipelineForm {

  @NotEmpty(message = "Select a pipeline", groups = FullValidation.class)
  private String pipeline;

  @NotNull(message = "Select the status of the pipeline", groups = FullValidation.class)
  private InfrastructureStatus status;

  private MinMaxDateInput decommissioningDate;

  @NotNull(message = "Select the pipeline decommissioning premise", groups = FullValidation.class)
  private PipelineRemovalPremise removalPremise;

  public String getPipeline() {
    return pipeline;
  }

  public void setPipeline(String pipeline) {
    this.pipeline = pipeline;
  }

  public InfrastructureStatus getStatus() {
    return status;
  }

  public void setStatus(InfrastructureStatus status) {
    this.status = status;
  }

  public MinMaxDateInput getDecommissioningDate() {
    return decommissioningDate;
  }

  public void setDecommissioningDate(
      MinMaxDateInput decommissioningDate) {
    this.decommissioningDate = decommissioningDate;
  }

  public PipelineRemovalPremise getRemovalPremise() {
    return removalPremise;
  }

  public void setRemovalPremise(
      PipelineRemovalPremise removalPremise) {
    this.removalPremise = removalPremise;
  }
}
