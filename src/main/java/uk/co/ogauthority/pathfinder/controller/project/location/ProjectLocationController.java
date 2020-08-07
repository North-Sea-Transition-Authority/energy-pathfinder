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
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.rest.DevUkRestController;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@RequestMapping("/project/{projectId}/location")
public class ProjectLocationController {
  public static final String PAGE_NAME = "Location";

  private final BreadcrumbService breadcrumbService;
  private final ProjectLocationService locationService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public ProjectLocationController(BreadcrumbService breadcrumbService,
                                   ProjectLocationService locationService,
                                   ControllerHelperService controllerHelperService) {
    this.breadcrumbService = breadcrumbService;
    this.locationService = locationService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getLocationDetails(@PathVariable("projectId") Integer projectId,
                                         ProjectContext projectContext) {
    return getLocationModelAndView(projectId, locationService.getForm(projectContext.getProjectDetails()));
  }

  @PostMapping
  public ModelAndView saveProjectLocation(@PathVariable("projectId") Integer projectId,
                                          @Valid @ModelAttribute("form") ProjectLocationForm form,
                                          BindingResult bindingResult,
                                          ValidationType validationType,
                                          ProjectContext projectContext) {
    bindingResult = locationService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult, getLocationModelAndView(projectId, form),
        () -> {
          locationService.createOrUpdate(projectContext.getProjectDetails(), form);

          return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
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
