package uk.co.ogauthority.pathfinder.model.form.projectassessment;

import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ProjectAssessmentForm {

  @NotNull(message = "Select yes to publish", groups = FullValidation.class)
  private Boolean readyToBePublished;

  private Boolean updateRequired;

  public Boolean getReadyToBePublished() {
    return readyToBePublished;
  }

  public void setReadyToBePublished(Boolean readyToBePublished) {
    this.readyToBePublished = readyToBePublished;
  }

  public Boolean getUpdateRequired() {
    return updateRequired;
  }

  public void setUpdateRequired(Boolean updateRequired) {
    this.updateRequired = updateRequired;
  }
}
