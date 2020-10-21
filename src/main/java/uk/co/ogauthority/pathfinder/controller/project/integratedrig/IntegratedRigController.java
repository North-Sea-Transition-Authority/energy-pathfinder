package uk.co.ogauthority.pathfinder.controller.project.integratedrig;

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
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.integratedrig.IntegratedRigIntentionToReactivate;
import uk.co.ogauthority.pathfinder.model.enums.project.integratedrig.IntegratedRigStatus;
import uk.co.ogauthority.pathfinder.model.form.project.integratedrig.IntegratedRigForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.integratedrig.IntegratedRigService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/integrated-rigs")
public class IntegratedRigController extends ProjectFormPageController {

  public static final String SUMMARY_PAGE_NAME = "Integrated rigs";
  private static final String FORM_PAGE_NAME = "Integrated rig";

  private final IntegratedRigService integratedRigService;

  @Autowired
  public IntegratedRigController(BreadcrumbService breadcrumbService,
                                 ControllerHelperService controllerHelperService,
                                 IntegratedRigService integratedRigService) {
    super(breadcrumbService, controllerHelperService);
    this.integratedRigService = integratedRigService;
  }

  @GetMapping
  public ModelAndView getIntegratedRigs(@PathVariable Integer projectId,
                                        ProjectContext projectContext) {
    return getIntegratedRigsSummaryModelAndView(projectId);
  }

  @GetMapping("/integrated-rig")
  public ModelAndView addIntegratedRig(@PathVariable("projectId") Integer projectId,
                                              ProjectContext projectContext) {
    return getIntegratedRigModelAndView(projectId, new IntegratedRigForm());
  }

  @PostMapping("/integrated-rig")
  public ModelAndView createIntegratedRig(@PathVariable("projectId") Integer projectId,
                                          @Valid @ModelAttribute("form") IntegratedRigForm form,
                                          BindingResult bindingResult,
                                          ValidationType validationType,
                                          ProjectContext projectContext) {
    bindingResult = integratedRigService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getIntegratedRigModelAndView(projectId, form),
        form,
        () -> {
          integratedRigService.createIntegratedRig(projectContext.getProjectDetails(), form);
          return getIntegratedRigSummaryRedirect(projectId);
        }
    );
  }

  private ModelAndView getIntegratedRigsSummaryModelAndView(Integer projectId) {
    var modelAndView = new ModelAndView("project/integratedrig/integratedRigSummary")
        .addObject("pageTitle", SUMMARY_PAGE_NAME)
        .addObject("addIntegratedRigUrl",
            ReverseRouter.route(on(IntegratedRigController.class).addIntegratedRig(projectId, null))
        )
        .addObject("backToTaskListUrl",
            ControllerUtils.getBackToTaskListUrl(projectId)
        );

    breadcrumbService.fromTaskList(projectId, modelAndView, SUMMARY_PAGE_NAME);

    return modelAndView;
  }

  private ModelAndView getIntegratedRigModelAndView(Integer projectId, IntegratedRigForm form) {
    var modelAndView = new ModelAndView("project/integratedrig/integratedRig")
        .addObject("form", form)
        .addObject("pageTitle", FORM_PAGE_NAME)
        .addObject("facilitiesRestUrl", integratedRigService.getFacilityRestUrl())
        .addObject("preSelectedFacilityMap", integratedRigService.getPreSelectedFacility(form))
        .addObject("integratedRigStatuses", IntegratedRigStatus.getAllAsMap())
        .addObject("integratedRigIntentionsToReactivate", IntegratedRigIntentionToReactivate.getAllAsMap());

    breadcrumbService.fromIntegrateRig(projectId, modelAndView, FORM_PAGE_NAME);

    return modelAndView;
  }

  private ModelAndView getIntegratedRigSummaryRedirect(Integer projectId) {
    return ReverseRouter.redirect(on(IntegratedRigController.class).getIntegratedRigs(projectId, null));
  }
}
