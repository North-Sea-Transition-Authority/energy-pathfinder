package uk.co.ogauthority.pathfinder.model.view.projectupdate;

public class ProjectNoUpdateSummaryView {

  private String projectTitle;

  private String formattedSubmittedTimestamp;

  private String submittedBy;

  public ProjectNoUpdateSummaryView() {
  }

  public ProjectNoUpdateSummaryView(String projectTitle,
                                    String formattedSubmittedTimestamp,
                                    String submittedBy) {
    this.projectTitle = projectTitle;
    this.formattedSubmittedTimestamp = formattedSubmittedTimestamp;
    this.submittedBy = submittedBy;
  }

  public String getProjectTitle() {
    return projectTitle;
  }

  public void setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle;
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
