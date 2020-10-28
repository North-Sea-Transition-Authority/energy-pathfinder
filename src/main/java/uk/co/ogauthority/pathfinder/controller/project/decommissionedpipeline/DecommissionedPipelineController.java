package uk.co.ogauthority.pathfinder.controller.project.decommissionedpipeline;

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
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.InfrastructureStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissionedpipeline.PipelineRemovalPremise;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline.DecommissionedPipelineService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/pipelines")
public class DecommissionedPipelineController extends ProjectFormPageController {

  public static final String SUMMARY_PAGE_NAME = "Pipelines";
  private static final String FORM_PAGE_NAME = "Pipeline to be decommissioned";

  private final DecommissionedPipelineService decommissionedPipelineService;

  public DecommissionedPipelineController(BreadcrumbService breadcrumbService,
                                          ControllerHelperService controllerHelperService,
                                          DecommissionedPipelineService decommissionedPipelineService) {
    super(breadcrumbService, controllerHelperService);
    this.decommissionedPipelineService = decommissionedPipelineService;
  }

  @GetMapping
  public ModelAndView getPipelines(@PathVariable Integer projectId,
                                   ProjectContext projectContext) {
    return getDecommissionedPipelinesSummaryModelAndView(projectId);
  }

  @GetMapping("/pipeline")
  public ModelAndView addPipeline(@PathVariable("projectId") Integer projectId,
                                  ProjectContext projectContext) {
    return getDecommissionedPipelineModelAndView(projectId, new DecommissionedPipelineForm());
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

  private ModelAndView getDecommissionedPipelinesSummaryModelAndView(Integer projectId) {
    var modelAndView = new ModelAndView("project/decommissionedpipeline/decommissionedPipelinesSummary")
        .addObject("pageTitle", SUMMARY_PAGE_NAME)
        .addObject("addDecommissionedPipelineUrl",
            ReverseRouter.route(on(DecommissionedPipelineController.class).addPipeline(projectId, null))
        )
        .addObject("backToTaskListUrl",
            ControllerUtils.getBackToTaskListUrl(projectId)
        );

    breadcrumbService.fromTaskList(projectId, modelAndView, SUMMARY_PAGE_NAME);

    return modelAndView;
  }

  private ModelAndView getDecommissionedPipelineModelAndView(Integer projectId, DecommissionedPipelineForm form) {
    var modelAndView = new ModelAndView("project/decommissionedpipeline/decommissionedPipeline")
        .addObject("form", form)
        .addObject("pageTitle", FORM_PAGE_NAME)
        .addObject("pipelinesRestUrl", decommissionedPipelineService.getPipelinesRestUrl())
        .addObject("preSelectedPipelineMap", decommissionedPipelineService.getPreSelectedPipeline(form))
        .addObject("pipelineStatuses", InfrastructureStatus.getAllAsMap())
        .addObject("pipelineRemovalPremises", PipelineRemovalPremise.getAllAsMap());

    breadcrumbService.fromDecommissionedPipelines(projectId, modelAndView, FORM_PAGE_NAME);

    return modelAndView;
  }

  private ModelAndView getDecommissionedPipelineSummaryRedirect(Integer projectId) {
    return ReverseRouter.redirect(on(DecommissionedPipelineController.class).getPipelines(projectId, null));
  }
}
