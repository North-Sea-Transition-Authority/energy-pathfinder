package uk.co.ogauthority.pathfinder.service.email.projectupdate.noupdatenotification;

import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.noupdatenotification.NoUpdateNotificationEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

/**
 * Interface allowing consumers to provide custom no update notification email properties
 * for a specific project type.
 */
public interface NoUpdateNotificationEmailPropertyProvider {

  /**
   * The project type supported by this implementation.
   * @return The project type supported by this implementation
   */
  ProjectType getSupportedProjectType();

  /**
   * The no update notification email properties specific to this implementation.
   * @param projectDetail The project detail being processed
   * @param noUpdateReason the reason for the no update
   * @return the no update notification email properties specific to this project type
   */
  NoUpdateNotificationEmailProperties getNoUpdateNotificationEmailProperties(ProjectDetail projectDetail,
                                                                             String noUpdateReason);
}
