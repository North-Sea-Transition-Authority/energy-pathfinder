package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan.ForwardWorkPlanCollaborationOpportunityController;
import uk.co.ogauthority.pathfinder.controller.rest.ForwardWorkPlanCollaborationOpportunityRestController;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Service
public class ForwardWorkPlanCollaborationOpportunityModelService {

  public static final String PAGE_NAME = "Collaboration opportunities";
  public static final String PAGE_NAME_SINGULAR = "Collaboration opportunity";

  public static final String FORM_TEMPLATE_PATH =
      "project/collaborationopportunities/forwardworkplan/forwardWorkPlanCollaborationOpportunityForm";

  private static final String FORM_SUMMARY_TEMPLATE_PATH =
      "project/collaborationopportunities/forwardworkplan/forwardWorkPlanCollaborationOpportunitiesFormSummary";

  private final ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;

  private final BreadcrumbService breadcrumbService;

  @Autowired
  public ForwardWorkPlanCollaborationOpportunityModelService(
      ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService,
      BreadcrumbService breadcrumbService
  ) {
    this.forwardWorkPlanCollaborationOpportunityService = forwardWorkPlanCollaborationOpportunityService;
    this.breadcrumbService = breadcrumbService;
  }

  public ModelAndView getViewCollaborationOpportunitiesModelAndView(int projectId) {
    final var modelAndView = new ModelAndView(FORM_SUMMARY_TEMPLATE_PATH)
        .addObject("pageHeading", PAGE_NAME)
        .addObject(
            "addCollaborationOpportunityFormUrl",
            ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class).addCollaborationOpportunity(
                projectId,
                null
            ))
        );

    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);

    return modelAndView;
  }

  public ModelAndView getCollaborationOpportunityModelAndView(ModelAndView fileUploadModelAndView,
                                                              ForwardWorkPlanCollaborationOpportunityForm form,
                                                              int projectId) {
    final var modelAndView = fileUploadModelAndView
        .addObject("pageHeading", PAGE_NAME_SINGULAR)
        .addObject(
            "collaborationFunctionRestUrl",
            SearchSelectorService.route(on(ForwardWorkPlanCollaborationOpportunityRestController.class).searchFunctions(null))
        )
        .addObject("form", form)
        .addObject(
            "preselectedFunction",
            forwardWorkPlanCollaborationOpportunityService.getPreSelectedCollaborationFunction(form)
        );

    breadcrumbService.fromWorkPlanCollaborations(projectId, modelAndView, PAGE_NAME_SINGULAR);

    return modelAndView;
  }
}
