package uk.co.ogauthority.pathfinder.service.projectmanagement.details;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.ProjectManagementDetailView;

/**
 * Interface to be used for services which are responsible for getting the management summary details
 * for a project with a specific project type.
 */
public interface ProjectManagementDetailService {

  /**
   * Determines the project type the implementation supports.
   * @return The project type that the implementor supports
   */
  ProjectType getSupportedProjectType();

  /**
   * Get the path to the template to render.
   * @return The path to the template to render
   */
  String getTemplatePath();

  /**
   * Get the ProjectManagementDetailView required for this implementation.
   * @param projectDetail The project detail for the project we are looking at
   * @return The ProjectManagementDetailView relevant for this project type
   */
  ProjectManagementDetailView getManagementDetailView(ProjectDetail projectDetail);
}
