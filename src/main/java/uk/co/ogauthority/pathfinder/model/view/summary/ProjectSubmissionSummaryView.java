package uk.co.ogauthority.pathfinder.model.view.summary;

public class ProjectSubmissionSummaryView {

  private String projectDisplayName;

  private String formattedSubmittedTimestamp;

  private String submittedBy;

  public ProjectSubmissionSummaryView(String projectDisplayName,
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
}
