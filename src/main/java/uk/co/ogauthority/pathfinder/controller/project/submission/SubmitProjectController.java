package uk.co.ogauthority.pathfinder.controller.project.submission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryViewService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/submit")
public class SubmitProjectController {
  public static final String PAGE_NAME = "Review and submit";

  private final ProjectSummaryViewService projectSummaryViewService;

  @Autowired
  public SubmitProjectController(ProjectSummaryViewService projectSummaryViewService) {
    this.projectSummaryViewService = projectSummaryViewService;
  }

  @GetMapping
  public ModelAndView getProjectSummary(@PathVariable("projectId") Integer projectId,
                                        ProjectContext projectContext) {
    var modelAndView = new ModelAndView("project/summary/reviewAndSubmit");
    var projectSummaryView = projectSummaryViewService.getProjectSummaryView(projectContext.getProjectDetails());

    modelAndView
        .addObject("projectSummaryView", projectSummaryView)
        .addObject("taskListUrl", ControllerUtils.getBackToTaskListUrl(projectId));

    return modelAndView;
  }
}
