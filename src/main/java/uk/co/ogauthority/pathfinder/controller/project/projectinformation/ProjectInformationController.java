package uk.co.ogauthority.pathfinder.controller.project.projectinformation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityNotFoundException;
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
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;

@Controller
@RequestMapping("/project/{projectId}/project-information")
public class ProjectInformationController {

  public static final String PAGE_NAME = "Project information";

  private final ProjectService projectService;
  private final BreadcrumbService breadcrumbService;
  private final ProjectInformationService projectInformationService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public ProjectInformationController(ProjectService projectService,
                                      BreadcrumbService breadcrumbService,
                                      ProjectInformationService projectInformationService,
                                      ControllerHelperService controllerHelperService) {
    this.projectService = projectService;
    this.breadcrumbService = breadcrumbService;
    this.projectInformationService = projectInformationService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getProjectInformation(AuthenticatedUserAccount user,
                                            @PathVariable("projectId") Integer projectId) {
    //TODO PAT-133 Fetch with context of project and user
    var details = projectService.getLatestDetail(projectId)
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Unable to find project detail for project id  %d", projectId)));


    return getProjectInformationModelAndView(projectId)
        .addObject("form", projectInformationService.getForm(details));
  }

  @PostMapping
  public ModelAndView saveProjectInformation(AuthenticatedUserAccount user,
                                             @PathVariable("projectId") Integer projectId,
                                             @Valid @ModelAttribute("form") ProjectInformationForm form,
                                             BindingResult bindingResult,
                                             ValidationType validationType) {
    var details = projectService.getLatestDetail(projectId)
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Unable to find project detail for project id  %d", projectId)));

    bindingResult = projectInformationService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult, getProjectInformationModelAndView(projectId),
        () -> {
          projectInformationService.createOrUpdate(details, form);

          return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId));
        });
  }

  private ModelAndView getProjectInformationModelAndView(Integer projectId) {
    var modelAndView = new ModelAndView("project/projectinformation/projectInformation")
        .addObject("fieldStages",
            Stream.of(FieldStage.values()).sorted(Comparator.comparing(FieldStage::getDisplayOrder)).collect(
                Collectors.toList()));

    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }

}
