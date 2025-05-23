package uk.co.ogauthority.pathfinder.model.form.project.integratedrig;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.integratedrig.IntegratedRigIntentionToReactivate;
import uk.co.ogauthority.pathfinder.model.enums.project.integratedrig.IntegratedRigStatus;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class IntegratedRigForm {

  @NotEmpty(message = "Select a structure", groups = FullValidation.class)
  private String structure;

  private String name;

  @NotNull(message = "Select the status of the integrated rig", groups = FullValidation.class)
  private IntegratedRigStatus status;

  @NotNull(message = "Select yes if you intend to reactivate the integrated rig", groups = FullValidation.class)
  private IntegratedRigIntentionToReactivate intentionToReactivate;

  public String getStructure() {
    return structure;
  }

  public void setStructure(String structure) {
    this.structure = structure;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public IntegratedRigStatus getStatus() {
    return status;
  }

  public void setStatus(IntegratedRigStatus status) {
    this.status = status;
  }

  public IntegratedRigIntentionToReactivate getIntentionToReactivate() {
    return intentionToReactivate;
  }

  public void setIntentionToReactivate(
      IntegratedRigIntentionToReactivate intentionToReactivate) {
    this.intentionToReactivate = intentionToReactivate;
  }
}
