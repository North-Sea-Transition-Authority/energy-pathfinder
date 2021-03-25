package uk.co.ogauthority.pathfinder.model.form.projectassessment;

public class ProjectAssessmentValidationHint {

  private final boolean canRequestUpdate;

  public ProjectAssessmentValidationHint(boolean canRequestUpdate) {
    this.canRequestUpdate = canRequestUpdate;
  }

  public boolean isCanRequestUpdate() {
    return canRequestUpdate;
  }
}
