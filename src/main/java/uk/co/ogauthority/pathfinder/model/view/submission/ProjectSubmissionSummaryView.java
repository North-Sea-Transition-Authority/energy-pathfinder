package uk.co.ogauthority.pathfinder.model.view.submission;

public class ProjectSubmissionSummaryView extends SubmissionSummaryView {

  public ProjectSubmissionSummaryView(String projectDisplayName,
                                      String formattedSubmittedTimestamp,
                                      String submittedBy) {
    super(
        projectDisplayName,
        formattedSubmittedTimestamp,
        submittedBy
    );
  }
}
