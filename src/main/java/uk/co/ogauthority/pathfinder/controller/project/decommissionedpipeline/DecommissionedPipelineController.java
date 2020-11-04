package uk.co.ogauthority.pathfinder.controller.project.decommissionedpipeline;

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
import uk.co.ogauthority.pathfinder.model.enums.project.InfrastructureStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedpipeline.PipelineRemovalPremise;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineForm;
import uk.co.ogauthority.pathfinder.model.view.decommissionedpipeline.DecommissionedPipelineView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline.DecommissionedPipelineService;
import uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline.DecommissionedPipelineSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/pipelines")
public class DecommissionedPipelineController extends ProjectFormPageController {

  private static final String SUMMARY_PAGE_NAME = "Pipelines to be decommissioned";
  public static final String TASK_LIST_NAME = "Pipelines";
  private static final String FORM_PAGE_NAME = "Pipeline";
  public static final String REMOVE_PAGE_NAME = "Remove pipeline";

  private final DecommissionedPipelineService decommissionedPipelineService;
  private final DecommissionedPipelineSummaryService decommissionedPipelineSummaryService;

  @Autowired
  public DecommissionedPipelineController(BreadcrumbService breadcrumbService,
                                          ControllerHelperService controllerHelperService,
                                          DecommissionedPipelineService decommissionedPipelineService,
                                          DecommissionedPipelineSummaryService decommissionedPipelineSummaryService) {
    super(breadcrumbService, controllerHelperService);
    this.decommissionedPipelineService = decommissionedPipelineService;
    this.decommissionedPipelineSummaryService = decommissionedPipelineSummaryService;
  }

  @GetMapping
  public ModelAndView getPipelines(@PathVariable Integer projectId,
                                   ProjectContext projectContext) {
    var decommissionedPipelineViews = decommissionedPipelineSummaryService.getDecommissionedPipelineSummaryViews(
        projectContext.getProjectDetails()
    );
    return getDecommissionedPipelinesSummaryModelAndView(projectId, decommissionedPipelineViews, ValidationResult.NOT_VALIDATED);
  }

  @PostMapping
  public ModelAndView savePipelines(@PathVariable("projectId") Integer projectId,
                                    ProjectContext projectContext) {
    var decommissionedPipelineViews = decommissionedPipelineSummaryService
        .getValidatedDecommissionedPipelineSummaryViews(
            projectContext.getProjectDetails()
        );

    var validationResult = decommissionedPipelineSummaryService.validateViews(decommissionedPipelineViews);

    return validationResult.equals(ValidationResult.VALID)
        ? ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null))
        : getDecommissionedPipelinesSummaryModelAndView(projectId, decommissionedPipelineViews, validationResult);
  }

  @GetMapping("/pipeline")
  public ModelAndView addPipeline(@PathVariable("projectId") Integer projectId,
                                  ProjectContext projectContext) {
    return getDecommissionedPipelineModelAndView(projectId, new DecommissionedPipelineForm());
  }

  @GetMapping("/pipeline/{decommissionedPipelineId}")
  public ModelAndView getPipeline(@PathVariable("projectId") Integer projectId,
                                  @PathVariable("decommissionedPipelineId") Integer decommissionedPipelineId,
                                  ProjectContext projectContext) {
    var form = decommissionedPipelineService.getForm(decommissionedPipelineId, projectContext.getProjectDetails());
    return getDecommissionedPipelineModelAndView(projectId, form);
  }

  @PostMapping("/pipeline")
  public ModelAndView createPipeline(@PathVariable("projectId") Integer projectId,
                                     @Valid @ModelAttribute("form") DecommissionedPipelineForm form,
                                     BindingResult bindingResult,
                                     ValidationType validationType,
                                     ProjectContext projectContext) {
    bindingResult = decommissionedPipelineService.validate(form, bindingResult, validationType);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getDecommissionedPipelineModelAndView(projectId, form),
        form,
        () -> {
          decommissionedPipelineService.createDecommissionedPipeline(projectContext.getProjectDetails(), form);
          return getDecommissionedPipelineSummaryRedirect(projectId);
        }
    );
  }

  @PostMapping("/pipeline/{decommissionedPipelineId}")
  public ModelAndView updatePipeline(@PathVariable("projectId") Integer projectId,
                                     @PathVariable("decommissionedPipelineId") Integer decommissionedPipelineId,
                                     @Valid @ModelAttribute("form") DecommissionedPipelineForm form,
                                     BindingResult bindingResult,
                                     ValidationType validationType,
                                     ProjectContext projectContext) {
    bindingResult = decommissionedPipelineService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getDecommissionedPipelineModelAndView(projectId, form),
        form,
        () -> {
          decommissionedPipelineService.updateDecommissionedPipeline(
              decommissionedPipelineId,
              projectContext.getProjectDetails(),
              form
          );
          return getDecommissionedPipelineSummaryRedirect(projectId);
        }
    );
  }

  @GetMapping("/pipeline/{decommissionedPipelineId}/remove/{displayOrder}")
  public ModelAndView removePipelineConfirmation(@PathVariable("projectId") Integer projectId,
                                                 @PathVariable("decommissionedPipelineId") Integer decommissionedPipelineId,
                                                 @PathVariable("displayOrder") Integer displayOrder,
                                                 ProjectContext projectContext) {
    var decommissionedPipelineView = decommissionedPipelineSummaryService.getDecommissionedPipelineSummaryView(
        decommissionedPipelineId,
        projectContext.getProjectDetails(),
        displayOrder
    );

    return removeDecommissionedPipelineModelAndView(projectId, decommissionedPipelineView);
  }

  @PostMapping("/pipeline/{decommissionedPipelineId}/remove/{displayOrder}")
  public ModelAndView removePipeline(@PathVariable("projectId") Integer projectId,
                                     @PathVariable("decommissionedPipelineId") Integer decommissionedPipelineId,
                                     @PathVariable("displayOrder") Integer displayOrder,
                                     ProjectContext projectContext) {
    var decommissionedPipeline = decommissionedPipelineService.getDecommissionedPipelineOrError(
        decommissionedPipelineId,
        projectContext.getProjectDetails()
    );

    decommissionedPipelineService.deleteDecommissionedPipeline(decommissionedPipeline);

    return getDecommissionedPipelineSummaryRedirect(projectId);
  }

  private ModelAndView getDecommissionedPipelinesSummaryModelAndView(Integer projectId,
                                                                     List<DecommissionedPipelineView> decommissionedPipelineViews,
                                                                     ValidationResult validationResult) {
    var modelAndView = new ModelAndView("project/decommissionedpipeline/decommissionedPipelineFormSummary")
        .addObject("pageTitle", SUMMARY_PAGE_NAME)
        .addObject("addDecommissionedPipelineUrl",
            ReverseRouter.route(on(DecommissionedPipelineController.class).addPipeline(projectId, null))
        )
        .addObject("backToTaskListUrl",
            ControllerUtils.getBackToTaskListUrl(projectId)
        )
        .addObject("decommissionedPipelineViews", decommissionedPipelineViews)
        .addObject("errorList",
            validationResult.equals(ValidationResult.INVALID)
                ? decommissionedPipelineSummaryService.getDecommissionedPipelineViewErrors(decommissionedPipelineViews)
                : null
        );

    breadcrumbService.fromTaskList(projectId, modelAndView, TASK_LIST_NAME);

    return modelAndView;
  }

  private ModelAndView getDecommissionedPipelineModelAndView(Integer projectId, DecommissionedPipelineForm form) {
    var modelAndView = new ModelAndView("project/decommissionedpipeline/decommissionedPipeline")
        .addObject("form", form)
        .addObject("pageTitle", FORM_PAGE_NAME)
        .addObject("pipelineRestUrl", decommissionedPipelineService.getPipelineRestUrl())
        .addObject("preSelectedPipelineMap", decommissionedPipelineService.getPreSelectedPipeline(form))
        .addObject("pipelineStatuses", InfrastructureStatus.getAllAsMap())
        .addObject("pipelineRemovalPremises", PipelineRemovalPremise.getAllAsMap());

    breadcrumbService.fromDecommissionedPipelines(projectId, modelAndView, FORM_PAGE_NAME);

    return modelAndView;
  }

  private ModelAndView getDecommissionedPipelineSummaryRedirect(Integer projectId) {
    return ReverseRouter.redirect(on(DecommissionedPipelineController.class).getPipelines(projectId, null));
  }

  private String getDecommissionedPipelineSummaryUrl(Integer projectId) {
    return ReverseRouter.route(on(DecommissionedPipelineController.class).getPipelines(projectId, null));
  }

  private ModelAndView removeDecommissionedPipelineModelAndView(Integer projectId, DecommissionedPipelineView decommissionedPipelineView) {
    var modelAndView = new ModelAndView("project/decommissionedpipeline/removeDecommissionedPipeline")
        .addObject("decommissionedPipelineView", decommissionedPipelineView)
        .addObject("cancelUrl", getDecommissionedPipelineSummaryUrl(projectId))
        .addObject("pageName", REMOVE_PAGE_NAME);

    breadcrumbService.fromDecommissionedPipelines(projectId, modelAndView, REMOVE_PAGE_NAME);

    return modelAndView;
  }
}
