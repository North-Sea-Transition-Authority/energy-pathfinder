package uk.co.ogauthority.pathfinder.service.project.submission;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.submission.ProjectNoUpdateSubmissionSummaryView;
import uk.co.ogauthority.pathfinder.model.view.submission.ProjectSubmissionSummaryView;

/**
 * Interface to indicate an implementor provides summary views for submission confirmation screens.
 */
public interface ProjectSubmissionSummaryService {

  /**
   * Indicates the supported project type of the implementor.
   * @return the supported project type of the implementor
   */
  ProjectType getSupportedProjectType();

  /**
   * Gets the project submission summary for a submitted project detail.
   * @param projectDetail The project detail that has just been submitted
   * @return the submission summary view associated with the project detail
   */
  ProjectSubmissionSummaryView getSubmissionSummaryView(ProjectDetail projectDetail);

  /**
   * Gets the project no update submission summary for a submitted project detail.
   * @param projectDetail The project detail that has just had a no update submitted
   * @return the no update submission summary view associated with the project detail
   */
  ProjectNoUpdateSubmissionSummaryView getNoUpdateSubmissionSummaryView(ProjectDetail projectDetail);

}
