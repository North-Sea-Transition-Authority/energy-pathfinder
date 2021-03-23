package uk.co.ogauthority.pathfinder.controller.project.subseainfrastructure;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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
import uk.co.ogauthority.pathfinder.model.enums.MeasurementUnits;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.InfrastructureStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaInfrastructureType;
import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaStructureMass;
import uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure.SubseaInfrastructureForm;
import uk.co.ogauthority.pathfinder.model.view.subseainfrastructure.SubseaInfrastructureView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.subseainfrastructure.SubseaInfrastructureService;
import uk.co.ogauthority.pathfinder.service.project.subseainfrastructure.SubseaInfrastructureSummaryService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/subsea-infrastructures")
@Profile("subsea-infrastructure") // PAT-495
public class SubseaInfrastructureController extends ProjectFormPageController {

  public static final String TASK_LIST_NAME = "Subsea infrastructure";
  public static final String SUMMARY_PAGE_NAME = "Subsea infrastructure to be decommissioned";
  private static final String FORM_PAGE_NAME = "Subsea infrastructure";
  public static final String REMOVE_PAGE_NAME = "Remove subsea infrastructure";

  private final SubseaInfrastructureService subseaInfrastructureService;
  private final SubseaInfrastructureSummaryService subseaInfrastructureSummaryService;

  @Autowired
  public SubseaInfrastructureController(BreadcrumbService breadcrumbService,
                                        ControllerHelperService controllerHelperService,
                                        SubseaInfrastructureService subseaInfrastructureService,
                                        SubseaInfrastructureSummaryService subseaInfrastructureSummaryService) {
    super(breadcrumbService, controllerHelperService);
    this.subseaInfrastructureService = subseaInfrastructureService;
    this.subseaInfrastructureSummaryService = subseaInfrastructureSummaryService;
  }

  @GetMapping
  public ModelAndView viewSubseaStructures(@PathVariable Integer projectId,
                                           ProjectContext projectContext) {
    var subseaInfrastructureViews = subseaInfrastructureSummaryService.getSubseaInfrastructureSummaryViews(
        projectContext.getProjectDetails()
    );
    return getSubseaStructuresSummaryModelAndView(projectId, subseaInfrastructureViews, ValidationResult.NOT_VALIDATED);
  }

  @PostMapping
  public ModelAndView saveSubseaStructures(@PathVariable("projectId") Integer projectId,
                                           ProjectContext projectContext) {
    var subseaInfrastructureViews =
        subseaInfrastructureSummaryService.getValidatedSubseaInfrastructureSummaryViews(
            projectContext.getProjectDetails()
        );

    var validationResult = subseaInfrastructureSummaryService.validateViews(subseaInfrastructureViews);

    return validationResult.equals(ValidationResult.VALID)
        ? ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null))
        : getSubseaStructuresSummaryModelAndView(projectId, subseaInfrastructureViews, validationResult);
  }

  @GetMapping("/subsea-infrastructure")
  public ModelAndView addSubseaInfrastructure(@PathVariable("projectId") Integer projectId,
                                              ProjectContext projectContext) {
    return getSubseaInfrastructureModelAndView(projectId, new SubseaInfrastructureForm());
  }

  @GetMapping("/subsea-infrastructure/{subseaInfrastructureId}/edit")
  public ModelAndView getSubseaInfrastructure(@PathVariable("projectId") Integer projectId,
                                              @PathVariable("subseaInfrastructureId") Integer subseaInfrastructureId,
                                              ProjectContext projectContext) {
    var form = subseaInfrastructureService.getForm(subseaInfrastructureId, projectContext.getProjectDetails());
    return getSubseaInfrastructureModelAndView(projectId, form);
  }

  @PostMapping("/subsea-infrastructure")
  public ModelAndView createSubseaInfrastructure(@PathVariable("projectId") Integer projectId,
                                                 @Valid @ModelAttribute("form") SubseaInfrastructureForm form,
                                                 BindingResult bindingResult,
                                                 ValidationType validationType,
                                                 ProjectContext projectContext) {
    bindingResult = subseaInfrastructureService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getSubseaInfrastructureModelAndView(projectId, form),
        form,
        () -> {
          var subseaInfrastructure = subseaInfrastructureService.createSubseaInfrastructure(projectContext.getProjectDetails(), form);
          AuditService.audit(
              AuditEvent.SUBSEA_INFRASTRUCTURE_UPDATED,
              String.format(
                  AuditEvent.SUBSEA_INFRASTRUCTURE_UPDATED.getMessage(),
                  subseaInfrastructure.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );
          return getSubseaInfrastructureSummaryRedirect(projectId);
        }
    );
  }

  @PostMapping("/subsea-infrastructure/{subseaInfrastructureId}/edit")
  public ModelAndView updateSubseaInfrastructure(@PathVariable("projectId") Integer projectId,
                                                 @PathVariable("subseaInfrastructureId") Integer subseaInfrastructureId,
                                                 @Valid @ModelAttribute("form") SubseaInfrastructureForm form,
                                                 BindingResult bindingResult,
                                                 ValidationType validationType,
                                                 ProjectContext projectContext) {
    bindingResult = subseaInfrastructureService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getSubseaInfrastructureModelAndView(projectId, form),
        form,
        () -> {
          subseaInfrastructureService.updateSubseaInfrastructure(
              subseaInfrastructureId,
              projectContext.getProjectDetails(),
              form
          );
          AuditService.audit(
              AuditEvent.SUBSEA_INFRASTRUCTURE_UPDATED,
              String.format(
                  AuditEvent.SUBSEA_INFRASTRUCTURE_UPDATED.getMessage(),
                  subseaInfrastructureId,
                  projectContext.getProjectDetails().getId()
              )
          );
          return getSubseaInfrastructureSummaryRedirect(projectId);
        }
    );
  }

  @GetMapping("/subsea-infrastructure/{subseaInfrastructureId}/remove/{displayOrder}")
  public ModelAndView removeSubseaInfrastructuresConfirmation(@PathVariable("projectId") Integer projectId,
                                                              @PathVariable("subseaInfrastructureId") Integer subseaInfrastructureId,
                                                              @PathVariable("displayOrder") Integer displayOrder,
                                                              ProjectContext projectContext) {
    var subseaInfrastructureView = subseaInfrastructureSummaryService.getSubseaInfrastructureSummaryView(
        subseaInfrastructureId,
        projectContext.getProjectDetails(),
        displayOrder
    );

    return removeSubseaInfrastructureModelAndView(projectId, subseaInfrastructureView);
  }

  @PostMapping("/subsea-infrastructure/{subseaInfrastructureId}/remove/{displayOrder}")
  public ModelAndView removeSubseaInfrastructure(@PathVariable("projectId") Integer projectId,
                                                 @PathVariable("subseaInfrastructureId") Integer subseaInfrastructureId,
                                                 @PathVariable("displayOrder") Integer displayOrder,
                                                 ProjectContext projectContext) {
    var subseaInfrastructureView = subseaInfrastructureService.getSubseaInfrastructure(
        subseaInfrastructureId,
        projectContext.getProjectDetails()
    );

    subseaInfrastructureService.deleteSubseaInfrastructure(subseaInfrastructureView);
    AuditService.audit(
        AuditEvent.SUBSEA_INFRASTRUCTURE_REMOVED,
        String.format(
            AuditEvent.SUBSEA_INFRASTRUCTURE_REMOVED.getMessage(),
            subseaInfrastructureId,
            projectContext.getProjectDetails().getId()
        )
    );

    return getSubseaInfrastructureSummaryRedirect(projectId);
  }

  private ModelAndView getSubseaStructuresSummaryModelAndView(Integer projectId,
                                                              List<SubseaInfrastructureView> subseaInfrastructureViews,
                                                              ValidationResult validationResult) {
    var modelAndView = new ModelAndView("project/subseainfrastructure/subseaInfrastructureFormSummary")
        .addObject("pageTitle", SUMMARY_PAGE_NAME)
        .addObject("addSubseaInfrastructureUrl",
            ReverseRouter.route(on(SubseaInfrastructureController.class).addSubseaInfrastructure(projectId, null))
        )
        .addObject("backToTaskListUrl",
            ControllerUtils.getBackToTaskListUrl(projectId)
        )
        .addObject("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectId))
        .addObject("subseaInfrastructureViews", subseaInfrastructureViews)
        .addObject("errorList",
            validationResult.equals(ValidationResult.INVALID)
                ? subseaInfrastructureSummaryService.getSubseaInfrastructureViewErrors(subseaInfrastructureViews)
                : null
        );

    breadcrumbService.fromTaskList(projectId, modelAndView, TASK_LIST_NAME);

    return modelAndView;
  }

  private ModelAndView getSubseaInfrastructureModelAndView(Integer projectId, SubseaInfrastructureForm form) {

    var modelAndView = new ModelAndView("project/subseainfrastructure/subseaInfrastructure")
        .addObject("form", form)
        .addObject("pageTitle", FORM_PAGE_NAME)
        .addObject("facilitiesRestUrl", subseaInfrastructureService.getFacilityRestUrl())
        .addObject("preSelectedFacilityMap", subseaInfrastructureService.getPreSelectedFacility(form))
        .addObject("infrastructureStatuses", InfrastructureStatus.getAllAsMap())
        .addObject("concreteMattressInfrastructureType",
            SubseaInfrastructureType.getEntryAsMap(SubseaInfrastructureType.CONCRETE_MATTRESSES)
        )
        .addObject("subseaInfrastructureType",
            SubseaInfrastructureType.getEntryAsMap(SubseaInfrastructureType.SUBSEA_STRUCTURE)
        )
        .addObject("otherInfrastructureType",
            SubseaInfrastructureType.getEntryAsMap(SubseaInfrastructureType.OTHER)
        )
        .addObject("subseaStructureMasses", SubseaStructureMass.getAllAsMap())
        .addObject("metricTonneUnit", MeasurementUnits.METRIC_TONNE);

    breadcrumbService.fromSubseaInfrastructure(projectId, modelAndView, FORM_PAGE_NAME);

    return modelAndView;
  }

  private ModelAndView getSubseaInfrastructureSummaryRedirect(Integer projectId) {
    return ReverseRouter.redirect(on(SubseaInfrastructureController.class).viewSubseaStructures(projectId, null));
  }

  private String getSubseaInfrastructureSummaryUrl(Integer projectId) {
    return ReverseRouter.route(on(SubseaInfrastructureController.class).viewSubseaStructures(projectId, null));
  }

  private ModelAndView removeSubseaInfrastructureModelAndView(Integer projectId, SubseaInfrastructureView subseaInfrastructureView) {
    var modelAndView = new ModelAndView("project/subseainfrastructure/removeSubseaInfrastructure")
        .addObject("subseaInfrastructureView", subseaInfrastructureView)
        .addObject("cancelUrl", getSubseaInfrastructureSummaryUrl(projectId))
        .addObject("pageName", REMOVE_PAGE_NAME);

    breadcrumbService.fromSubseaInfrastructure(projectId, modelAndView, REMOVE_PAGE_NAME);

    return modelAndView;
  }
}
