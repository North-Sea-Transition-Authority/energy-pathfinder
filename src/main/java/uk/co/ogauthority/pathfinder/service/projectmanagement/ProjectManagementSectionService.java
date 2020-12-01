package uk.co.ogauthority.pathfinder.service.projectmanagement;

import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;

public interface ProjectManagementSectionService {

  /**
   * Returns true if getSection should take the project detail version
   * the user has selected, or false to default to the latest project detail version.
   */
  default boolean useSelectedVersionProjectDetail() {
    return false;
  }

  ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user);
}
