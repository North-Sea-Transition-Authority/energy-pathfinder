package uk.co.ogauthority.pathfinder.controller.project;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.location.ProjectLocationController;
import uk.co.ogauthority.pathfinder.controller.project.projectinformation.ProjectInformationController;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;

@Controller
@RequestMapping("/project/{projectId}/tasks")
public class TaskListController {

  private final ProjectService projectService;
  private final ProjectInformationService projectInformationService;
  private final BreadcrumbService breadcrumbService;
  private final ProjectLocationService projectLocationService;

  @Autowired
  public TaskListController(ProjectService projectService,
                            ProjectInformationService projectInformationService,
                            BreadcrumbService breadcrumbService,
                            ProjectLocationService projectLocationService) {
    this.projectService = projectService;
    this.projectInformationService = projectInformationService;
    this.breadcrumbService = breadcrumbService;
    this.projectLocationService = projectLocationService;
  }

  @GetMapping
  public ModelAndView viewTaskList(@PathVariable("projectId") Integer projectId) {
    var currentDetail = projectService.getLatestDetail(projectId)
        .orElseThrow(() -> new EntityNotFoundException(String.format("Unable to find project with id: %d", projectId)));

    var modelAndView = new ModelAndView("project/taskList");
    modelAndView.addObject("projectInformationUrl",
        ReverseRouter.route(on(ProjectInformationController.class).getProjectInformation(null, projectId))
    );
    modelAndView.addObject("projectInformationCompleted", projectInformationService.isComplete(currentDetail));

    modelAndView.addObject("locationUrl",
        ReverseRouter.route(on(ProjectLocationController.class).getLocationDetails(null, projectId))
    );
    modelAndView.addObject("projectLocationCompleted", projectLocationService.isComplete(currentDetail));
    breadcrumbService.fromWorkArea(modelAndView, "Task list");

    return modelAndView;
  }

}
