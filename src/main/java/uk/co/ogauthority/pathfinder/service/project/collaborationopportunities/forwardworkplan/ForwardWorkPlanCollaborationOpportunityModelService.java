package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan.ForwardWorkPlanCollaborationOpportunityController;
import uk.co.ogauthority.pathfinder.controller.rest.ForwardWorkPlanCollaborationOpportunityRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class ForwardWorkPlanCollaborationOpportunityModelService {

  public static final String PAGE_NAME = "Collaboration opportunities";
  public static final String PAGE_NAME_SINGULAR = "Collaboration opportunity";
  public static final String REMOVE_PAGE_NAME = "Remove collaboration opportunity";

  public static final String FORM_TEMPLATE_PATH =
      "project/collaborationopportunities/forwardworkplan/forwardWorkPlanCollaborationOpportunityForm";

  private static final String FORM_SUMMARY_TEMPLATE_PATH =
      "project/collaborationopportunities/forwardworkplan/forwardWorkPlanCollaborationOpportunitiesFormSummary";

  private static final String REMOVE_CONFIRM_TEMPLATE_PATH =
      "project/collaborationopportunities/forwardworkplan/removeForwardWorkPlanCollaborationOpportunity";

  private final ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;

  private final BreadcrumbService breadcrumbService;

  private final ForwardWorkPlanCollaborationOpportunitiesSummaryService forwardWorkPlanCollaborationOpportunitiesSummaryService;

  @Autowired
  public ForwardWorkPlanCollaborationOpportunityModelService(
      ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService,
      BreadcrumbService breadcrumbService,
      ForwardWorkPlanCollaborationOpportunitiesSummaryService forwardWorkPlanCollaborationOpportunitiesSummaryService
  ) {
    this.forwardWorkPlanCollaborationOpportunityService = forwardWorkPlanCollaborationOpportunityService;
    this.breadcrumbService = breadcrumbService;
    this.forwardWorkPlanCollaborationOpportunitiesSummaryService = forwardWorkPlanCollaborationOpportunitiesSummaryService;
  }

  public ModelAndView getViewCollaborationOpportunitiesModelAndView(ProjectDetail projectDetail,
                                                                    ValidationResult validationResult) {
    final var collaborationViews = forwardWorkPlanCollaborationOpportunitiesSummaryService.getSummaryViews(
        projectDetail
    );
    return getViewCollaborationOpportunitiesModelAndView(projectDetail, validationResult, collaborationViews);
  }

  private ModelAndView getViewCollaborationOpportunitiesModelAndView(
      ProjectDetail projectDetail,
      ValidationResult validationResult,
      List<ForwardWorkPlanCollaborationOpportunityView> collaborationViews
  ) {

    final var projectId = projectDetail.getProject().getId();
    final var errorItems = validationResult.equals(ValidationResult.INVALID)
        ? forwardWorkPlanCollaborationOpportunitiesSummaryService.getErrors(collaborationViews)
        : null;

    final var modelAndView = new ModelAndView(FORM_SUMMARY_TEMPLATE_PATH)
        .addObject("pageHeading", PAGE_NAME)
        .addObject(
            "addCollaborationOpportunityFormUrl",
            ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class).addCollaborationOpportunity(
                projectId,
                null
            ))
        )
        .addObject("collaborationOpportunityViews", collaborationViews)
        .addObject("isValid", validationResult.equals(ValidationResult.VALID))
        .addObject("errorSummary", errorItems)
        .addObject("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId));

    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);

    return modelAndView;
  }

  public ModelAndView getSaveCollaborationOpportunitySummaryModelAndView(ProjectDetail projectDetail) {

    final var summaryViews = forwardWorkPlanCollaborationOpportunitiesSummaryService.getValidatedSummaryViews(
        projectDetail
    );
    final var validationResult = forwardWorkPlanCollaborationOpportunitiesSummaryService.validateViews(
        summaryViews
    );

    if (validationResult.equals(ValidationResult.INVALID)) {
      return getViewCollaborationOpportunitiesModelAndView(
          projectDetail,
          validationResult,
          summaryViews
      );
    }

    return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectDetail.getProject().getId(), null));
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

  public ModelAndView getRemoveCollaborationOpportunityConfirmationModelAndView(int projectId,
                                                                                int opportunityId,
                                                                                int displayOrder) {

    final var collaborationOpportunity = forwardWorkPlanCollaborationOpportunityService.getOrError(opportunityId);
    final var collaborationOpportunityView = forwardWorkPlanCollaborationOpportunitiesSummaryService.getView(
        collaborationOpportunity,
        displayOrder
    );

    final var modelAndView = new ModelAndView(REMOVE_CONFIRM_TEMPLATE_PATH)
        .addObject("view", collaborationOpportunityView)
        .addObject(
            "cancelUrl",
            ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class).viewCollaborationOpportunities(
                projectId,
                null
            ))
        );

    breadcrumbService.fromWorkPlanCollaborations(projectId, modelAndView, REMOVE_PAGE_NAME);

    return modelAndView;
  }
}
