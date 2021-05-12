package uk.co.ogauthority.pathfinder.service.projectmanagement.heading;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

/**
 * Interface for services to provide implementations for custom project management
 * headers for projects of a certain type.
 */
public interface ProjectManagementHeadingService {

  /**
   * Get the project type that the implementing service supports.
   * @return the project type that the implementing service supports
   */
  ProjectType getSupportedProjectType();

  /**
   * Get the heading text to display for the project.
   * @param projectDetail the project detail we are looking at
   * @return the heading text to display for the project
   */
  String getHeadingText(ProjectDetail projectDetail);

  /**
   * Get the caption text to display for the project.
   * @param projectDetail the project detail we are looking at
   * @return the caption text to display for the project
   */
  String getCaptionText(ProjectDetail projectDetail);
}
