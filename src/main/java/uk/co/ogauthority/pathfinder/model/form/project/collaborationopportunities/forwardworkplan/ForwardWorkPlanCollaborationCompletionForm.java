package uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan;

import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ForwardWorkPlanCollaborationCompletionForm {

  @NotNull(
      message = "Select if you want to add another collaboration opportunity",
      groups = FullValidation.class
  )
  private Boolean hasOtherCollaborationsToAdd;

  public Boolean getHasOtherCollaborationsToAdd() {
    return hasOtherCollaborationsToAdd;
  }

  public void setHasOtherCollaborationsToAdd(Boolean hasOtherCollaborationsToAdd) {
    this.hasOtherCollaborationsToAdd = hasOtherCollaborationsToAdd;
  }
}
