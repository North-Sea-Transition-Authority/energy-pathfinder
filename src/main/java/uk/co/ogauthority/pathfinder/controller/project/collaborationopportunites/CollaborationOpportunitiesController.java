package uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites;

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
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.rest.CollaborationOpportunityRestController;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/collaboration-opportunities")
public class CollaborationOpportunitiesController {

  public static final String PAGE_NAME = "Collaboration opportunities";
  public static final String PAGE_NAME_SINGULAR = "Collaboration opportunity";

  private final BreadcrumbService breadcrumbService;
  private final ControllerHelperService controllerHelperService;
  private final CollaborationOpportunitiesService collaborationOpportunitiesService;


  @Autowired
  public CollaborationOpportunitiesController(BreadcrumbService breadcrumbService,
                                              ControllerHelperService controllerHelperService,
                                              CollaborationOpportunitiesService collaborationOpportunitiesService) {
    this.breadcrumbService = breadcrumbService;
    this.controllerHelperService = controllerHelperService;
    this.collaborationOpportunitiesService = collaborationOpportunitiesService;
  }

  @GetMapping
  public ModelAndView viewCollaborationOpportunities(@PathVariable("projectId") Integer projectId,
                                                     ProjectContext projectContext) {
    return getViewCollaborationOpportunitiesModelAndView(projectId, projectContext);
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


  private ModelAndView getViewCollaborationOpportunitiesModelAndView(Integer projectId, ProjectContext projectContext) {
    var modelAndView = new ModelAndView("project/collaborationopportunities/collaborationOpportunitiesSummary")
        .addObject(
            "addCollaborationOpportunityUrl",
            ReverseRouter.route(on(CollaborationOpportunitiesController.class).addCollaborationOpportunity(projectId, null))
        );
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
