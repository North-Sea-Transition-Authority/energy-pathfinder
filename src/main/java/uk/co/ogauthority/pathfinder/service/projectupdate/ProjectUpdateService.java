package uk.co.ogauthority.pathfinder.service.projectupdate;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.ProjectUpdate;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.projectupdate.ProjectUpdateType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.projectupdate.ProjectUpdateRepository;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

@Service
public class ProjectUpdateService {

  private final ProjectUpdateRepository projectUpdateRepository;
  private final ProjectDetailsRepository projectDetailsRepository;
  private final ProjectService projectService;
  private final List<ProjectFormSectionService> projectFormSectionServices;

  @Autowired
  public ProjectUpdateService(
      ProjectUpdateRepository projectUpdateRepository,
      ProjectDetailsRepository projectDetailsRepository,
      ProjectService projectService,
      List<ProjectFormSectionService> projectFormSectionServices) {
    this.projectUpdateRepository = projectUpdateRepository;
    this.projectDetailsRepository = projectDetailsRepository;
    this.projectService = projectService;
    this.projectFormSectionServices = projectFormSectionServices;
  }

  @Transactional
  public ProjectUpdate startUpdate(ProjectDetail projectDetail,
                                   AuthenticatedUserAccount user,
                                   ProjectUpdateType updateType) {
    return startUpdate(projectDetail, ProjectStatus.DRAFT, user, updateType);
  }

  @Transactional
  public ProjectUpdate startUpdate(ProjectDetail projectDetail,
                                   ProjectStatus newStatus,
                                   AuthenticatedUserAccount user,
                                   ProjectUpdateType updateType) {
    var newDetail = createNewProjectVersion(projectDetail, newStatus, user);
    var projectUpdate = new ProjectUpdate();
    projectUpdate.setFromDetail(projectDetail);
    projectUpdate.setToDetail(newDetail);
    projectUpdate.setUpdateType(updateType);
    return projectUpdateRepository.save(projectUpdate);
  }

  public void deleteProjectUpdate(ProjectUpdate projectUpdate) {
    projectUpdateRepository.delete(projectUpdate);
  }

  @Transactional
  public ProjectDetail createNewProjectVersion(ProjectDetail fromDetail, AuthenticatedUserAccount userAccount) {
    return createNewProjectVersion(fromDetail, fromDetail.getStatus(), userAccount);
  }

  /**
   * Create a new project detail version as a duplicate of fromDetail.
   * @param fromDetail the project detail to base the new version off of
   * @param userAccount the user creating the new version
   */
  @Transactional
  public ProjectDetail createNewProjectVersion(ProjectDetail fromDetail,
                                               ProjectStatus status,
                                               AuthenticatedUserAccount userAccount) {
    final var newDetail = projectService.createNewProjectDetailVersion(fromDetail, status, userAccount);
    projectFormSectionServices.forEach(
        projectFormSectionService -> projectFormSectionService.copySectionData(fromDetail, newDetail)
    );
    return newDetail;
  }

  public boolean isUpdateInProgress(Project project) {
    return projectDetailsRepository.isProjectUpdateInProgress(project.getId());
  }

  public Optional<ProjectUpdate> getByToDetail(ProjectDetail toDetail) {
    return projectUpdateRepository.findByToDetail(toDetail);
  }
}
