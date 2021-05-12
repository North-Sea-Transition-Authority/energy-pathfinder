package uk.co.ogauthority.pathfinder.model.form.projectassessment;

import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class ProjectAssessmentValidationHint {

  private final boolean canRequestUpdate;

  private final ProjectType projectType;

  public ProjectAssessmentValidationHint(boolean canRequestUpdate, ProjectType projectType) {
    this.canRequestUpdate = canRequestUpdate;
    this.projectType = projectType;
  }

  public boolean isCanRequestUpdate() {
    return canRequestUpdate;
  }

  public ProjectType getProjectType() {
    return projectType;
  }
}
