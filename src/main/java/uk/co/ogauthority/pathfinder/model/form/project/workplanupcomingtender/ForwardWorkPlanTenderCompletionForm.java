package uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender;

import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ForwardWorkPlanTenderCompletionForm {

  @NotNull(
      message = "Select if you want to add another upcoming tender",
      groups = FullValidation.class
  )
  private Boolean hasOtherTendersToAdd;

  public Boolean getHasOtherTendersToAdd() {
    return hasOtherTendersToAdd;
  }

  public void setHasOtherTendersToAdd(Boolean hasOtherTendersToAdd) {
    this.hasOtherTendersToAdd = hasOtherTendersToAdd;
  }
}
