package uk.co.ogauthority.pathfinder.service.email.projectupdate.updatesubmitted;

import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.ProjectUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public interface UpdateSubmittedEmailPropertyProvider {

  /**
   * The project type supported by this implementation.
   * @return The project type supported by this implementation
   */
  ProjectType getSupportedProjectType();

  /**
   * The update submitted properties specific to this implementation.
   * @param projectDetail The project detail being processed
   * @return the update submitted email properties specific to this project type
   */
  ProjectUpdateEmailProperties getUpdateSubmittedEmailProperties(ProjectDetail projectDetail);
}
