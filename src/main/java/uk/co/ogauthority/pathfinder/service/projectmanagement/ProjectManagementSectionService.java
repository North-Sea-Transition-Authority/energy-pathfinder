package uk.co.ogauthority.pathfinder.service.projectmanagement;

import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;

public interface ProjectManagementSectionService {

  default boolean useSelectedVersionProjectDetail() {
    return false;
  }

  ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user);
}
