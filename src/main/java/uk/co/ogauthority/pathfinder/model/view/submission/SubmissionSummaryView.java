package uk.co.ogauthority.pathfinder.model.view.submission;

import java.util.Objects;

public abstract class SubmissionSummaryView {

  private String projectDisplayName;

  private String formattedSubmittedTimestamp;

  private String submittedBy;

  public SubmissionSummaryView(String projectDisplayName,
                        String formattedSubmittedTimestamp,
                        String submittedBy) {
    this.projectDisplayName = projectDisplayName;
    this.formattedSubmittedTimestamp = formattedSubmittedTimestamp;
    this.submittedBy = submittedBy;
  }

  public String getProjectDisplayName() {
    return projectDisplayName;
  }

  public void setProjectDisplayName(String projectDisplayName) {
    this.projectDisplayName = projectDisplayName;
  }

  public String getFormattedSubmittedTimestamp() {
    return formattedSubmittedTimestamp;
  }

  public void setFormattedSubmittedTimestamp(String formattedSubmittedTimestamp) {
    this.formattedSubmittedTimestamp = formattedSubmittedTimestamp;
  }

  public String getSubmittedBy() {
    return submittedBy;
  }

  public void setSubmittedBy(String submittedBy) {
    this.submittedBy = submittedBy;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SubmissionSummaryView that = (SubmissionSummaryView) o;
    return Objects.equals(projectDisplayName, that.projectDisplayName)
        && Objects.equals(formattedSubmittedTimestamp, that.formattedSubmittedTimestamp)
        && Objects.equals(submittedBy, that.submittedBy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        projectDisplayName,
        formattedSubmittedTimestamp,
        submittedBy
    );
  }
}
