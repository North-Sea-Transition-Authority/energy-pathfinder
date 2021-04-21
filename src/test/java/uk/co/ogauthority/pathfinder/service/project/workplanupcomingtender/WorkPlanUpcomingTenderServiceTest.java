package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Map.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.WorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.controller.rest.WorkPlanUpcomingTenderRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.WorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.WorkPlanUpcomingTenderContractBand;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender.WorkPlanUpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.WorkPlanUpcomingTenderUtil;

@RunWith(MockitoJUnitRunner.class)
public class WorkPlanUpcomingTenderServiceTest {

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

    var modelAndView = workPlanUpcomingTenderService.getUpcomingTendersModelAndView(projectId);

    assertThat(modelAndView.getViewName()).isEqualTo(WorkPlanUpcomingTenderService.TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("pageName", WorkPlanUpcomingTenderController.PAGE_NAME),
        entry("addUpcomingTenderUrl", ReverseRouter.route(on(WorkPlanUpcomingTenderController.class).addUpcomingTender(projectId, null))
        )
    );

    verify(breadcrumbService, times(1)).fromTaskList(projectId, modelAndView, WorkPlanUpcomingTenderController.PAGE_NAME);
  }

  @Test
  public void getViewUpcomingTendersModelAndView() {
    var projectId = projectDetail.getProject().getId();
    var form = new WorkPlanUpcomingTenderForm();

    var modelAndView = workPlanUpcomingTenderService.getViewUpcomingTendersModelAndView(
        projectDetail,
        form
    );

    assertThat(modelAndView.getViewName()).isEqualTo("project/workplanupcomingtender/workPlanUpcomingTender");
    assertThat(modelAndView.getModel()).containsExactly(
        entry("pageNameSingular", WorkPlanUpcomingTenderController.PAGE_NAME_SINGULAR),
        entry("form", form),
        entry("preSelectedFunction", workPlanUpcomingTenderService.getPreSelectedFunction(form)),
        entry("contractBands", WorkPlanUpcomingTenderContractBand.getAllAsMap()),
        entry("departmentTenderRestUrl", SearchSelectorService.route(on(WorkPlanUpcomingTenderRestController.class).searchTenderDepartments(null))
        ));

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

  private void checkCommonFields(WorkPlanUpcomingTenderForm form, WorkPlanUpcomingTender newUpcomingTender) {
    assertThat(newUpcomingTender.getDescriptionOfWork()).isEqualTo(WorkPlanUpcomingTenderUtil.DESCRIPTION_OF_WORK);
    assertThat(newUpcomingTender.getEstimatedTenderDate()).isEqualTo(form.getEstimatedTenderDate().createDateOrNull());
    assertThat(newUpcomingTender.getContractBand()).isEqualTo(WorkPlanUpcomingTenderUtil.CONTRACT_BAND);
    assertThat(newUpcomingTender.getContactName()).isEqualTo(WorkPlanUpcomingTenderUtil.CONTACT_NAME);
    assertThat(newUpcomingTender.getPhoneNumber()).isEqualTo(WorkPlanUpcomingTenderUtil.PHONE_NUMBER);
    assertThat(newUpcomingTender.getJobTitle()).isEqualTo(WorkPlanUpcomingTenderUtil.JOB_TITLE);
    assertThat(newUpcomingTender.getEmailAddress()).isEqualTo(WorkPlanUpcomingTenderUtil.EMAIL);
  }

  @Test
  public void findDepartmentTenderLikeWithManualEntry() {
    var results = workPlanUpcomingTenderService.findDepartmentTenderLikeWithManualEntry(Function.DRILLING.getDisplayName());
    assertThat(results)
        .extracting(RestSearchItem::getId)
        .containsExactly(Function.DRILLING.name());
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