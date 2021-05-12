package uk.co.ogauthority.pathfinder.model.view.projectmanagement.details;

import java.util.Objects;

public abstract class ProjectManagementDetailView {

  private String status;

  private String submissionDate;

  private String submittedByUser;

  private String submittedByUserEmail;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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
    return Objects.equals(status, that.status)
        && Objects.equals(submissionDate, that.submissionDate)
        && Objects.equals(submittedByUser, that.submittedByUser)
        && Objects.equals(submittedByUserEmail, that.submittedByUserEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        status,
        submissionDate,
        submittedByUser,
        submittedByUserEmail
    );
  }
}
