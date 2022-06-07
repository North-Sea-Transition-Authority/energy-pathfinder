package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan.ForwardWorkPlanCollaborationOpportunityController;
import uk.co.ogauthority.pathfinder.controller.rest.ForwardWorkPlanCollaborationOpportunityRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationCompletionForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetupForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityViewUtil;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationErrorOrderingService;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanCollaborationOpportunityTestUtil;
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

  @Mock
  private ValidationErrorOrderingService validationErrorOrderingService;

  private ForwardWorkPlanCollaborationOpportunityModelService forwardWorkPlanCollaborationOpportunityModelService;

  @Before
  public void setup() {
    forwardWorkPlanCollaborationOpportunityModelService = new ForwardWorkPlanCollaborationOpportunityModelService(
        forwardWorkPlanCollaborationOpportunityService,
        breadcrumbService,
        forwardWorkPlanCollaborationOpportunitiesSummaryService,
        validationErrorOrderingService
    );
  }

  @Test
  public void getViewCollaborationOpportunitiesModelAndView_whenErrors_assertModelProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var projectId = projectDetail.getProject().getId();

    final var collaborationOpportunityViews = List.of(new ForwardWorkPlanCollaborationOpportunityView());

    final var validationResult = ValidationResult.NOT_VALIDATED;
    final var collaborationCompletionForm = new ForwardWorkPlanCollaborationCompletionForm();

    final var resultingModel = forwardWorkPlanCollaborationOpportunityModelService.getViewCollaborationOpportunitiesModelAndView(
        projectDetail,
        validationResult,
        collaborationOpportunityViews,
        collaborationCompletionForm,
        ReverseRouter.emptyBindingResult()
    );

    assertThat(resultingModel.getModelMap()).containsExactly(
        entry("pageHeading", ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME),
        entry("collaborationOpportunityViews", collaborationOpportunityViews),
        entry("errorSummary", Collections.emptyList()),
        entry("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId)),
        entry("form", collaborationCompletionForm),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR,
            projectDetail.getProjectType().getDisplayName()
        ),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR,
            projectDetail.getProjectType().getLowercaseDisplayName()
        )
    );

    verify(breadcrumbService, times(1)).fromTaskList(
        projectId,
        resultingModel,
        ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME
    );

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
  public void getRemoveCollaborationOpportunityConfirmationModelAndView_assertModelProperties() {

    final var projectId = 100;
    final var opportunityId = 10;
    final var displayOrder = 1;
    final var opportunity = new ForwardWorkPlanCollaborationOpportunity();
    final var opportunityView = new ForwardWorkPlanCollaborationOpportunityView();

    when(forwardWorkPlanCollaborationOpportunitiesSummaryService.getView(
        opportunity,
        displayOrder
    )).thenReturn(opportunityView);

    final var modelAndView = forwardWorkPlanCollaborationOpportunityModelService.getRemoveCollaborationOpportunityConfirmationModelAndView(
        projectId,
        opportunity,
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

  @Test
  public void getCollaborationSetupModelAndView_assertModelProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var projectId = projectDetail.getProject().getId();

    final var collaborationSetupForm = new ForwardWorkPlanCollaborationSetupForm();

    final var resultingModel = forwardWorkPlanCollaborationOpportunityModelService.getCollaborationSetupModelAndView(
        projectDetail,
        collaborationSetupForm
    );

    assertThat(resultingModel.getModel()).containsExactly(
        entry("pageName", ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME),
        entry("form", collaborationSetupForm),
        entry("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId)),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR,
            projectDetail.getProjectType().getDisplayName()
        ),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR,
            projectDetail.getProjectType().getLowercaseDisplayName()
        )
    );

    verify(breadcrumbService, times(1)).fromTaskList(
        projectId,
        resultingModel,
        ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME
    );
  }

  @Test
  public void getErrors_whenNoErrors_thenEmptyList() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        projectDetail
    );
    final var isValid = true;

    ForwardWorkPlanCollaborationOpportunityView collaborationOpportunityView = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        collaborationOpportunity,
        1,
        Collections.emptyList()
    )
        .includeSummaryLinks(true)
        .isValid(isValid)
        .build();

    final var errorList = forwardWorkPlanCollaborationOpportunityModelService.getErrors(List.of(collaborationOpportunityView));
    assertThat(errorList).isEmpty();
  }

  @Test
  public void getErrors_whenErrors_thenAssertExpectedMessages() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        projectDetail
    );

    ForwardWorkPlanCollaborationOpportunityView validCollaborationOpportunityView = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        collaborationOpportunity,
        1,
        Collections.emptyList()
    )
        .includeSummaryLinks(true)
        .isValid(true)
        .build();

    ForwardWorkPlanCollaborationOpportunityView invalidCollaborationOpportunityView = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        collaborationOpportunity,
        2,
        Collections.emptyList()
    )
        .includeSummaryLinks(true)
        .isValid(false)
        .build();

    final var errorList = forwardWorkPlanCollaborationOpportunityModelService.getErrors(
        List.of(validCollaborationOpportunityView, invalidCollaborationOpportunityView)
    );

    assertThat(errorList).hasSize(1);

    final var invalidViewDisplayOrder = invalidCollaborationOpportunityView.getDisplayOrder();
    assertThat(errorList.get(0).getDisplayOrder()).isEqualTo(invalidViewDisplayOrder);
    assertThat(errorList.get(0).getFieldName()).isEqualTo(String.format(
        ForwardWorkPlanCollaborationOpportunityModelService.ERROR_FIELD_NAME,
        invalidViewDisplayOrder
    ));
    assertThat(errorList.get(0).getErrorMessage()).isEqualTo(String.format(
        ForwardWorkPlanCollaborationOpportunityModelService.ERROR_MESSAGE,
        invalidViewDisplayOrder
    ));

  }

  @Test
  public void getErrors_whenMixtureOfValidAndInvalidViews_thenRelevantErrorsDetected() {
    var views = List.of(
        ForwardWorkPlanCollaborationOpportunityTestUtil.getView(1, true),
        ForwardWorkPlanCollaborationOpportunityTestUtil.getView(2, false),
        ForwardWorkPlanCollaborationOpportunityTestUtil.getView(3, false)
    );
    var errors = forwardWorkPlanCollaborationOpportunityModelService.getErrors(views);
    assertThat(errors).hasSize(2);

    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(2);
    assertThat(errors.get(0).getFieldName()).isEqualTo(String.format(ForwardWorkPlanCollaborationOpportunityModelService.ERROR_FIELD_NAME, 2));
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(String.format(ForwardWorkPlanCollaborationOpportunityModelService.ERROR_MESSAGE, 2));

    assertThat(errors.get(1).getDisplayOrder()).isEqualTo(3);
    assertThat(errors.get(1).getFieldName()).isEqualTo(String.format(ForwardWorkPlanCollaborationOpportunityModelService.ERROR_FIELD_NAME, 3));
    assertThat(errors.get(1).getErrorMessage()).isEqualTo(String.format(ForwardWorkPlanCollaborationOpportunityModelService.ERROR_MESSAGE, 3));
  }

  @Test
  public void getErrors_whenNoCollaborations_thenEmptyListError() {
    var errors = forwardWorkPlanCollaborationOpportunityModelService.getErrors(Collections.emptyList());

    assertThat(errors).hasSize(1);
    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(1);
    assertThat(errors.get(0).getFieldName()).isEqualTo(ForwardWorkPlanCollaborationOpportunityModelService.EMPTY_LIST_ERROR);
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(ForwardWorkPlanCollaborationOpportunityModelService.EMPTY_LIST_ERROR);
  }

  @Test
  public void getSummaryViewErrors_whenOnlyCollaborationViewErrors() {

    final var views = List.of(
        ForwardWorkPlanCollaborationOpportunityTestUtil.getView(1, false)
    );

    final var invalidValidationResult = ValidationResult.INVALID;

    final var resultingErrors = forwardWorkPlanCollaborationOpportunityModelService.getSummaryViewErrors(
        views,
        invalidValidationResult,
        null,
        null
    );

    assertThat(resultingErrors).extracting(ErrorItem::getErrorMessage).containsExactly(
        String.format(ForwardWorkPlanCollaborationOpportunityModelService.ERROR_MESSAGE, views.get(0).getDisplayOrder())
    );
  }

  @Test
  public void getSummaryViewErrors_whenOnlyCompletionFormErrors() {

    final var errorItem = new ErrorItem(1, "fieldName", "error message");
    final var expectedErrorItems = List.of(errorItem);

    when(validationErrorOrderingService.getErrorItemsFromBindingResult(any(), any(), anyInt())).thenReturn(expectedErrorItems);

    final var resultingErrors = forwardWorkPlanCollaborationOpportunityModelService.getSummaryViewErrors(
        Collections.emptyList(),
        ValidationResult.NOT_VALIDATED,
        null,
        null
    );

    assertThat(resultingErrors).isEqualTo(expectedErrorItems);

  }

  @Test
  public void getSummaryViewErrors_whenCollaborationAndCompletionFormErrors() {

    final var formErrorItem = new ErrorItem(1, "fieldName", "error message");
    final var expectedFormErrorItems = List.of(formErrorItem);

    when(validationErrorOrderingService.getErrorItemsFromBindingResult(any(), any(), anyInt())).thenReturn(expectedFormErrorItems);

    final var views = List.of(
        ForwardWorkPlanCollaborationOpportunityTestUtil.getView(1, false)
    );

    final var invalidValidationResult = ValidationResult.INVALID;

    final var resultingErrors = forwardWorkPlanCollaborationOpportunityModelService.getSummaryViewErrors(
        views,
        invalidValidationResult,
        null,
        null
    );

    assertThat(resultingErrors).extracting(ErrorItem::getErrorMessage).containsExactly(
        String.format(ForwardWorkPlanCollaborationOpportunityModelService.ERROR_MESSAGE, views.get(0).getDisplayOrder()),
        formErrorItem.getErrorMessage()
    );
  }

}