package uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.InfrastructureStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedpipeline.PipelineRemovalPremise;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedString;

public class DecommissionedPipelineForm {

  @NotEmpty(message = "Select a pipeline", groups = FullValidation.class)
  private String pipeline;

  @NotEmpty(message = "Enter the material type of the pipeline", groups = FullValidation.class)
  @LengthRestrictedString(messagePrefix = "The material type", groups = {FullValidation.class, PartialValidation.class})
  private String materialType;

  @NotNull(message = "Select the status of the pipeline", groups = FullValidation.class)
  private InfrastructureStatus status;

  private MinMaxDateInput decommissioningYears;

  @NotNull(message = "Select the pipeline removal premise", groups = FullValidation.class)
  private PipelineRemovalPremise removalPremise;

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

  public InfrastructureStatus getStatus() {
    return status;
  }

  public void setStatus(InfrastructureStatus status) {
    this.status = status;
  }

  public MinMaxDateInput getDecommissioningYears() {
    return decommissioningYears;
  }

  public void setDecommissioningYears(
      MinMaxDateInput decommissioningYears) {
    this.decommissioningYears = decommissioningYears;
  }

  public PipelineRemovalPremise getRemovalPremise() {
    return removalPremise;
  }

  public void setRemovalPremise(
      PipelineRemovalPremise removalPremise) {
    this.removalPremise = removalPremise;
  }
}
