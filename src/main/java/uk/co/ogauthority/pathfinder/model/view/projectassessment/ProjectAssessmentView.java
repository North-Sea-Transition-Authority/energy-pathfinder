package uk.co.ogauthority.pathfinder.model.view.projectassessment;

import java.util.Objects;

public class ProjectAssessmentView {

  private String projectQuality;

  private Boolean readyToBePublished;

  private Boolean updateRequired;

  private String assessmentDate;

  private String assessedByUser;

  public String getProjectQuality() {
    return projectQuality;
  }

  public void setProjectQuality(String projectQuality) {
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

  public String getAssessmentDate() {
    return assessmentDate;
  }

  public void setAssessmentDate(String assessmentDate) {
    this.assessmentDate = assessmentDate;
  }

  public String getAssessedByUser() {
    return assessedByUser;
  }

  public void setAssessedByUser(String assessedByUser) {
    this.assessedByUser = assessedByUser;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectAssessmentView that = (ProjectAssessmentView) o;
    return Objects.equals(projectQuality, that.projectQuality)
        && Objects.equals(readyToBePublished, that.readyToBePublished)
        && Objects.equals(updateRequired, that.updateRequired)
        && Objects.equals(assessmentDate, that.assessmentDate)
        && Objects.equals(assessedByUser, that.assessedByUser);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        projectQuality,
        readyToBePublished,
        updateRequired,
        assessmentDate,
        assessedByUser
    );
  }
}
