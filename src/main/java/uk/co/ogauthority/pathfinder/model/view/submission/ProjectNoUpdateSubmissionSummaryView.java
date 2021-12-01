package uk.co.ogauthority.pathfinder.model.view.submission;

public class ProjectNoUpdateSubmissionSummaryView extends SubmissionSummaryView {

  public ProjectNoUpdateSubmissionSummaryView(String projectDisplayName,
                                              String formattedSubmittedTimestamp,
                                              String submittedBy) {
    super(
        projectDisplayName,
        formattedSubmittedTimestamp,
        submittedBy
    );
  }

}
