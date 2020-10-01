package uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites;

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
import uk.co.ogauthority.pathfinder.controller.rest.CollaborationOpportunityRestController;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/collaboration-opportunities")
public class CollaborationOpportunitiesController extends ProjectFormPageController {

  public static final String PAGE_NAME = "Collaboration opportunities";
  public static final String PAGE_NAME_SINGULAR = "Collaboration opportunity";
  public static final String REMOVE_PAGE_NAME = "Remove collaboration opportunity";

  private final CollaborationOpportunitiesService collaborationOpportunitiesService;
  private final CollaborationOpportunitiesSummaryService collaborationOpportunitiesSummaryService;


  @Autowired
  public CollaborationOpportunitiesController(BreadcrumbService breadcrumbService,
                                              ControllerHelperService controllerHelperService,
                                              CollaborationOpportunitiesService collaborationOpportunitiesService,
                                              CollaborationOpportunitiesSummaryService collaborationOpportunitiesSummaryService) {
    super(breadcrumbService, controllerHelperService);
    this.collaborationOpportunitiesService = collaborationOpportunitiesService;
    this.collaborationOpportunitiesSummaryService = collaborationOpportunitiesSummaryService;
  }

  @GetMapping
  public ModelAndView viewCollaborationOpportunities(@PathVariable("projectId") Integer projectId,
                                                     ProjectContext projectContext) {
    return getViewCollaborationOpportunitiesModelAndView(
        projectId,
        collaborationOpportunitiesSummaryService.getSummaryViews(projectContext.getProjectDetails()),
        ValidationResult.NOT_VALIDATED,
        projectContext
      );
  }

  @PostMapping
  public ModelAndView saveCollaborationOpportunities(@PathVariable("projectId") Integer projectId,
                                                     ProjectContext projectContext) {
    var views = collaborationOpportunitiesSummaryService.getValidatedSummaryViews(
        projectContext.getProjectDetails()
    );

    var validationResult = collaborationOpportunitiesSummaryService.validateViews(views);

    if (validationResult.equals(ValidationResult.INVALID)) {
      return getViewCollaborationOpportunitiesModelAndView(
          projectId,
          views,
          validationResult,
          projectContext
      );
    }

    return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
  }


  @GetMapping("/collaboration-opportunity")
  public ModelAndView addCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                  ProjectContext projectContext) {
    return getCollaborationOpportunityModelAndView(projectId, new CollaborationOpportunityForm());
  }


  @PostMapping("/collaboration-opportunity")
  public ModelAndView saveCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                   @Valid @ModelAttribute("form") CollaborationOpportunityForm form,
                                                   BindingResult bindingResult,
                                                   ValidationType validationType,
                                                   ProjectContext projectContext) {
    bindingResult = collaborationOpportunitiesService.validate(form, bindingResult, validationType);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getCollaborationOpportunityModelAndView(projectId, form),
        form,
        () -> {
          collaborationOpportunitiesService.createCollaborationOpportunity(projectContext.getProjectDetails(), form);
          return ReverseRouter.redirect(on(CollaborationOpportunitiesController.class).viewCollaborationOpportunities(projectId, null));
        }
    );
  }

  @GetMapping("/collaboration-opportunity/{opportunityId}/edit")
  public ModelAndView editCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                   @PathVariable("opportunityId") Integer opportunityId,
                                                   ProjectContext projectContext) {
    var opportunity = collaborationOpportunitiesService.getOrError(opportunityId);
    return getCollaborationOpportunityModelAndView(projectId, collaborationOpportunitiesService.getForm(opportunity));
  }


  @PostMapping("/collaboration-opportunity/{opportunityId}/edit")
  public ModelAndView updateCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                     @PathVariable("opportunityId") Integer opportunityId,
                                                     @Valid @ModelAttribute("form") CollaborationOpportunityForm form,
                                                     BindingResult bindingResult,
                                                     ValidationType validationType,
                                                     ProjectContext projectContext) {
    var opportunity = collaborationOpportunitiesService.getOrError(opportunityId);
    bindingResult = collaborationOpportunitiesService.validate(form, bindingResult, validationType);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getCollaborationOpportunityModelAndView(projectId, form),
        form,
        () -> {
          collaborationOpportunitiesService.updateCollaborationOpportunity(opportunity, form);
          return ReverseRouter.redirect(on(CollaborationOpportunitiesController.class).viewCollaborationOpportunities(projectId, null));
        }
    );
  }

  @GetMapping("/collaboration-opportunity/{opportunityId}/delete/{displayOrder}")
  public ModelAndView deleteCollaborationOpportunityConfirm(@PathVariable("projectId") Integer projectId,
                                                            @PathVariable("opportunityId") Integer opportunityId,
                                                            @PathVariable("displayOrder") Integer displayOrder,
                                                            ProjectContext projectContext) {
    var opportunity = collaborationOpportunitiesService.getOrError(opportunityId);

    var modelAndView = new ModelAndView("project/collaborationopportunities/removeCollaborationOpportunity")
        .addObject("view", collaborationOpportunitiesSummaryService.getView(opportunity, displayOrder))
        .addObject("cancelUrl", ReverseRouter.route(
              on(CollaborationOpportunitiesController.class).viewCollaborationOpportunities(projectId, null))
        );
    breadcrumbService.fromUpcomingTenders(projectId, modelAndView, REMOVE_PAGE_NAME);
    return modelAndView;
  }

  @PostMapping("/collaboration-opportunity/{opportunityId}/delete/{displayOrder}")
  public ModelAndView deleteUpcomingTender(@PathVariable("projectId") Integer projectId,
                                           @PathVariable("opportunityId") Integer opportunityId,
                                           @PathVariable("displayOrder") Integer displayOrder,
                                           ProjectContext projectContext) {
    var opportunity = collaborationOpportunitiesService.getOrError(opportunityId);
    collaborationOpportunitiesService.delete(opportunity);
    return ReverseRouter.redirect(on(CollaborationOpportunitiesController.class).viewCollaborationOpportunities(projectId, null));
  }


  private ModelAndView getViewCollaborationOpportunitiesModelAndView(
      Integer projectId,
      List<CollaborationOpportunityView> views,
      ValidationResult validationResult,
      ProjectContext projectContext
  ) {
    var modelAndView = new ModelAndView("project/collaborationopportunities/collaborationOpportunitiesSummary")
        .addObject(
            "addCollaborationOpportunityUrl",
            ReverseRouter.route(on(CollaborationOpportunitiesController.class).addCollaborationOpportunity(projectId, null))
        )
        .addObject("opportunityViews", views)
        .addObject("isValid", validationResult.equals(ValidationResult.VALID))
        .addObject("errorSummary",
          validationResult.equals(ValidationResult.INVALID)
            ? collaborationOpportunitiesSummaryService.getErrors(views)
            : null
        )
        .addObject("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId));
    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }

  private ModelAndView getCollaborationOpportunityModelAndView(Integer projectId, CollaborationOpportunityForm form) {
    var modelAndView = new ModelAndView("project/collaborationopportunities/collaborationOpportunity")
        .addObject(
            "collaborationFunctionRestUrl",
            SearchSelectorService.route(on(CollaborationOpportunityRestController.class).searchFunctions(null))
        )
        .addObject("form", form)
        .addObject("preselectedCollaboration", collaborationOpportunitiesService.getPreSelectedCollaborationFunction(form));
    breadcrumbService.fromCollaborationOpportunities(projectId, modelAndView, PAGE_NAME_SINGULAR);
    return modelAndView;
  }
}
