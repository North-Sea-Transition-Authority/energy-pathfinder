package uk.co.ogauthority.pathfinder.service.email.projectupdate.updaterequested;

import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.ProjectUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

/**
 * Interface allowing consumers to provide custom update requested email properties
 * for a specific project type.
 */
public interface UpdateRequestedEmailPropertyProvider {

  /**
   * The project type supported by this implementation.
   * @return The project type supported by this implementation
   */
  ProjectType getSupportedProjectType();

  /**
   * The update requested properties specific to this implementation.
   * @param projectDetail The project detail being processed
   * @return the update requested email properties specific to this project type
   */
  ProjectUpdateRequestedEmailProperties getUpdateRequestedEmailProperties(ProjectDetail projectDetail,
                                                                          String updateReason,
                                                                          String deadlineDate);
}
