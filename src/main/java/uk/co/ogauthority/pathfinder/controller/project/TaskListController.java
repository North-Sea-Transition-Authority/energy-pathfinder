package uk.co.ogauthority.pathfinder.controller.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;

@Controller
@RequestMapping("/project/{applicationId}/tasks")
public class TaskListController {

  private BreadcrumbService breadcrumbService;

  @Autowired
  public TaskListController(BreadcrumbService breadcrumbService) {
    this.breadcrumbService = breadcrumbService;
  }

  @GetMapping
  public ModelAndView viewTaskList(@PathVariable("applicationId") Integer applicationId) {
    var modelAndView = new ModelAndView("project/taskList");
    breadcrumbService.fromWorkArea(modelAndView, "Task list");
    return modelAndView;
  }

}
