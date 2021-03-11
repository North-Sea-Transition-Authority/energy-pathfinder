package uk.co.ogauthority.pathfinder.controller.project.integratedrig;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
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
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.integratedrig.IntegratedRigIntentionToReactivate;
import uk.co.ogauthority.pathfinder.model.enums.project.integratedrig.IntegratedRigStatus;
import uk.co.ogauthority.pathfinder.model.form.project.integratedrig.IntegratedRigForm;
import uk.co.ogauthority.pathfinder.model.view.integratedrig.IntegratedRigView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.integratedrig.IntegratedRigService;
import uk.co.ogauthority.pathfinder.service.project.integratedrig.IntegratedRigSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/integrated-rigs")
public class IntegratedRigController extends ProjectFormPageController {

  public static final String TASK_LIST_NAME = "Integrated rigs";
  public static final String SUMMARY_PAGE_NAME = "Integrated rigs to be decommissioned";
  private static final String FORM_PAGE_NAME = "Integrated rig";
  public static final String REMOVE_PAGE_NAME = "Remove integrated rig";

  private final IntegratedRigService integratedRigService;
  private final IntegratedRigSummaryService integratedRigSummaryService;

  @Autowired
  public IntegratedRigController(BreadcrumbService breadcrumbService,
                                 ControllerHelperService controllerHelperService,
                                 IntegratedRigService integratedRigService,
                                 IntegratedRigSummaryService integratedRigSummaryService) {
    super(breadcrumbService, controllerHelperService);
    this.integratedRigService = integratedRigService;
    this.integratedRigSummaryService = integratedRigSummaryService;
  }

  @GetMapping
  public ModelAndView viewIntegratedRigs(@PathVariable Integer projectId,
                                         ProjectContext projectContext) {
    var integratedRigViews = integratedRigSummaryService.getIntegratedRigSummaryViews(
        projectContext.getProjectDetails()
    );
    return getIntegratedRigsSummaryModelAndView(projectId, integratedRigViews, ValidationResult.NOT_VALIDATED);
  }

  @PostMapping
  public ModelAndView saveIntegratedRigs(@PathVariable("projectId") Integer projectId,
                                         ProjectContext projectContext) {
    var integratedRigViews =
        integratedRigSummaryService.getValidatedIntegratedRigSummaryViews(
            projectContext.getProjectDetails()
        );

    var validationResult = integratedRigSummaryService.validateViews(integratedRigViews);

    return validationResult.equals(ValidationResult.VALID)
        ? ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null))
        : getIntegratedRigsSummaryModelAndView(projectId, integratedRigViews, validationResult);
  }

  @GetMapping("/integrated-rig")
  public ModelAndView addIntegratedRig(@PathVariable("projectId") Integer projectId,
                                       ProjectContext projectContext) {
    return getIntegratedRigModelAndView(projectId, new IntegratedRigForm());
  }

  @GetMapping("/integrated-rig/{integratedRigId}/edit")
  public ModelAndView getIntegratedRig(@PathVariable("projectId") Integer projectId,
                                       @PathVariable("integratedRigId") Integer integratedRigId,
                                       ProjectContext projectContext) {
    var form = integratedRigService.getForm(integratedRigId, projectContext.getProjectDetails());
    return getIntegratedRigModelAndView(projectId, form);
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
          var rig = integratedRigService.createIntegratedRig(projectContext.getProjectDetails(), form);
          AuditService.audit(
              AuditEvent.INTEGRATED_RIG_UPDATED,
              String.format(
                  AuditEvent.INTEGRATED_RIG_UPDATED.getMessage(),
                  rig.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );
          return getIntegratedRigSummaryRedirect(projectId);
        }
    );
  }

  @PostMapping("/integrated-rig/{integratedRigId}/edit")
  public ModelAndView updateIntegratedRig(@PathVariable("projectId") Integer projectId,
                                          @PathVariable("integratedRigId") Integer integratedRigId,
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
          integratedRigService.updateIntegratedRig(
              integratedRigId,
              projectContext.getProjectDetails(),
              form
          );
          return getIntegratedRigSummaryRedirect(projectId);
        }
    );
  }

  @GetMapping("/integrated-rig/{integratedRigId}/remove/{displayOrder}")
  public ModelAndView removeIntegratedRigsConfirmation(@PathVariable("projectId") Integer projectId,
                                                       @PathVariable("integratedRigId") Integer integratedRigId,
                                                       @PathVariable("displayOrder") Integer displayOrder,
                                                       ProjectContext projectContext) {
    var integratedRigView = integratedRigSummaryService.getIntegratedRigSummaryView(
        integratedRigId,
        projectContext.getProjectDetails(),
        displayOrder
    );

    return removeIntegratedRigModelAndView(projectId, integratedRigView);
  }

  @PostMapping("/integrated-rig/{integratedRigId}/remove/{displayOrder}")
  public ModelAndView removeIntegratedRig(@PathVariable("projectId") Integer projectId,
                                          @PathVariable("integratedRigId") Integer integratedRigId,
                                          @PathVariable("displayOrder") Integer displayOrder,
                                          ProjectContext projectContext) {
    var integratedRigView = integratedRigService.getIntegratedRig(
        integratedRigId,
        projectContext.getProjectDetails()
    );

    integratedRigService.deleteIntegratedRig(integratedRigView);

    return getIntegratedRigSummaryRedirect(projectId);
  }

  private ModelAndView getIntegratedRigsSummaryModelAndView(Integer projectId,
                                                            List<IntegratedRigView> integratedRigViews,
                                                            ValidationResult validationResult) {
    var modelAndView = new ModelAndView("project/integratedrig/integratedRigFormSummary")
        .addObject("pageTitle", SUMMARY_PAGE_NAME)
        .addObject("addIntegratedRigUrl",
            ReverseRouter.route(on(IntegratedRigController.class).addIntegratedRig(projectId, null))
        )
        .addObject("backToTaskListUrl",
            ControllerUtils.getBackToTaskListUrl(projectId)
        )
        .addObject("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectId))
        .addObject("integratedRigViews", integratedRigViews)
        .addObject("errorList",
            validationResult.equals(ValidationResult.INVALID)
                ? integratedRigSummaryService.getIntegratedRigViewErrors(integratedRigViews)
                : null
        );

    breadcrumbService.fromTaskList(projectId, modelAndView, TASK_LIST_NAME);

    return modelAndView;
  }

  private ModelAndView getIntegratedRigModelAndView(Integer projectId, IntegratedRigForm form) {
    var modelAndView = new ModelAndView("project/integratedrig/integratedRigForm")
        .addObject("form", form)
        .addObject("pageTitle", FORM_PAGE_NAME)
        .addObject("facilitiesRestUrl", integratedRigService.getFacilityRestUrl())
        .addObject("preSelectedFacilityMap", integratedRigService.getPreSelectedFacility(form))
        .addObject("integratedRigStatuses", IntegratedRigStatus.getAllAsMap())
        .addObject("integratedRigIntentionsToReactivate", IntegratedRigIntentionToReactivate.getAllAsMap());

    breadcrumbService.fromIntegratedRig(projectId, modelAndView, FORM_PAGE_NAME);

    return modelAndView;
  }

  private ModelAndView getIntegratedRigSummaryRedirect(Integer projectId) {
    return ReverseRouter.redirect(on(IntegratedRigController.class).viewIntegratedRigs(projectId, null));
  }

  private String getIntegratedRigSummaryUrl(Integer projectId) {
    return ReverseRouter.route(on(IntegratedRigController.class).viewIntegratedRigs(projectId, null));
  }

  private ModelAndView removeIntegratedRigModelAndView(Integer projectId, IntegratedRigView integratedRigView) {
    var modelAndView = new ModelAndView("project/integratedrig/removeIntegratedRig")
        .addObject("integratedRigView", integratedRigView)
        .addObject("cancelUrl", getIntegratedRigSummaryUrl(projectId))
        .addObject("pageName", REMOVE_PAGE_NAME);

    breadcrumbService.fromIntegratedRig(projectId, modelAndView, REMOVE_PAGE_NAME);

    return modelAndView;
  }
}
