package uk.co.ogauthority.pathfinder.controller.project.platformsfpsos;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import javax.validation.Valid;
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
import uk.co.ogauthority.pathfinder.controller.rest.DevUkRestController;
import uk.co.ogauthority.pathfinder.model.enums.MeasurementUnits;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.FuturePlans;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.PlatformFpsoInfrastructureType;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.SubstructureRemovalPremise;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoForm;
import uk.co.ogauthority.pathfinder.model.view.platformfpso.PlatformFpsoView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.platformsfpsos.PlatformsFpsosService;
import uk.co.ogauthority.pathfinder.service.project.platformsfpsos.PlatformsFpsosSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/platforms-fpsos")
public class PlatformsFpsosController extends ProjectFormPageController {

  public static final String TASK_LIST_NAME = "Platforms and FPSOs";
  public static final String SUMMARY_PAGE_NAME = "Platforms and FPSOs to be decommissioned";
  public static final String FORM_PAGE_NAME = "Platform or FPSO";
  public static final String REMOVE_PAGE_NAME = "Remove platform or FPSO";

  private final PlatformsFpsosService platformsFpsosService;
  private final PlatformsFpsosSummaryService platformsFpsosSummaryService;

  public PlatformsFpsosController(BreadcrumbService breadcrumbService,
                                  ControllerHelperService controllerHelperService,
                                  PlatformsFpsosService platformsFpsosService,
                                  PlatformsFpsosSummaryService platformsFpsosSummaryService) {
    super(breadcrumbService, controllerHelperService);
    this.platformsFpsosService = platformsFpsosService;
    this.platformsFpsosSummaryService = platformsFpsosSummaryService;
  }

  @GetMapping
  public ModelAndView viewPlatformsFpsos(@PathVariable("projectId") Integer projectId,
                                         ProjectContext projectContext) {
    return getViewPlatformsFpsosModelAndView(
        projectId,
        platformsFpsosSummaryService.getSummaryViews(projectContext.getProjectDetails()),
        ValidationResult.NOT_VALIDATED
    );
  }

  @PostMapping
  public ModelAndView savePlatformsFpsos(@PathVariable("projectId") Integer projectId,
                                         ProjectContext projectContext) {
    var views = platformsFpsosSummaryService.getValidatedSummaryViews(projectContext.getProjectDetails());
    var validationResult = platformsFpsosSummaryService.validateViews(views);

    if (validationResult.equals(ValidationResult.INVALID)) {
      return getViewPlatformsFpsosModelAndView(
          projectId,
          views,
          validationResult
          );
    }

    return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
  }

  @GetMapping("/platform-fpso")
  public ModelAndView addPlatformFpso(@PathVariable("projectId") Integer projectId,
                                      ProjectContext projectContext) {
    return getPlatformFpsoFormModelAndView(projectId, new PlatformFpsoForm());
  }

  @PostMapping("/platform-fpso")
  public ModelAndView saveNewPlatformFpso(@PathVariable("projectId") Integer projectId,
                                          @Valid @ModelAttribute("form") PlatformFpsoForm form,
                                          BindingResult bindingResult,
                                          ValidationType validationType,
                                          ProjectContext projectContext
  ) {
    bindingResult = platformsFpsosService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getPlatformFpsoFormModelAndView(projectId, form),
        form,
        () -> {
          var platformFpso = platformsFpsosService.createPlatformFpso(projectContext.getProjectDetails(), form);
          AuditService.audit(
              AuditEvent.PLATFORM_FPSO_UPDATED,
              String.format(
                  AuditEvent.PLATFORM_FPSO_UPDATED.getMessage(),
                  platformFpso.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );
          return ReverseRouter.redirect(on(PlatformsFpsosController.class).viewPlatformsFpsos(projectId, null));
        }
    );
  }

  @GetMapping("/platform-fpso/{platformFpsoId}/edit")
  public ModelAndView editPlatformFpso(@PathVariable("projectId") Integer projectId,
                                       @PathVariable("platformFpsoId") Integer platformFpsoId,
                                       ProjectContext projectContext) {
    var platformFpso = platformsFpsosService.getOrError(platformFpsoId);
    return getPlatformFpsoFormModelAndView(
        projectId,
        platformsFpsosService.getForm(platformFpso)
    );
  }

  @PostMapping("/platform-fpso/{platformFpsoId}/edit")
  public ModelAndView updatePlatformFpso(@PathVariable("projectId") Integer projectId,
                                         @PathVariable("platformFpsoId") Integer platformFpsoId,
                                         @Valid @ModelAttribute("form") PlatformFpsoForm form,
                                         BindingResult bindingResult,
                                         ValidationType validationType,
                                         ProjectContext projectContext) {
    var platformFpso = platformsFpsosService.getOrError(platformFpsoId);
    bindingResult = platformsFpsosService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getPlatformFpsoFormModelAndView(projectId, form),
        form,
        () -> {
          platformsFpsosService.updatePlatformFpso(projectContext.getProjectDetails(), platformFpso, form);
          AuditService.audit(
              AuditEvent.PLATFORM_FPSO_UPDATED,
              String.format(
                  AuditEvent.PLATFORM_FPSO_UPDATED.getMessage(),
                  platformFpsoId,
                  projectContext.getProjectDetails().getId()
              )
          );
          return ReverseRouter.redirect(on(PlatformsFpsosController.class).viewPlatformsFpsos(projectId, null));
        }
    );
  }

  @GetMapping("/platform-fpso/{platformFpsoId}/remove/{displayOrder}")
  public ModelAndView removePlatformFpsoConfirm(@PathVariable("projectId") Integer projectId,
                                                @PathVariable("platformFpsoId") Integer platformFpsoId,
                                                @PathVariable("displayOrder") Integer displayOrder,
                                                ProjectContext projectContext) {
    var platformFpso = platformsFpsosService.getOrError(platformFpsoId);
    var modelAndView = new ModelAndView("project/platformsfpsos/removePlatformFpso")
        .addObject("view", platformsFpsosSummaryService.getView(platformFpso, displayOrder, projectId))
        .addObject("cancelUrl", ReverseRouter.route(on(PlatformsFpsosController.class).viewPlatformsFpsos(projectId, null)));
    breadcrumbService.fromPlatformsFpsos(projectId, modelAndView, REMOVE_PAGE_NAME);
    return modelAndView;
  }

  @PostMapping("/platform-fpso/{platformFpsoId}/remove/{displayOrder}")
  public ModelAndView removePlatformFpso(@PathVariable("projectId") Integer projectId,
                                         @PathVariable("platformFpsoId") Integer platformFpsoId,
                                         @PathVariable("displayOrder") Integer displayOrder,
                                         ProjectContext projectContext) {
    var platformFpso = platformsFpsosService.getOrError(platformFpsoId);
    platformsFpsosService.delete(platformFpso);
    AuditService.audit(
        AuditEvent.PLATFORM_FPSO_REMOVED,
        String.format(
            AuditEvent.PLATFORM_FPSO_REMOVED.getMessage(),
            platformFpsoId,
            projectContext.getProjectDetails().getId()
        )
    );
    return ReverseRouter.redirect(on(PlatformsFpsosController.class).viewPlatformsFpsos(projectId, null));
  }

  private ModelAndView getPlatformFpsoFormModelAndView(Integer projectId, PlatformFpsoForm form) {
    var modelAndView = new ModelAndView("project/platformsfpsos/platformsFpsosForm")
        .addObject("form", form)
        .addObject("platformInfrastructureType",
            PlatformFpsoInfrastructureType.getEntryAsMap(PlatformFpsoInfrastructureType.PLATFORM)
        )
        .addObject("fpsoInfrastructureType",
            PlatformFpsoInfrastructureType.getEntryAsMap(PlatformFpsoInfrastructureType.FPSO)
        )
        .addObject("facilitiesUrl", SearchSelectorService.route(on(DevUkRestController.class).searchFacilitiesWithManualEntry(null)))
        .addObject("mtUnit", MeasurementUnits.METRIC_TONNE)
        .addObject("preselectedPlatformStructure", platformsFpsosService.getPreselectedPlatformStructure(form))
        .addObject("preselectedFpsoStructure", platformsFpsosService.getPreselectedFpsoStructure(form))
        .addObject("substructureRemovalPremiseMap", SubstructureRemovalPremise.getAllAsMap())
        .addObject("futurePlansMap", FuturePlans.getAllAsMap());
    breadcrumbService.fromPlatformsFpsos(projectId, modelAndView, FORM_PAGE_NAME);
    return modelAndView;
  }

  private ModelAndView getViewPlatformsFpsosModelAndView(Integer projectId,
                                                         List<PlatformFpsoView> views,
                                                         ValidationResult validationResult

  ) {
    var modelAndView = new ModelAndView("project/platformsfpsos/platformsFpsoFormSummary")
        .addObject("pageName", SUMMARY_PAGE_NAME)
        .addObject("views", views)
        .addObject("isValid", validationResult.equals(ValidationResult.VALID))
        .addObject("errorSummary",
            validationResult.equals(ValidationResult.INVALID)
            ? platformsFpsosSummaryService.getErrors(views)
            : null
        )
        .addObject("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId))
        .addObject("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectId))
        .addObject("addPlatformFpsoUrl", ReverseRouter.route(on(PlatformsFpsosController.class).addPlatformFpso(projectId, null)));
    breadcrumbService.fromTaskList(projectId, modelAndView, TASK_LIST_NAME);
    return modelAndView;
  }
}
