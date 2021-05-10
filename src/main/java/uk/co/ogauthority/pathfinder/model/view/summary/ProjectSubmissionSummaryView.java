package uk.co.ogauthority.pathfinder.model.view.summary;

public class ProjectSubmissionSummaryView {

  private String projectTitleOrOperator;

  private String formattedSubmittedTimestamp;

  private String submittedBy;

  public ProjectSubmissionSummaryView(String projectTitleOrOperator,
                                      String formattedSubmittedTimestamp,
                                      String submittedBy) {
    this.projectTitleOrOperator = projectTitleOrOperator;
    this.formattedSubmittedTimestamp = formattedSubmittedTimestamp;
    this.submittedBy = submittedBy;
  }

  public String getProjectTitleOrOperator() {
    return projectTitleOrOperator;
  }

  public void setProjectTitleOrOperator(String projectTitleOrOperator) {
    this.projectTitleOrOperator = projectTitleOrOperator;
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
