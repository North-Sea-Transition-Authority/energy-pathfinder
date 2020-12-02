package uk.co.ogauthority.pathfinder.service.projectupdate;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectupdate.OperatorUpdateController;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.ProjectUpdate;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.projectupdate.ProjectUpdateType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.projectupdate.ProjectUpdateRepository;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

@Service
public class ProjectUpdateService {

  public static final String START_PAGE_TEMPLATE_PATH = "projectupdate/startPage";

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
  public ProjectUpdate startUpdate(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    var newDetail = createNewProjectVersion(projectDetail, user);
    var projectUpdate = new ProjectUpdate();
    projectUpdate.setFromDetail(projectDetail);
    projectUpdate.setNewDetail(newDetail);
    projectUpdate.setUpdateType(ProjectUpdateType.OPERATOR_INITIATED);
    projectUpdateRepository.save(projectUpdate);
    return projectUpdate;
  }

  /**
   * Create a new project detail version as a duplicate of fromDetail.
   * @param fromDetail the project detail to base the new version off of
   * @param userAccount the user creating the new version
   */
  @Transactional
  public ProjectDetail createNewProjectVersion(ProjectDetail fromDetail, AuthenticatedUserAccount userAccount) {
    final var newDetail = projectService.createNewProjectDetailVersion(fromDetail, userAccount);
    projectFormSectionServices.forEach(
        projectFormSectionService -> projectFormSectionService.copySectionData(fromDetail, newDetail)
    );
    return newDetail;
  }

  public boolean isUpdateInProgress(Project project) {
    return projectDetailsRepository.projectUpdateInProgress(project.getId());
  }

  public ModelAndView getProjectUpdateModelAndView(Integer projectId) {
    return new ModelAndView(START_PAGE_TEMPLATE_PATH)
        .addObject("startActionUrl", ReverseRouter.route(on(OperatorUpdateController.class).startUpdate(projectId, null, null)));
  }
}
