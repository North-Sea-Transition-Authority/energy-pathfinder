package uk.co.ogauthority.pathfinder.service.project.cancellation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.exception.CancelDraftProjectException;
import uk.co.ogauthority.pathfinder.exception.CancelProjectVersionImplementationException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryRenderingService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateService;

@Service
public class CancelDraftProjectVersionService {

  public static final String CANCEL_DRAFT_TEMPLATE_PATH = "project/cancelDraft";

  private final ProjectService projectService;
  private final ProjectUpdateService projectUpdateService;
  private final ProjectSummaryRenderingService projectSummaryRenderingService;
  private final List<ProjectFormSectionService> projectFormSectionServices;
  private final List<CancelProjectVersionService> cancelProjectVersionServices;

  @Autowired
  public CancelDraftProjectVersionService(ProjectService projectService,
                                          ProjectUpdateService projectUpdateService,
                                          ProjectSummaryRenderingService projectSummaryRenderingService,
                                          List<ProjectFormSectionService> projectFormSectionServices,
                                          List<CancelProjectVersionService> cancelProjectVersionServices) {
    this.projectService = projectService;
    this.projectUpdateService = projectUpdateService;
    this.projectSummaryRenderingService = projectSummaryRenderingService;
    this.projectFormSectionServices = projectFormSectionServices;
    this.cancelProjectVersionServices = cancelProjectVersionServices;
  }

  public boolean isCancellable(ProjectDetail projectDetail) {
    return getCancelProjectVersionServiceForProjectType(projectDetail).isCancellable(projectDetail);
  }

  @Transactional
  public void cancelDraft(ProjectDetail projectDetail) {
    if (isCancellable(projectDetail)) {
      cancelProjectVersion(projectDetail);
    } else {
      throw new CancelDraftProjectException(String.format(
          "Project detail with ID %d and type %s does not support version %d being cancelled",
          projectDetail.getId(),
          projectDetail.getProjectType().name(),
          projectDetail.getVersion()
      ));
    }
  }

  private void cancelProjectVersion(ProjectDetail projectDetail) {

    projectFormSectionServices.stream()
        .filter(projectFormSectionService -> projectFormSectionService.getSupportedProjectTypes().contains(projectDetail.getProjectType()))
        .forEach(projectFormSectionService -> projectFormSectionService.removeSectionData(projectDetail));

    if (!projectDetail.isFirstVersion()) {
      projectUpdateService.getByToDetail(projectDetail).ifPresent(projectUpdate -> {
        projectUpdateService.deleteProjectUpdate(projectUpdate);
        projectService.updateProjectDetailIsCurrentVersion(projectUpdate.getFromDetail(), true);
      });
    }

    projectService.deleteProjectDetail(projectDetail);

    if (projectDetail.isFirstVersion()) {
      projectService.deleteProject(projectDetail.getProject());
    }
  }

  @Transactional
  public void cancelDraftIfExists(Integer projectId) {
    var latestProjectDetail = projectService.getLatestDetailOrError(projectId);
    if (latestProjectDetail.getStatus().equals(ProjectStatus.DRAFT)) {
      cancelDraft(latestProjectDetail);
    }
  }

  public ModelAndView getCancelDraftModelAndView(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    return new ModelAndView(CANCEL_DRAFT_TEMPLATE_PATH)
        .addObject("isUpdate", !projectDetail.isFirstVersion())
        .addObject("projectSummaryHtml", projectSummaryRenderingService.renderSummary(projectDetail, user))
        .addObject("backToTaskListUrl", ReverseRouter.route(on(TaskListController.class)
            .viewTaskList(projectDetail.getProject().getId(), null)));
  }

  private CancelProjectVersionService getCancelProjectVersionServiceForProjectType(ProjectDetail projectDetail) {
    return cancelProjectVersionServices
        .stream()
        .filter(cancelService -> cancelService.getSupportedProjectType().equals(projectDetail.getProjectType()))
        .findFirst()
        .orElseThrow(() -> new CancelProjectVersionImplementationException(String.format(
            "Could not find implementation of CancelDraftProjectVersionService that supports ProjectDetail with ID %d and type %s ",
            projectDetail.getId(),
            projectDetail.getProjectType()
        )));
  }
}
