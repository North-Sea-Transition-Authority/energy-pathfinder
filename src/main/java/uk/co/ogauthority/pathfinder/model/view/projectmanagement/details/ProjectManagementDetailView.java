package uk.co.ogauthority.pathfinder.model.view.projectmanagement.details;

import java.util.Objects;

public class ProjectManagementDetailView {

  private String fieldStage;

  private String field;

  private String status;

  private boolean isEnergyTransitionProject;

  private String submissionDate;

  private String submittedByUser;

  private String submittedByUserEmail;

  public String getFieldStage() {
    return fieldStage;
  }

  public void setFieldStage(String fieldStage) {
    this.fieldStage = fieldStage;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public boolean getIsEnergyTransitionProject() {
    return isEnergyTransitionProject;
  }

  public void setIsEnergyTransitionProject(boolean isEnergyTransitionProject) {
    this.isEnergyTransitionProject = isEnergyTransitionProject;
  }

  public String getSubmissionDate() {
    return submissionDate;
  }

  public void setSubmissionDate(String submissionDate) {
    this.submissionDate = submissionDate;
  }

  public String getSubmittedByUser() {
    return submittedByUser;
  }

  public void setSubmittedByUser(String submittedByUser) {
    this.submittedByUser = submittedByUser;
  }

  public String getSubmittedByUserEmail() {
    return submittedByUserEmail;
  }

  public void setSubmittedByUserEmail(String submittedByUserEmail) {
    this.submittedByUserEmail = submittedByUserEmail;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectManagementDetailView that = (ProjectManagementDetailView) o;
    return Objects.equals(fieldStage, that.fieldStage)
        && Objects.equals(field, that.field)
        && Objects.equals(status, that.status)
        && Objects.equals(isEnergyTransitionProject, that.isEnergyTransitionProject)
        && Objects.equals(submissionDate, that.submissionDate)
        && Objects.equals(submittedByUser, that.submittedByUser)
        && Objects.equals(submittedByUserEmail, that.submittedByUserEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        fieldStage,
        field,
        status,
        isEnergyTransitionProject,
        submissionDate,
        submittedByUser,
        submittedByUserEmail
    );
  }
}
