package uk.co.ogauthority.pathfinder.model.form.project.projectassessment;

import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.projectassessment.ProjectQuality;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ProjectAssessmentForm {

  @NotNull(message = "Select the project quality", groups = FullValidation.class)
  private ProjectQuality projectQuality;

  @NotNull(message = "Select yes if the project is ready to be published", groups = FullValidation.class)
  private Boolean readyToBePublished;

  private Boolean updateRequired;

  public ProjectQuality getProjectQuality() {
    return projectQuality;
  }

  public void setProjectQuality(
      ProjectQuality projectQuality) {
    this.projectQuality = projectQuality;
  }

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
