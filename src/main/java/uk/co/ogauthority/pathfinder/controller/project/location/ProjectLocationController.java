package uk.co.ogauthority.pathfinder.controller.project.location;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.rest.DevUkRestController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Controller
@RequestMapping("/project/{projectId}/location")
public class ProjectLocationController {
  public static final String PAGE_NAME = "Location";

  private final ProjectService projectService;
  private final BreadcrumbService breadcrumbService;
  private final ProjectLocationService locationService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public ProjectLocationController(ProjectService projectService,
                                   BreadcrumbService breadcrumbService,
                                   ProjectLocationService locationService,
                                   ControllerHelperService controllerHelperService) {
    this.projectService = projectService;
    this.breadcrumbService = breadcrumbService;
    this.locationService = locationService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getLocationDetails(AuthenticatedUserAccount user,
                                         @PathVariable("projectId") Integer projectId) {
    //TODO PAT-133 Fetch with context of project and user
    var details = projectService.getLatestDetail(projectId)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Unable to find project detail for project id  %d", projectId)));

    return getLocationModelAndView(projectId, locationService.getForm(details));
  }

  @PostMapping
  public ModelAndView saveProjectLocation(AuthenticatedUserAccount user,
                                          @PathVariable("projectId") Integer projectId,
                                          @Valid @ModelAttribute("form") ProjectLocationForm form,
                                          BindingResult bindingResult,
                                          ValidationType validationType) {
    var details = projectService.getLatestDetail(projectId)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Unable to find project detail for project id  %d", projectId)));
    bindingResult = locationService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult, getLocationModelAndView(projectId, form),
        () -> {
          locationService.createOrUpdate(details, form);

          return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId));
        });
  }


  public ModelAndView getLocationModelAndView(Integer projectId, ProjectLocationForm form) {
    var modelAndView = new ModelAndView("project/location/location")
        .addObject("fieldsRestUrl", SearchSelectorService.route(on(DevUkRestController.class).searchFields(null)))
        .addObject("form", form)
        .addObject("preselectedField", locationService.getPreSelectedField(form));

    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }
}
