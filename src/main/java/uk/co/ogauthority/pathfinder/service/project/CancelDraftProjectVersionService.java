package uk.co.ogauthority.pathfinder.service.project;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.projectupdate.ProjectUpdateType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryRenderingService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorProjectUpdateService;

@Service
public class CancelDraftProjectVersionService {

  public static final String CANCEL_DRAFT_TEMPLATE_PATH = "project/cancelDraft";

  private final ProjectService projectService;
  private final ProjectUpdateService projectUpdateService;
  private final RegulatorProjectUpdateService regulatorProjectUpdateService;
  private final ProjectSummaryRenderingService projectSummaryRenderingService;
  private final List<ProjectFormSectionService> projectFormSectionServices;

  @Autowired
  public CancelDraftProjectVersionService(ProjectService projectService,
                                          ProjectUpdateService projectUpdateService,
                                          RegulatorProjectUpdateService regulatorProjectUpdateService,
                                          ProjectSummaryRenderingService projectSummaryRenderingService,
                                          List<ProjectFormSectionService> projectFormSectionServices) {
    this.projectService = projectService;
    this.projectUpdateService = projectUpdateService;
    this.regulatorProjectUpdateService = regulatorProjectUpdateService;
    this.projectSummaryRenderingService = projectSummaryRenderingService;
    this.projectFormSectionServices = projectFormSectionServices;
  }

  @Transactional
  public void cancelDraft(ProjectDetail projectDetail) {
    projectFormSectionServices.forEach(
        projectFormSectionService -> projectFormSectionService.removeSectionData(projectDetail)
    );
    if (!projectDetail.isFirstVersion()) {
      projectUpdateService.getByToDetail(projectDetail).ifPresent(projectUpdate -> {
        if (projectUpdate.getUpdateType() == ProjectUpdateType.REGULATOR_REQUESTED) {
          regulatorProjectUpdateService.deleteRegulatorRequestedUpdate(projectUpdate);
        }
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
}
