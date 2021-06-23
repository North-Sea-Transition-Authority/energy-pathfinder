package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.WorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.controller.rest.WorkPlanUpcomingTenderRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.WorkPlanUpcomingTenderContractBand;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderCompletionForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderSetupForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderSummaryService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationErrorOrderingService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.WorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class WorkPlanUpcomingTenderModelServiceTest {

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private ValidationErrorOrderingService validationErrorOrderingService;

  private WorkPlanUpcomingTenderModelService workPlanUpcomingTenderModelService;

  private ProjectDetail projectDetail;

  @Before
  public void setup() {

    final var searchSelectorService = new SearchSelectorService();

    workPlanUpcomingTenderModelService = new WorkPlanUpcomingTenderModelService(
        breadcrumbService,
        searchSelectorService,
        validationErrorOrderingService
    );

    projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
  }

  @Test
  public void getUpcomingTenderFormModelAndView_assertCorrectModelProperties() {

    var projectId = projectDetail.getProject().getId();
    var form = new WorkPlanUpcomingTenderForm();

    var modelAndView = workPlanUpcomingTenderModelService.getUpcomingTenderFormModelAndView(
        projectDetail,
        form
    );

    assertThat(modelAndView.getViewName()).isEqualTo(WorkPlanUpcomingTenderModelService.FORM_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("pageNameSingular", WorkPlanUpcomingTenderController.PAGE_NAME_SINGULAR),
        entry("form", form),
        entry("preSelectedFunction", workPlanUpcomingTenderModelService.getPreSelectedFunction(form)),
        entry("contractBands", WorkPlanUpcomingTenderContractBand.getAllAsMap()),
        entry("departmentTenderRestUrl",
            SearchSelectorService.route(on(WorkPlanUpcomingTenderRestController.class).searchTenderDepartments(null))
        ),
        entry("contractTermPeriodDays", DurationPeriod.getEntryAsMap(DurationPeriod.DAYS)),
        entry("contractTermPeriodWeeks", DurationPeriod.getEntryAsMap(DurationPeriod.WEEKS)),
        entry("contractTermPeriodMonths", DurationPeriod.getEntryAsMap(DurationPeriod.MONTHS)),
        entry("contractTermPeriodYears", DurationPeriod.getEntryAsMap(DurationPeriod.YEARS)),
        entry("quarters", Quarter.getAllAsMap())
    );

    verify(breadcrumbService, times(1)).fromWorkPlanUpcomingTenders(
        projectId,
        modelAndView,
        WorkPlanUpcomingTenderController.PAGE_NAME_SINGULAR
    );
  }

  @Test
  public void getViewUpcomingTendersModelAndView_assertCorrectModelProperties() {

    var projectId = projectDetail.getProject().getId();

    final var tenderView = WorkPlanUpcomingTenderUtil.getView(1, true);
    final var tenderViewList = List.of(tenderView);

    final var form = new ForwardWorkPlanTenderCompletionForm();

    var modelAndView = workPlanUpcomingTenderModelService.getViewUpcomingTendersModelAndView(
        projectDetail,
        tenderViewList,
        ValidationResult.NOT_VALIDATED,
        form,
        ReverseRouter.emptyBindingResult()
    );

    assertThat(modelAndView.getViewName()).isEqualTo(WorkPlanUpcomingTenderModelService.SUMMARY_TEMPLATE_PATH);

    assertThat(modelAndView.getModel()).containsOnlyKeys(
        "pageName",
        "tenderViews",
        "isValid",
        "errorSummary",
        "backToTaskListUrl",
        "form",
        ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR,
        ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR
    );

    assertThat(modelAndView.getModel()).contains(
        entry("pageName", WorkPlanUpcomingTenderController.PAGE_NAME),
        entry("tenderViews", tenderViewList),
        entry("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId)),
        entry("form", form),
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
        modelAndView,
        WorkPlanUpcomingTenderController.PAGE_NAME
    );
  }

  @Test
  public void getViewUpcomingTendersModelAndView_whenInvalidTenderViews_assertFormErrorsAreOffset() {

    final var invalidTenderViews = List.of(
        WorkPlanUpcomingTenderUtil.getView(1, false),
        WorkPlanUpcomingTenderUtil.getView(2, false)
    );

    final var form = new ForwardWorkPlanTenderCompletionForm();
    final var bindingResult = ReverseRouter.emptyBindingResult();

    workPlanUpcomingTenderModelService.getViewUpcomingTendersModelAndView(
        projectDetail,
        invalidTenderViews,
        ValidationResult.INVALID,
        form,
        bindingResult
    );

    verify(validationErrorOrderingService, times(1)).getErrorItemsFromBindingResult(
        form,
        bindingResult,
        invalidTenderViews.size() + 1
    );
  }

  @Test
  public void getPreSelectedFunction_whenNullDepartmentType_thenEmptyMap() {
    final var form = new WorkPlanUpcomingTenderForm();
    var results = workPlanUpcomingTenderModelService.getPreSelectedFunction(form);
    assertThat(results).isEmpty();
  }

  @Test
  public void getPreSelectedFunction_whenDepartmentTypeFromList_thenListValueReturned() {

    final var preSelectedDepartmentType = Function.DRILLING;

    final var form = new WorkPlanUpcomingTenderForm();
    form.setDepartmentType(preSelectedDepartmentType.name());

    var results = workPlanUpcomingTenderModelService.getPreSelectedFunction(form);

    assertThat(results).containsExactly(
        entry(preSelectedDepartmentType.getSelectionId(), preSelectedDepartmentType.getSelectionText())
    );
  }

  @Test
  public void getPreSelectedFunction_whenDepartmentTypeNotFromList_thenManualEntryValueReturned() {

    final var preSelectedDepartmentTypeValue = "my manual entry";
    final var preSelectedDepartmentTypeWithPrefix = SearchSelectablePrefix.FREE_TEXT_PREFIX + preSelectedDepartmentTypeValue;

    final var form = new WorkPlanUpcomingTenderForm();
    form.setDepartmentType(preSelectedDepartmentTypeWithPrefix);

    var results = workPlanUpcomingTenderModelService.getPreSelectedFunction(form);

    assertThat(results).containsExactly(
        entry(preSelectedDepartmentTypeWithPrefix, preSelectedDepartmentTypeValue)
    );
  }

  @Test
  public void getErrors_whenMixtureOfValidAndInvalidViews_thenRelevantErrorsDetected() {
    var views = List.of(
        WorkPlanUpcomingTenderUtil.getView(1, true),
        WorkPlanUpcomingTenderUtil.getView(2, false),
        WorkPlanUpcomingTenderUtil.getView(3, false)
    );
    var errors = workPlanUpcomingTenderModelService.getErrors(views);
    assertThat(errors).hasSize(2);

    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(2);
    assertThat(errors.get(0).getFieldName()).isEqualTo(String.format(WorkPlanUpcomingTenderModelService.ERROR_FIELD_NAME, 2));
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(String.format(WorkPlanUpcomingTenderModelService.ERROR_MESSAGE, 2));

    assertThat(errors.get(1).getDisplayOrder()).isEqualTo(3);
    assertThat(errors.get(1).getFieldName()).isEqualTo(String.format(WorkPlanUpcomingTenderModelService.ERROR_FIELD_NAME, 3));
    assertThat(errors.get(1).getErrorMessage()).isEqualTo(String.format(WorkPlanUpcomingTenderModelService.ERROR_MESSAGE, 3));
  }

  @Test
  public void getErrors_whenNoTenders_thenEmptyListError() {
    var errors = workPlanUpcomingTenderModelService.getErrors(Collections.emptyList());

    assertThat(errors).hasSize(1);
    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(1);
    assertThat(errors.get(0).getFieldName()).isEqualTo(UpcomingTenderSummaryService.EMPTY_LIST_ERROR);
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(UpcomingTenderSummaryService.EMPTY_LIST_ERROR);
  }

  @Test
  public void getRemoveUpcomingTenderConfirmModelAndView_assertCorrectModelProperties() {
    var projectId = projectDetail.getProject().getId();
    var tenderView = WorkPlanUpcomingTenderUtil.getView(1, true);

    var modelAndView = workPlanUpcomingTenderModelService.getRemoveUpcomingTenderConfirmModelAndView(projectId, tenderView);

    assertThat(modelAndView.getViewName()).isEqualTo(WorkPlanUpcomingTenderModelService.REMOVE_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("view", tenderView),
        entry("cancelUrl", ReverseRouter.route(on(WorkPlanUpcomingTenderController.class)
            .viewUpcomingTenders(projectId, null))
        )
    );

    verify(breadcrumbService, times(1)).fromWorkPlanUpcomingTenders(
        projectId,
        modelAndView,
        WorkPlanUpcomingTenderController.REMOVE_PAGE_NAME
    );
  }

  @Test
  public void getUpcomingTenderSetupModelAndView_assertCorrectModelProperties() {

    final var form = new ForwardWorkPlanTenderSetupForm();

    final var resultingModel = workPlanUpcomingTenderModelService.getUpcomingTenderSetupModelAndView(
        projectDetail,
        form
    );

    assertThat(resultingModel.getViewName()).isEqualTo(WorkPlanUpcomingTenderModelService.SETUP_TEMPLATE_PATH);

    assertThat(resultingModel.getModelMap()).containsExactly(
        entry("pageName", WorkPlanUpcomingTenderController.PAGE_NAME),
        entry("form", form),
        entry("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectDetail.getProject().getId())),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR,
            projectDetail.getProjectType().getDisplayName()
        ),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR,
            projectDetail.getProjectType().getLowercaseDisplayName()
        )
    );
  }
}