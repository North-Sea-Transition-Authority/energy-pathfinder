package uk.co.ogauthority.pathfinder.controller.project;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsEventCategory;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsService;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsUtils;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.cancellation.CancelDraftProjectVersionService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = { ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN })
@RequestMapping("/project/{projectId}/cancel-draft")
public class CancelDraftProjectVersionController {

  private final CancelDraftProjectVersionService cancelDraftProjectVersionService;
  private final AnalyticsService analyticsService;

  @Autowired
  public CancelDraftProjectVersionController(CancelDraftProjectVersionService cancelDraftProjectVersionService,
                                             AnalyticsService analyticsService) {
    this.cancelDraftProjectVersionService = cancelDraftProjectVersionService;
    this.analyticsService = analyticsService;
  }

  @GetMapping
  public ModelAndView getCancelDraft(@PathVariable("projectId") Integer projectId,
                                     ProjectContext projectContext,
                                     AuthenticatedUserAccount user) {
    final var projectDetail = projectContext.getProjectDetails();
    checkCancellingPermitted(projectDetail);
    return cancelDraftProjectVersionService.getCancelDraftModelAndView(projectDetail, user);
  }

  @PostMapping
  public ModelAndView cancelDraft(@PathVariable("projectId") Integer projectId,
                                  ProjectContext projectContext,
                                  AuthenticatedUserAccount user,
                                  @CookieValue(name = AnalyticsUtils.GA_CLIENT_ID_COOKIE_NAME, required = false)
                                        Optional<String> analyticsClientId) {
    final var projectDetail = projectContext.getProjectDetails();
    checkCancellingPermitted(projectDetail);
    cancelDraftProjectVersionService.cancelDraft(projectContext.getProjectDetails());
    analyticsService.sendAnalyticsEvent(analyticsClientId, AnalyticsEventCategory.PROJECT_DRAFT_CANCELLED,
        Map.of("project_type", projectDetail.getProjectType().name()));
    return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(null, null));
  }

  private void checkCancellingPermitted(ProjectDetail projectDetail) {
    if (!cancelDraftProjectVersionService.isCancellable(projectDetail)) {
      throw new AccessDeniedException(String.format(
          "Project detail with ID %d and type %s does not support version %d being cancelled",
          projectDetail.getId(),
          projectDetail.getProjectType().name(),
          projectDetail.getVersion()
      ));
    }
  }
}
