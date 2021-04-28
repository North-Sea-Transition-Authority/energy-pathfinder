package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.WorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.controller.rest.WorkPlanUpcomingTenderRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.WorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.WorkPlanUpcomingTenderContractBand;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.WorkPlanUpcomingTenderView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender.WorkPlanUpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.WorkPlanUpcomingTenderUtil;

@RunWith(MockitoJUnitRunner.class)
public class WorkPlanUpcomingTenderServiceTest {

  private static final Integer DISPLAY_ORDER = 1;

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private ValidationService validationService;

  @Mock
  private WorkPlanUpcomingTenderFormValidator workPlanUpcomingTenderFormValidator;

  @Mock
  private WorkPlanUpcomingTenderRepository workPlanUpcomingTenderRepository;

  private WorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  private final WorkPlanUpcomingTender upcomingTender = WorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

  private final WorkPlanUpcomingTenderView tenderView = WorkPlanUpcomingTenderUtil.getView(DISPLAY_ORDER, true);

  @Before
  public void setup() {
    SearchSelectorService searchSelectorService = new SearchSelectorService();
    FunctionService functionService = new FunctionService(searchSelectorService);

    workPlanUpcomingTenderService = new WorkPlanUpcomingTenderService(
        breadcrumbService,
        functionService,
        validationService,
        workPlanUpcomingTenderFormValidator,
        workPlanUpcomingTenderRepository,
        searchSelectorService
    );

    when(workPlanUpcomingTenderRepository.save(any(WorkPlanUpcomingTender.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void getUpcomingTendersModelAndView() {
    var projectId = projectDetail.getProject().getId();
    var list = List.of(tenderView);

    var modelAndView = workPlanUpcomingTenderService.getUpcomingTendersModelAndView(projectId, list);

    assertThat(modelAndView.getViewName()).isEqualTo(WorkPlanUpcomingTenderService.TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("pageName", WorkPlanUpcomingTenderController.PAGE_NAME),
        entry("tenderViews", list),
        entry("addUpcomingTenderUrl", ReverseRouter.route(on(WorkPlanUpcomingTenderController.class).addUpcomingTender(projectId, null))
        )
    );

    verify(breadcrumbService, times(1)).fromTaskList(projectId, modelAndView, WorkPlanUpcomingTenderController.PAGE_NAME);
  }

  @Test
  public void getViewUpcomingTendersModelAndView() {
    var projectId = projectDetail.getProject().getId();
    var form = new WorkPlanUpcomingTenderForm();

    var modelAndView = workPlanUpcomingTenderService.getUpcomingTenderFormModelAndView(
        projectDetail,
        form
    );

    assertThat(modelAndView.getViewName()).isEqualTo("project/workplanupcomingtender/workPlanUpcomingTender");
    assertThat(modelAndView.getModel()).containsExactly(
        entry("pageNameSingular", WorkPlanUpcomingTenderController.PAGE_NAME_SINGULAR),
        entry("form", form),
        entry("preSelectedFunction", workPlanUpcomingTenderService.getPreSelectedFunction(form)),
        entry("contractBands", WorkPlanUpcomingTenderContractBand.getAllAsMap()),
        entry("departmentTenderRestUrl", SearchSelectorService.route(on(WorkPlanUpcomingTenderRestController.class).searchTenderDepartments(null))),
        entry("contractTermPeriodDays", DurationPeriod.getEntryAsMap(DurationPeriod.DAYS)),
        entry("contractTermPeriodWeeks", DurationPeriod.getEntryAsMap(DurationPeriod.WEEKS)),
        entry("contractTermPeriodMonths", DurationPeriod.getEntryAsMap(DurationPeriod.MONTHS)),
        entry("contractTermPeriodYears", DurationPeriod.getEntryAsMap(DurationPeriod.YEARS))
    );

    verify(breadcrumbService, times(1)).fromWorkPlanUpcomingTenders(projectId, modelAndView, WorkPlanUpcomingTenderController.PAGE_NAME_SINGULAR);
  }

  @Test
  public void createUpcomingTender() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    var newUpcomingTenders = workPlanUpcomingTenderService.createUpcomingTender(
        projectDetail,
        form
    );
    assertThat(newUpcomingTenders.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(newUpcomingTenders.getDepartmentType()).isEqualTo(WorkPlanUpcomingTenderUtil.UPCOMING_TENDER_DEPARTMENT);
    assertThat(newUpcomingTenders.getManualDepartmentType()).isNull();
    checkCommonFields(form, newUpcomingTenders);
  }

  @Test
  public void createUpcomingTender_manualDepartment() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm_manualEntry();
    var newUpcomingTender = workPlanUpcomingTenderService.createUpcomingTender(
        detail,
        form
    );
    assertThat(newUpcomingTender.getProjectDetail()).isEqualTo(detail);
    assertThat(newUpcomingTender.getManualDepartmentType()).isEqualTo(SearchSelectorService.removePrefix(WorkPlanUpcomingTenderUtil.MANUAL_TENDER_DEPARTMENT));
    assertThat(newUpcomingTender.getDepartmentType()).isNull();
    checkCommonFields(form, newUpcomingTender);
  }

  @Test
  public void createUpcomingTender_whenNoContractTermPeriod_thenNoContractTermColumnsPopulatedInEntity() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setContractTermDurationPeriod(null);

    var upcomingTender = workPlanUpcomingTenderService.createUpcomingTender(
        detail,
        form
    );

    assertThat(upcomingTender.getContractTermDurationPeriod()).isNull();
    assertThat(upcomingTender.getContractTermDuration()).isNull();

    checkCommonFields(form, upcomingTender);
  }

  @Test
  public void createUpcomingTender_whenNoContractTermPeriodIsDays_thenNoContractTermColumnsPopulatedInEntity() {

    final var expectedContractTermDuration = 10;
    final var expectedContractTermDurationPeriod = DurationPeriod.DAYS;

    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setContractTermDurationPeriod(expectedContractTermDurationPeriod);
    form.setContractTermDayDuration(expectedContractTermDuration);
    form.setContractTermWeekDuration(11);
    form.setContractTermMonthDuration(12);
    form.setContractTermYearDuration(13);

    var upcomingTender = workPlanUpcomingTenderService.createUpcomingTender(
        detail,
        form
    );

    assertExpectedContractTermDurationAndPeriod(
        upcomingTender,
        form,
        expectedContractTermDurationPeriod,
        expectedContractTermDuration
    );
  }

  @Test
  public void createUpcomingTender_whenNoContractTermPeriodIsWeeks_thenNoContractTermColumnsPopulatedInEntity() {

    final var expectedContractTermDuration = 11;
    final var expectedContractTermDurationPeriod = DurationPeriod.WEEKS;

    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setContractTermDurationPeriod(expectedContractTermDurationPeriod);
    form.setContractTermDayDuration(10);
    form.setContractTermWeekDuration(expectedContractTermDuration);
    form.setContractTermMonthDuration(12);
    form.setContractTermYearDuration(13);

    var upcomingTender = workPlanUpcomingTenderService.createUpcomingTender(
        detail,
        form
    );

    assertExpectedContractTermDurationAndPeriod(
        upcomingTender,
        form,
        expectedContractTermDurationPeriod,
        expectedContractTermDuration
    );
  }

  @Test
  public void createUpcomingTender_whenNoContractTermPeriodIsMonths_thenNoContractTermColumnsPopulatedInEntity() {

    final var expectedContractTermDuration = 12;
    final var expectedContractTermDurationPeriod = DurationPeriod.MONTHS;

    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setContractTermDurationPeriod(expectedContractTermDurationPeriod);
    form.setContractTermDayDuration(10);
    form.setContractTermWeekDuration(11);
    form.setContractTermMonthDuration(expectedContractTermDuration);
    form.setContractTermYearDuration(13);

    var upcomingTender = workPlanUpcomingTenderService.createUpcomingTender(
        detail,
        form
    );

    assertExpectedContractTermDurationAndPeriod(
        upcomingTender,
        form,
        expectedContractTermDurationPeriod,
        expectedContractTermDuration
    );
  }

  @Test
  public void createUpcomingTender_whenNoContractTermPeriodIsYears_thenNoContractTermColumnsPopulatedInEntity() {

    final var expectedContractTermDuration = 13;
    final var expectedContractTermDurationPeriod = DurationPeriod.YEARS;

    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setContractTermDurationPeriod(expectedContractTermDurationPeriod);
    form.setContractTermDayDuration(10);
    form.setContractTermWeekDuration(11);
    form.setContractTermMonthDuration(12);
    form.setContractTermYearDuration(expectedContractTermDuration);

    var upcomingTender = workPlanUpcomingTenderService.createUpcomingTender(
        detail,
        form
    );

    assertExpectedContractTermDurationAndPeriod(
        upcomingTender,
        form,
        expectedContractTermDurationPeriod,
        expectedContractTermDuration
    );
  }

  private void assertExpectedContractTermDurationAndPeriod(WorkPlanUpcomingTender upcomingTender,
                                                           WorkPlanUpcomingTenderForm form,
                                                           DurationPeriod expectedContractTermDurationPeriod,
                                                           Integer expectedContractTermDuration) {
    assertThat(upcomingTender.getContractTermDurationPeriod()).isEqualTo(expectedContractTermDurationPeriod);
    assertThat(upcomingTender.getContractTermDuration()).isEqualTo(expectedContractTermDuration);
    checkCommonFields(form, upcomingTender);
  }

  private void checkCommonFields(WorkPlanUpcomingTenderForm form, WorkPlanUpcomingTender newUpcomingTender) {
    assertThat(newUpcomingTender.getDescriptionOfWork()).isEqualTo(WorkPlanUpcomingTenderUtil.DESCRIPTION_OF_WORK);
    assertThat(newUpcomingTender.getEstimatedTenderDate()).isEqualTo(form.getEstimatedTenderDate().createDateOrNull());
    assertThat(newUpcomingTender.getContractBand()).isEqualTo(WorkPlanUpcomingTenderUtil.CONTRACT_BAND);
    assertThat(newUpcomingTender.getContactName()).isEqualTo(WorkPlanUpcomingTenderUtil.CONTACT_NAME);
    assertThat(newUpcomingTender.getPhoneNumber()).isEqualTo(WorkPlanUpcomingTenderUtil.PHONE_NUMBER);
    assertThat(newUpcomingTender.getJobTitle()).isEqualTo(WorkPlanUpcomingTenderUtil.JOB_TITLE);
    assertThat(newUpcomingTender.getEmailAddress()).isEqualTo(WorkPlanUpcomingTenderUtil.EMAIL);
  }

  private void checkCommonFormFields(WorkPlanUpcomingTenderForm form, WorkPlanUpcomingTender upcomingTender) {
    assertThat(form.getDescriptionOfWork()).isEqualTo(upcomingTender.getDescriptionOfWork());
    assertThat(form.getEstimatedTenderDate().createDateOrNull()).isEqualTo(upcomingTender.getEstimatedTenderDate());
    assertThat(form.getContractBand()).isEqualTo(upcomingTender.getContractBand());
    assertThat(form.getContactDetail().getName()).isEqualTo(upcomingTender.getContactName());
    assertThat(form.getContactDetail().getPhoneNumber()).isEqualTo(upcomingTender.getPhoneNumber());
    assertThat(form.getContactDetail().getJobTitle()).isEqualTo(upcomingTender.getJobTitle());
    assertThat(form.getContactDetail().getEmailAddress()).isEqualTo(upcomingTender.getEmailAddress());
  }

  @Test
  public void getForm_whenFromListDepartmentType_assertCorrectFormValue() {
    var form = workPlanUpcomingTenderService.getForm(upcomingTender);
    assertThat(form.getDepartmentType()).isEqualTo(upcomingTender.getDepartmentType().name());
    checkCommonFormFields(form, upcomingTender);
  }

  @Test
  public void getForm_whenManualEntryDepartmentType_assertCorrectFormValue() {
    var manualDepartment = WorkPlanUpcomingTenderUtil.getUpcomingTender_manualEntry(projectDetail);
    var form = workPlanUpcomingTenderService.getForm(manualDepartment);
    assertThat(form.getDepartmentType()).isEqualTo(SearchSelectorService.getValueWithManualEntryPrefix(manualDepartment.getManualDepartmentType()));
    checkCommonFormFields(form, upcomingTender);
  }

  @Test
  public void getForm_whenDurationPeriodIsNull_thenPeriodAndDurationNullInForm() {

    final var form = getFormWithContractDurationAndPeriod(null, 1);

    assertThat(form.getContractTermDurationPeriod()).isNull();
    assertThat(form.getContractTermDayDuration()).isNull();
    assertThat(form.getContractTermWeekDuration()).isNull();
    assertThat(form.getContractTermMonthDuration()).isNull();
    assertThat(form.getContractTermYearDuration()).isNull();

    checkCommonFields(form, upcomingTender);
  }

  @Test
  public void getForm_whenDurationPeriodIsDaysAndDurationProvided_thenPeriodAndDurationCorrectlyPopulatedInForm() {

    final var durationPeriod = DurationPeriod.DAYS;
    final var duration = 10;

    final var form = getFormWithContractDurationAndPeriod(durationPeriod, duration);

    assertThat(form.getContractTermDurationPeriod()).isEqualTo(durationPeriod);
    assertThat(form.getContractTermDayDuration()).isEqualTo(duration);
    assertThat(form.getContractTermWeekDuration()).isNull();
    assertThat(form.getContractTermMonthDuration()).isNull();
    assertThat(form.getContractTermYearDuration()).isNull();

    checkCommonFields(form, upcomingTender);
  }

  @Test
  public void getForm_whenDurationPeriodIsWeeksAndDurationProvided_thenPeriodAndDurationCorrectlyPopulatedInForm() {

    final var durationPeriod = DurationPeriod.WEEKS;
    final var duration = 10;

    final var form = getFormWithContractDurationAndPeriod(durationPeriod, duration);

    assertThat(form.getContractTermDurationPeriod()).isEqualTo(durationPeriod);
    assertThat(form.getContractTermWeekDuration()).isEqualTo(duration);
    assertThat(form.getContractTermDayDuration()).isNull();
    assertThat(form.getContractTermMonthDuration()).isNull();
    assertThat(form.getContractTermYearDuration()).isNull();

    checkCommonFields(form, upcomingTender);
  }

  @Test
  public void getForm_whenDurationPeriodIsMonthsAndDurationProvided_thenPeriodAndDurationCorrectlyPopulatedInForm() {

    final var durationPeriod = DurationPeriod.MONTHS;
    final var duration = 10;

    final var form = getFormWithContractDurationAndPeriod(durationPeriod, duration);

    assertThat(form.getContractTermDurationPeriod()).isEqualTo(durationPeriod);
    assertThat(form.getContractTermMonthDuration()).isEqualTo(duration);
    assertThat(form.getContractTermDayDuration()).isNull();
    assertThat(form.getContractTermWeekDuration()).isNull();
    assertThat(form.getContractTermYearDuration()).isNull();

    checkCommonFields(form, upcomingTender);
  }

  @Test
  public void getForm_whenDurationPeriodIsYearsAndDurationProvided_thenPeriodAndDurationCorrectlyPopulatedInForm() {

    final var durationPeriod = DurationPeriod.YEARS;
    final var duration = 10;

    final var form = getFormWithContractDurationAndPeriod(durationPeriod, duration);

    assertThat(form.getContractTermDurationPeriod()).isEqualTo(durationPeriod);
    assertThat(form.getContractTermYearDuration()).isEqualTo(duration);
    assertThat(form.getContractTermDayDuration()).isNull();
    assertThat(form.getContractTermWeekDuration()).isNull();
    assertThat(form.getContractTermMonthDuration()).isNull();

    checkCommonFields(form, upcomingTender);
  }

  private WorkPlanUpcomingTenderForm getFormWithContractDurationAndPeriod(DurationPeriod durationPeriod,
                                                                          Integer duration) {
    final var upcomingTender = WorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractTermDurationPeriod(durationPeriod);
    upcomingTender.setContractTermDuration(duration);

    return workPlanUpcomingTenderService.getForm(upcomingTender);
  }

  @Test
  public void validate_partial() {
    var form = new WorkPlanUpcomingTenderForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    workPlanUpcomingTenderService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_full() {
    var form = new WorkPlanUpcomingTenderForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    workPlanUpcomingTenderService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void findDepartmentTenderLikeWithManualEntry() {
    var results = workPlanUpcomingTenderService.findDepartmentTenderLikeWithManualEntry(Function.DRILLING.getDisplayName());
    assertThat(results)
        .extracting(RestSearchItem::getId)
        .containsExactly(Function.DRILLING.name());
  }

  @Test
  public void getUpcomingTendersForDetail_whenNoneExist_thenEmptyList() {
    when(workPlanUpcomingTenderRepository.findByProjectDetailOrderByIdAsc(projectDetail)).thenReturn(List.of());
    var upcomingTenders = workPlanUpcomingTenderService.getUpcomingTendersForDetail(projectDetail);
    assertThat(upcomingTenders).isEmpty();
  }

  @Test
  public void getUpcomingTendersForDetail_whenExist_thenReturnList() {
    var upcomingTenderManualEntry = WorkPlanUpcomingTenderUtil.getUpcomingTender_manualEntry(detail);

    when(workPlanUpcomingTenderRepository.findByProjectDetailOrderByIdAsc(projectDetail)).thenReturn(List.of(upcomingTender, upcomingTenderManualEntry));

    var upcomingTenders = workPlanUpcomingTenderService.getUpcomingTendersForDetail(projectDetail);

    assertThat(upcomingTenders).containsExactly(upcomingTender, upcomingTenderManualEntry);
  }

  @Test
  public void findDepartmentTenderLikeWithManualEntry_withManualEntry() {
    var manualEntry = "manual entry";
    var results = workPlanUpcomingTenderService.findDepartmentTenderLikeWithManualEntry(manualEntry);
    assertThat(results)
        .extracting(RestSearchItem::getId)
        .containsExactly(SearchSelectablePrefix.FREE_TEXT_PREFIX+manualEntry);
  }

  @Test
  public void getPreSelectedFunction_whenNullDepartmentType_thenEmptyMap() {
    final var form = new WorkPlanUpcomingTenderForm();
    var results = workPlanUpcomingTenderService.getPreSelectedFunction(form);
    assertThat(results).isEmpty();
  }

  @Test
  public void getPreSelectedFunction_whenDepartmentTypeFromList_thenListValueReturned() {
    final var preSelectedDepartmentType = Function.DRILLING;
    final var form = new WorkPlanUpcomingTenderForm();
    form.setDepartmentType(preSelectedDepartmentType.name());
    var results = workPlanUpcomingTenderService.getPreSelectedFunction(form);
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
    var results = workPlanUpcomingTenderService.getPreSelectedFunction(form);
    assertThat(results).containsExactly(
        entry(preSelectedDepartmentTypeWithPrefix, preSelectedDepartmentTypeValue)
    );
  }

  @Test
  public void isComplete_whenInvalid_thenFalse() {

    var isComplete = workPlanUpcomingTenderService.isComplete(projectDetail);

    assertThat(isComplete).isFalse();
  }

  @Test
  public void canShowInTaskList_whenNotForwardWorkPlan_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);

    assertThat(workPlanUpcomingTenderService.canShowInTaskList(projectDetail)).isFalse();
  }

  @Test
  public void canShowInTaskList_whenForwardWorkPlan_thenTrue() {
    assertThat(workPlanUpcomingTenderService.canShowInTaskList(projectDetail)).isTrue();
  }
}