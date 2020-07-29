package uk.co.ogauthority.pathfinder.controller.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;

@Controller
@RequestMapping("/project/{projectId}/tasks")
public class TaskListController {

  private final ProjectService projectService;
  private final BreadcrumbService breadcrumbService;

  @Autowired
  public TaskListController(ProjectService projectService,
                            BreadcrumbService breadcrumbService) {
    this.projectService = projectService;
    this.breadcrumbService = breadcrumbService;
  }

  @GetMapping
  public ModelAndView viewTaskList(@PathVariable("projectId") Integer projectId) {
    var currentDetail = projectService.getLatestDetail(projectId);
    var modelAndView = new ModelAndView("project/taskList");
    breadcrumbService.fromWorkArea(modelAndView, "Task list");
    return modelAndView;
  }

}
