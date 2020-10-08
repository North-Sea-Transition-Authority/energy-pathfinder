package uk.co.ogauthority.pathfinder.controller.project.platformsfpsos;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.rest.DevUkRestController;
import uk.co.ogauthority.pathfinder.model.enums.MeasurementUnits;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.FuturePlans;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.SubstructureRemovalPremise;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.platformsfpsos.PlatformsFpsosService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/platforms-fpsos")
public class PlatformsFpsosController extends ProjectFormPageController {

  public static final String SUMMARY_PAGE_NAME = "Platforms and FPSOs";
  public static final String FORM_PAGE_NAME = "Platform or FPSO";

  private final PlatformsFpsosService platformsFpsosService;

  public PlatformsFpsosController(BreadcrumbService breadcrumbService,
                                  ControllerHelperService controllerHelperService,
                                  PlatformsFpsosService platformsFpsosService) {
    super(breadcrumbService, controllerHelperService);
    this.platformsFpsosService = platformsFpsosService;
  }

  @GetMapping("")
  public ModelAndView viewPlatformFpso(@PathVariable("projectId") Integer projectId,
                                       ProjectContext projectContext) {
    return getViewPlatformsFpsosModelAndView(projectId);
  }

  @GetMapping("/add")
  public ModelAndView addPlatformFpso(@PathVariable("projectId") Integer projectId,
                                      ProjectContext projectContext) {
    return getPlatformFpsoFormModelAndView(projectId, platformsFpsosService.getForm());
  }

  @PostMapping("/add")
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
          platformsFpsosService.createPlatformFpso(projectContext.getProjectDetails(), form);
          return ReverseRouter.redirect(on(PlatformsFpsosController.class).viewPlatformFpso(projectId, null));
        }
    );
  }

  private ModelAndView getPlatformFpsoFormModelAndView(Integer projectId, PlatformFpsoForm form) {
    var modelAndView = new ModelAndView("project/platformsfpsos/platformsFpsosForm")
        .addObject("form", form)
        .addObject("facilitiesUrl", SearchSelectorService.route(on(DevUkRestController.class).searchFacilitiesWithManualEntry(null)))
        .addObject("mtUnit", MeasurementUnits.METRIC_TONNE)
        .addObject("preselectedStructure", platformsFpsosService.getPreselectedStructure(form))
        .addObject("substructureRemovalPremiseMap", SubstructureRemovalPremise.getAllAsMap())
        .addObject("futurePlansMap", FuturePlans.getAllAsMap());
    breadcrumbService.fromUpcomingTenders(projectId, modelAndView, FORM_PAGE_NAME);
    return modelAndView;
  }

  private ModelAndView getViewPlatformsFpsosModelAndView(Integer projectId) {
    var modelAndView = new ModelAndView("project/platformsfpsos/platformsFpsosSummary")
        .addObject("addPlatformFpsoUrl", ReverseRouter.route(on(PlatformsFpsosController.class).addPlatformFpso(projectId, null)));
    breadcrumbService.fromTaskList(projectId, modelAndView, SUMMARY_PAGE_NAME);
    return modelAndView;
  }
}
