package uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan;

import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ForwardWorkPlanCollaborationSetupForm {

  @NotNull(message = "Select yes if you have any collaboration opportunities to add", groups = FullValidation.class)
  private Boolean hasCollaborationsToAdd;

  public Boolean getHasCollaborationsToAdd() {
    return hasCollaborationsToAdd;
  }

  public void setHasCollaborationsToAdd(Boolean hasCollaborationsToAdd) {
    this.hasCollaborationsToAdd = hasCollaborationsToAdd;
  }

}
