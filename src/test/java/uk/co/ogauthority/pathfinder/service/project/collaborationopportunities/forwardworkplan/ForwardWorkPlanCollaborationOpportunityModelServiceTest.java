package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan.ForwardWorkPlanCollaborationOpportunityController;
import uk.co.ogauthority.pathfinder.controller.rest.ForwardWorkPlanCollaborationOpportunityRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationOpportunityModelServiceTest {

  @Mock
  private ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private ForwardWorkPlanCollaborationOpportunitiesSummaryService forwardWorkPlanCollaborationOpportunitiesSummaryService;

  private ForwardWorkPlanCollaborationOpportunityModelService forwardWorkPlanCollaborationOpportunityModelService;

  @Before
  public void setup() {
    forwardWorkPlanCollaborationOpportunityModelService = new ForwardWorkPlanCollaborationOpportunityModelService(
        forwardWorkPlanCollaborationOpportunityService,
        breadcrumbService,
        forwardWorkPlanCollaborationOpportunitiesSummaryService
    );
  }

  @Test
  public void getViewCollaborationOpportunitiesModelAndView_withNotValidatedValidationResult_assertModelProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunityViews = List.of(new ForwardWorkPlanCollaborationOpportunityView());
    final var validationResult = ValidationResult.NOT_VALIDATED;

    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.getSummaryViews(projectDetail)).thenReturn(collaborationOpportunityViews);

    final var modelAndView = forwardWorkPlanCollaborationOpportunityModelService.getViewCollaborationOpportunitiesModelAndView(
        projectDetail,
        validationResult
    );

    getViewCollaborationOpportunitiesModelAndView_assertCommonProperties(
        projectDetail,
        collaborationOpportunityViews,
        modelAndView
    );

    assertThat(modelAndView.getModelMap()).contains(
        entry("isValid", false),
        entry("errorSummary", null)
    );

    verify(forwardWorkPlanCollaborationOpportunitiesSummaryService, never()).getErrors(collaborationOpportunityViews);
  }

  @Test
  public void getViewCollaborationOpportunitiesModelAndView_withValidValidationResult_assertModelProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunityViews = List.of(new ForwardWorkPlanCollaborationOpportunityView());
    final var validationResult = ValidationResult.VALID;

    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.getSummaryViews(projectDetail)).thenReturn(collaborationOpportunityViews);

    final var modelAndView = forwardWorkPlanCollaborationOpportunityModelService.getViewCollaborationOpportunitiesModelAndView(
        projectDetail,
        validationResult
    );

    getViewCollaborationOpportunitiesModelAndView_assertCommonProperties(
        projectDetail,
        collaborationOpportunityViews,
        modelAndView
    );

    assertThat(modelAndView.getModelMap()).contains(
        entry("isValid", true),
        entry("errorSummary", null)
    );

    verify(forwardWorkPlanCollaborationOpportunitiesSummaryService, never()).getErrors(collaborationOpportunityViews);
  }

  @Test
  public void getViewCollaborationOpportunitiesModelAndView_withInvalidValidationResult_assertModelProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunityViews = List.of(new ForwardWorkPlanCollaborationOpportunityView());
    final var validationResult = ValidationResult.INVALID;

    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.getSummaryViews(projectDetail)).thenReturn(collaborationOpportunityViews);

    final var modelAndView = forwardWorkPlanCollaborationOpportunityModelService.getViewCollaborationOpportunitiesModelAndView(
        projectDetail,
        validationResult
    );

    getViewCollaborationOpportunitiesModelAndView_assertCommonProperties(
        projectDetail,
        collaborationOpportunityViews,
        modelAndView
    );

    assertThat(modelAndView.getModelMap()).contains(
        entry("isValid", false),
        entry("errorSummary", Collections.emptyList())
    );

    verify(forwardWorkPlanCollaborationOpportunitiesSummaryService, times(1)).getErrors(collaborationOpportunityViews);
  }

  private void getViewCollaborationOpportunitiesModelAndView_assertCommonProperties(
      ProjectDetail projectDetail,
      List<ForwardWorkPlanCollaborationOpportunityView> collaborationOpportunityViews,
      ModelAndView modelAndView
  ) {

    final var projectId = projectDetail.getProject().getId();

    assertThat(modelAndView.getModelMap()).containsOnlyKeys(
        "pageHeading",
        "addCollaborationOpportunityFormUrl",
        "collaborationOpportunityViews",
        "isValid",
        "errorSummary",
        "backToTaskListUrl"
    );

    assertThat(modelAndView.getModelMap()).contains(
        entry("pageHeading", ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME),
        entry(
            "addCollaborationOpportunityFormUrl",
            ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class).addCollaborationOpportunity(
                projectId,
                null
            ))
        ),
        entry("collaborationOpportunityViews", collaborationOpportunityViews),
        entry("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId))
    );

    verify(breadcrumbService).fromTaskList(projectId, modelAndView, ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME);
  }

  @Test
  public void getCollaborationOpportunityModelAndView_assertModelProperties() {

    final var form = new ForwardWorkPlanCollaborationOpportunityForm();
    final var preselectedFunction = Function.DRILLING;
    final var preselectedFunctionMap = Map.of(preselectedFunction.getSelectionId(), preselectedFunction.getSelectionText());
    final var projectId = 1;

    when(forwardWorkPlanCollaborationOpportunityService.getPreSelectedCollaborationFunction(any())).thenReturn(
        preselectedFunctionMap
    );

    final var modelAndView = forwardWorkPlanCollaborationOpportunityModelService.getCollaborationOpportunityModelAndView(
        new ModelAndView(),
        form,
        projectId
    );

    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("pageHeading", ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME_SINGULAR),
        entry(
            "collaborationFunctionRestUrl",
            SearchSelectorService.route(on(ForwardWorkPlanCollaborationOpportunityRestController.class).searchFunctions(null))
        ),
        entry("form", form),
        entry("preselectedFunction", preselectedFunctionMap)
    );

    verify(breadcrumbService, times(1)).fromWorkPlanCollaborations(
        projectId,
        modelAndView,
        ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME_SINGULAR
    );
  }

  @Test
  public void getSaveCollaborationOpportunitySummaryModelAndView_whenInvalidViews_assertModelProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunityViews = List.of(new ForwardWorkPlanCollaborationOpportunityView());

    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.getValidatedSummaryViews(projectDetail)).thenReturn(collaborationOpportunityViews);
    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.validateViews(collaborationOpportunityViews)).thenReturn(ValidationResult.INVALID);

    final var modelAndView = forwardWorkPlanCollaborationOpportunityModelService.getSaveCollaborationOpportunitySummaryModelAndView(
        projectDetail
    );

    getViewCollaborationOpportunitiesModelAndView_assertCommonProperties(
        projectDetail,
        collaborationOpportunityViews,
        modelAndView
    );

    assertThat(modelAndView.getModelMap()).contains(
        entry("isValid", false),
        entry("errorSummary", Collections.emptyList())
    );

    verify(forwardWorkPlanCollaborationOpportunitiesSummaryService, times(1)).getErrors(collaborationOpportunityViews);

  }

  @Test
  public void getSaveCollaborationOpportunitySummaryModelAndView_whenValidViews_assertTaskListModelAndView() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.validateViews(any())).thenReturn(ValidationResult.VALID);

    final var modelAndView = forwardWorkPlanCollaborationOpportunityModelService.getSaveCollaborationOpportunitySummaryModelAndView(
        projectDetail
    );

    final var expectedModelAndViewName = ReverseRouter.redirect(on(TaskListController.class).viewTaskList(
        projectDetail.getProject().getId(),
        null
    )).getViewName();

    assertThat(modelAndView.getViewName()).isEqualTo(expectedModelAndViewName);
  }

  @Test
  public void getRemoveCollaborationOpportunityConfirmationModelAndView_assertModelProperties() {

    final var projectId = 100;
    final var opportunityId = 10;
    final var displayOrder = 1;
    final var opportunity = new ForwardWorkPlanCollaborationOpportunity();
    final var opportunityView = new ForwardWorkPlanCollaborationOpportunityView();

    when(forwardWorkPlanCollaborationOpportunityService.getOrError(opportunityId)).thenReturn(opportunity);
    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.getView(
        opportunity,
        displayOrder
    )).thenReturn(opportunityView);

    final var modelAndView = forwardWorkPlanCollaborationOpportunityModelService.getRemoveCollaborationOpportunityConfirmationModelAndView(
        projectId,
        opportunityId,
        displayOrder
    );

    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("view", opportunityView),
        entry(
            "cancelUrl",
            ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class).viewCollaborationOpportunities(
                projectId,
                null
            ))
        )
    );

    verify(breadcrumbService, times(1)).fromWorkPlanCollaborations(
        projectId,
        modelAndView,
        ForwardWorkPlanCollaborationOpportunityModelService.REMOVE_PAGE_NAME
    );
  }

}