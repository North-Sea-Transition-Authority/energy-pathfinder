package uk.co.ogauthority.pathfinder.controller.project.projectinformation;

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
import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.Quarter;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/project-information")
public class ProjectInformationController extends ProjectFormPageController {

  public static final String PAGE_NAME = "Project information & contact details";

  private final ProjectInformationService projectInformationService;

  @Autowired
  public ProjectInformationController(BreadcrumbService breadcrumbService,
                                      ProjectInformationService projectInformationService,
                                      ControllerHelperService controllerHelperService) {
    super(breadcrumbService, controllerHelperService);
    this.projectInformationService = projectInformationService;
  }

  @GetMapping
  public ModelAndView getProjectInformation(@PathVariable("projectId") Integer projectId,
                                            ProjectContext projectContext) {
    var form = projectInformationService.getForm(projectContext.getProjectDetails());
    return getProjectInformationModelAndView(projectId, form);
  }

  @PostMapping
  public ModelAndView saveProjectInformation(@PathVariable("projectId") Integer projectId,
                                             @Valid @ModelAttribute("form") ProjectInformationForm form,
                                             BindingResult bindingResult,
                                             ValidationType validationType,
                                             ProjectContext projectContext) {
    bindingResult = projectInformationService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getProjectInformationModelAndView(projectId, form),
        form,
        () -> {
          projectInformationService.createOrUpdate(projectContext.getProjectDetails(), form);
          //TODO PAT-314 in here have a parent service that includes projectInformation and the ProjectSetup services
          //Call that to filter any decom stuff if necessary? Or just call projectSetupService directly?

          return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
        });
  }

  private ModelAndView getProjectInformationModelAndView(Integer projectId, ProjectInformationForm form) {
    var modelAndView = new ModelAndView("project/projectinformation/projectInformation")
        .addObject("form", form)
        .addObject("pageName", PAGE_NAME)
        .addObject("discoveryFieldStage", FieldStage.getEntryAsMap(FieldStage.DISCOVERY))
        .addObject("discoveryFieldStageDescription", FieldStage.DISCOVERY.getDescription())
        .addObject("developmentFieldStage", FieldStage.getEntryAsMap(FieldStage.DEVELOPMENT))
        .addObject("developmentFieldStageDescription", FieldStage.DEVELOPMENT.getDescription())
        .addObject("operationsFieldStage", FieldStage.getEntryAsMap(FieldStage.OPERATIONS))
        .addObject("operationsFieldStageDescription", FieldStage.OPERATIONS.getDescription())
        .addObject("decommissioningFieldStage", FieldStage.getEntryAsMap(FieldStage.DECOMMISSIONING))
        .addObject("decommissioningFieldStageDescription", FieldStage.DECOMMISSIONING.getDescription())
        .addObject("energyTransitionFieldStage", FieldStage.getEntryAsMap(FieldStage.ENERGY_TRANSITION))
        .addObject("energyTransitionFieldStageDescription", FieldStage.ENERGY_TRANSITION.getDescription())
        .addObject("quarters", Quarter.getAllAsMap());

    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }

}
