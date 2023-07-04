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
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/project-information")
public class ProjectInformationController extends ProjectFormPageController {

  public static final String PAGE_NAME = "Project information & contact details";

  private final ProjectInformationService projectInformationService;
  private final ProjectSetupService projectSetupService;

  @Autowired
  public ProjectInformationController(BreadcrumbService breadcrumbService,
                                      ProjectInformationService projectInformationService,
                                      ControllerHelperService controllerHelperService,
                                      ProjectSetupService projectSetupService) {
    super(breadcrumbService, controllerHelperService);
    this.projectInformationService = projectInformationService;
    this.projectSetupService = projectSetupService;
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
          var projectInformation = projectInformationService.createOrUpdate(
              projectContext.getProjectDetails(),
              form
          );

          projectSetupService.removeTaskListSetupSectionsNotApplicableToFieldStage(
              projectContext.getProjectDetails(),
              projectInformation.getFieldStage()
          );

          AuditService.audit(
              AuditEvent.PROJECT_INFORMATION_UPDATED,
              String.format(
                  AuditEvent.PROJECT_INFORMATION_UPDATED.getMessage(),
                  projectContext.getProjectDetails().getId()
              )
          );
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
        .addObject("decommissioningFieldStage", FieldStage.getEntryAsMap(FieldStage.DECOMMISSIONING))
        .addObject("decommissioningFieldStageDescription", FieldStage.DECOMMISSIONING.getDescription())
        .addObject("carbonCaptureAndStorageFieldStage", FieldStage.getEntryAsMap(FieldStage.CARBON_CAPTURE_AND_STORAGE))
        .addObject("carbonCaptureAndStorageFieldStageDescription", FieldStage.CARBON_CAPTURE_AND_STORAGE.getDescription())
        .addObject("carbonCaptureAndStorageCategories", FieldStageSubCategory.getAllAsMap(FieldStage.CARBON_CAPTURE_AND_STORAGE))
        .addObject("hydrogenFieldStage", FieldStage.getEntryAsMap(FieldStage.HYDROGEN))
        .addObject("hydrogenFieldStageDescription", FieldStage.HYDROGEN.getDescription())
        .addObject("offshoreElectrificationFieldStage", FieldStage.getEntryAsMap(FieldStage.OFFSHORE_ELECTRIFICATION))
        .addObject("offshoreElectrificationFieldStageDescription", FieldStage.OFFSHORE_ELECTRIFICATION.getDescription())
        .addObject("offshoreWindFieldStage", FieldStage.getEntryAsMap(FieldStage.OFFSHORE_WIND))
        .addObject("offshoreWindFieldStageDescription", FieldStage.OFFSHORE_WIND.getDescription())
        .addObject("offshoreWindCategories", FieldStageSubCategory.getAllAsMap(FieldStage.OFFSHORE_WIND))
        .addObject("quarters", Quarter.getAllAsMap());

    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }

}
