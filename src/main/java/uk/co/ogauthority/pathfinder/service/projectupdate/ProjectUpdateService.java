package uk.co.ogauthority.pathfinder.service.projectupdate;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

@Service
public class ProjectUpdateService {

  private final ProjectService projectService;
  private final List<ProjectFormSectionService> projectFormSectionServices;

  @Autowired
  public ProjectUpdateService(ProjectService projectService,
                              List<ProjectFormSectionService> projectFormSectionServices) {
    this.projectService = projectService;
    this.projectFormSectionServices = projectFormSectionServices;
  }

  /**
   * Create a new project detail version as a duplicate of fromDetail.
   * @param fromDetail the project detail to base the new version off of
   * @param userAccount the user creating the new version
   */
  @Transactional
  public void createNewProjectVersion(ProjectDetail fromDetail, AuthenticatedUserAccount userAccount) {
    final var newDetail = projectService.createNewProjectDetailVersion(fromDetail, userAccount);
    projectFormSectionServices.forEach(
        projectFormSectionService -> projectFormSectionService.copySectionData(fromDetail, newDetail)
    );
  }
}
