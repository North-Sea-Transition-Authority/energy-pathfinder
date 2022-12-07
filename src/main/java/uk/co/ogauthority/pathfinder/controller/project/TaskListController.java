package uk.co.ogauthority.pathfinder.controller.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.tasks.TaskListService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@AllowProjectContributorAccess
@ProjectTypeCheck(types = { ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN })
@RequestMapping("/project/{projectId}/tasks")
public class TaskListController {

  private final BreadcrumbService breadcrumbService;
  private final TaskListService taskListService;

  @Autowired
  public TaskListController(BreadcrumbService breadcrumbService,
                            TaskListService taskListService) {
    this.breadcrumbService = breadcrumbService;
    this.taskListService = taskListService;
  }

  @GetMapping
  public ModelAndView viewTaskList(@PathVariable("projectId") Integer projectId,
                                   ProjectContext projectContext) {
    var modelAndView = taskListService.getTaskListModelAndView(
        projectContext.getProjectDetails(),
        projectContext.getUserToProjectRelationships(),
        projectContext.getUserAccount()
    );
    breadcrumbService.fromWorkArea(modelAndView, "Task list");

    return modelAndView;
  }

}
