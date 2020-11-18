package uk.co.ogauthority.pathfinder.service.project.management;

import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.management.ProjectManagementSection;

public interface ProjectManagementSectionService {

  ProjectManagementSection getSection(ProjectDetail projectDetail, AuthenticatedUserAccount user);
}
