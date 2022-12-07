package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan.ForwardWorkPlanCollaborationOpportunityController;
import uk.co.ogauthority.pathfinder.controller.rest.ForwardWorkPlanCollaborationOpportunityRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationCompletionForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetupForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationErrorOrderingService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
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

  private static final String SETUP_TEMPLATE_PATH =
      "project/collaborationopportunities/forwardworkplan/setupForwardWorkPlanCollaborations";

  protected static final String ERROR_FIELD_NAME = "collaboration-opportunity-%d";
  protected static final String EMPTY_LIST_ERROR = "You must add at least one collaboration opportunity";
  protected static final String ERROR_MESSAGE = "Collaboration opportunity %d is incomplete";

  private final ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;

  private final BreadcrumbService breadcrumbService;

  private final ForwardWorkPlanCollaborationOpportunitiesSummaryService forwardWorkPlanCollaborationOpportunitiesSummaryService;

  private final ValidationErrorOrderingService validationErrorOrderingService;

  @Autowired
  public ForwardWorkPlanCollaborationOpportunityModelService(
      ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService,
      BreadcrumbService breadcrumbService,
      ForwardWorkPlanCollaborationOpportunitiesSummaryService forwardWorkPlanCollaborationOpportunitiesSummaryService,
      ValidationErrorOrderingService validationErrorOrderingService
  ) {
    this.forwardWorkPlanCollaborationOpportunityService = forwardWorkPlanCollaborationOpportunityService;
    this.breadcrumbService = breadcrumbService;
    this.forwardWorkPlanCollaborationOpportunitiesSummaryService = forwardWorkPlanCollaborationOpportunitiesSummaryService;
    this.validationErrorOrderingService = validationErrorOrderingService;
  }

  public ModelAndView getViewCollaborationOpportunitiesModelAndView(
      ProjectDetail projectDetail,
      ValidationResult validationResult,
      List<ForwardWorkPlanCollaborationOpportunityView> collaborationViews,
      ForwardWorkPlanCollaborationCompletionForm form,
      BindingResult completeFormBindingResult
  ) {

    final var projectId = projectDetail.getProject().getId();
    final var errorItems = getSummaryViewErrors(
        collaborationViews,
        validationResult,
        form,
        completeFormBindingResult
    );

    final var modelAndView = new ModelAndView(FORM_SUMMARY_TEMPLATE_PATH)
        .addObject("pageHeading", PAGE_NAME)
        .addObject("collaborationOpportunityViews", collaborationViews)
        .addObject("errorSummary", errorItems)
        .addObject("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId))
        .addObject("form", form);

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(modelAndView, projectDetail);

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

  public ModelAndView getRemoveCollaborationOpportunityConfirmationModelAndView(
      int projectId,
      ForwardWorkPlanCollaborationOpportunity collaborationOpportunity,
      int displayOrder) {

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

  public ModelAndView getCollaborationSetupModelAndView(ProjectDetail projectDetail,
                                                        ForwardWorkPlanCollaborationSetupForm form) {

    final var projectId = projectDetail.getProject().getId();

    final var modelAndView = new ModelAndView(SETUP_TEMPLATE_PATH)
        .addObject("pageName", PAGE_NAME)
        .addObject("form", form)
        .addObject("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId));

    breadcrumbService.fromTaskList(
        projectId,
        modelAndView,
        PAGE_NAME
    );

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(modelAndView, projectDetail);

    return modelAndView;
  }

  protected List<ErrorItem> getErrors(List<ForwardWorkPlanCollaborationOpportunityView> views) {
    return SummaryUtil.getErrors(new ArrayList<>(views), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }

  protected List<ErrorItem> getSummaryViewErrors(List<ForwardWorkPlanCollaborationOpportunityView> collaborationOpportunityViews,
                                                 ValidationResult validationResult,
                                                 ForwardWorkPlanCollaborationCompletionForm form,
                                                 BindingResult bindingResult) {

    final var errorList = validationResult.equals(ValidationResult.INVALID)
        ? getErrors(collaborationOpportunityViews)
        : new ArrayList<ErrorItem>();

    final var formErrors = validationErrorOrderingService.getErrorItemsFromBindingResult(
        form,
        bindingResult,
        errorList.size() + 1 // offset form errors from view errors so error summary ordering is correct
    );

    errorList.addAll(formErrors);

    return errorList;
  }
}
