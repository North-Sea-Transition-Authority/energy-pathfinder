package uk.co.ogauthority.pathfinder.service.project.submission;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.submission.ProjectNoUpdateSubmissionSummaryView;
import uk.co.ogauthority.pathfinder.model.view.submission.ProjectSubmissionSummaryView;

public class TestProjectSubmissionSummaryService implements ProjectSubmissionSummaryService {

  @Override
  public ProjectType getSupportedProjectType() {
    return null;
  }

  @Override
  public ProjectSubmissionSummaryView getSubmissionSummaryView(ProjectDetail projectDetail) {
    return null;
  }

  @Override
  public ProjectNoUpdateSubmissionSummaryView getNoUpdateSubmissionSummaryView(ProjectDetail projectDetail) {
    return null;
  }
}
