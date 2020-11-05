package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSubmissionSummaryView;

public class ProjectSubmissionSummaryTestUtil {

  private static final String PROJECT_TITLE = "projectTitle";
  private static final String FORMATTED_TIMESTAMP = "30/10/2020 10:18:04";
  private static final String SUBMITTED_BY_USER = "user";

  public static ProjectSubmissionSummaryView getProjectSubmissionSummaryView() {
    return new ProjectSubmissionSummaryView(PROJECT_TITLE, FORMATTED_TIMESTAMP, SUBMITTED_BY_USER);
  }
}
