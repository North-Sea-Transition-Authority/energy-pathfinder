package uk.co.ogauthority.pathfinder.controller.project.submission;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.SubmitProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSubmissionSummaryViewService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryViewService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/submit")
public class SubmitProjectController {
  public static final String PAGE_NAME = "Review and submit";

  private final ProjectSummaryViewService projectSummaryViewService;
  private final SubmitProjectService submitProjectService;
  private final ProjectSubmissionSummaryViewService projectSubmissionSummaryViewService;

  @Autowired
  public SubmitProjectController(ProjectSummaryViewService projectSummaryViewService,
                                 SubmitProjectService submitProjectService,
                                 ProjectSubmissionSummaryViewService projectSubmissionSummaryViewService) {
    this.projectSummaryViewService = projectSummaryViewService;
    this.submitProjectService = submitProjectService;
    this.projectSubmissionSummaryViewService = projectSubmissionSummaryViewService;
  }

  @GetMapping
  public ModelAndView getProjectSummary(@PathVariable("projectId") Integer projectId,
                                        ProjectContext projectContext) {
    var modelAndView = new ModelAndView("project/summary/reviewAndSubmit");
    var projectSummaryView = projectSummaryViewService.getProjectSummaryView(projectContext.getProjectDetails());

    modelAndView
        .addObject("projectSummaryView", projectSummaryView)
        .addObject("submitProjectUrl", ReverseRouter.route(on(SubmitProjectController.class).submitProject(projectId, null)))
        .addObject("taskListUrl", ControllerUtils.getBackToTaskListUrl(projectId));

    return modelAndView;
  }

  @PostMapping
  public ModelAndView submitProject(@PathVariable("projectId") Integer projectId,
                                    ProjectContext projectContext) {
    submitProjectService.submitProject(projectContext.getProjectDetails(), projectContext.getUserAccount());
    return ReverseRouter.redirect(on(SubmitProjectController.class).submitProjectConfirmation(projectId, null));
  }

  @GetMapping("/confirmation")
  @ProjectStatusCheck(status = ProjectStatus.QA)
  public ModelAndView submitProjectConfirmation(@PathVariable("projectId") Integer projectId,
                                                ProjectContext projectContext) {
    var modelAndView = new ModelAndView("project/summary/submitConfirmation");

    var projectSubmissionSummaryView = projectSubmissionSummaryViewService
        .getProjectSubmissionSummaryView(projectContext.getProjectDetails());

    modelAndView
        .addObject("projectSubmissionSummaryView", projectSubmissionSummaryView)
        .addObject("workAreaUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)));

    return modelAndView;
  }
}
