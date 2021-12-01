package uk.co.ogauthority.pathfinder.service.project.cancellation;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

/**
 * Interface for implementers to determine when a project version should be
 * able to be cancelled.
 */
public interface CancelProjectVersionService {

  /**
   * Get the project type supported by this implementation.
   * @return the supported project type of this implementation
   */
  ProjectType getSupportedProjectType();

  /**
   * Determines if the provided projectDetail can be cancelled or not.
   * @param projectDetail The project detail attempting to be cancelled
   * @return true if the projectDetail can be cancelled, false otherwise
   */
  boolean isCancellable(ProjectDetail projectDetail);

}
