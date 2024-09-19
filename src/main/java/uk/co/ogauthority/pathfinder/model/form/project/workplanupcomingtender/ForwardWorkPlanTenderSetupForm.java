package uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender;

import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ForwardWorkPlanTenderSetupForm {

  @NotNull(message = "Select yes if you have any upcoming tenders to add", groups = FullValidation.class)
  private Boolean hasTendersToAdd;

  public Boolean getHasTendersToAdd() {
    return hasTendersToAdd;
  }

  public void setHasTendersToAdd(Boolean hasTendersToAdd) {
    this.hasTendersToAdd = hasTendersToAdd;
  }
}