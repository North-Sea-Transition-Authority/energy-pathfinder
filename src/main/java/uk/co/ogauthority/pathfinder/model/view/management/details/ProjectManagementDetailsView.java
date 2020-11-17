package uk.co.ogauthority.pathfinder.model.view.management.details;

import java.util.Objects;

public class ProjectManagementDetailsView {

  private String fieldStage;

  private String field;

  private Integer version;

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

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
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
    ProjectManagementDetailsView that = (ProjectManagementDetailsView) o;
    return Objects.equals(fieldStage, that.fieldStage)
        && Objects.equals(field, that.field)
        && Objects.equals(version, that.version)
        && Objects.equals(submissionDate, that.submissionDate)
        && Objects.equals(submittedByUser, that.submittedByUser)
        && Objects.equals(submittedByUserEmail, that.submittedByUserEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        fieldStage,
        field,
        version,
        submissionDate,
        submittedByUser,
        submittedByUserEmail
    );
  }
}
