package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectFormSectionServiceTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanUpcomingTenderServiceTest {

  private static final Integer TENDER_ID = 1;

  @Mock
  private ValidationService validationService;

  @Mock
  private ForwardWorkPlanUpcomingTenderFormValidator workPlanUpcomingTenderFormValidator;

  @Mock
  private ForwardWorkPlanUpcomingTenderRepository workPlanUpcomingTenderRepository;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService;

  @Mock
  private TeamService teamService;

  private ForwardWorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  private final ForwardWorkPlanUpcomingTender upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

  private final AuthenticatedUserAccount userAccount = UserTestingUtil.getAuthenticatedUserAccount();

  private final PortalOrganisationGroup portalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "org", "org");

  @Before
  public void setup() {
    SearchSelectorService searchSelectorService = new SearchSelectorService();
    FunctionService functionService = new FunctionService(searchSelectorService);

    workPlanUpcomingTenderService = new ForwardWorkPlanUpcomingTenderService(
        functionService,
        validationService,
        workPlanUpcomingTenderFormValidator,
        workPlanUpcomingTenderRepository,
        searchSelectorService,
        entityDuplicationService,
        forwardWorkPlanTenderSetupService,
        teamService);

    when(workPlanUpcomingTenderRepository.save(any(ForwardWorkPlanUpcomingTender.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createUpcomingTender() {
    when(teamService.getContributorPortalOrganisationGroup(userAccount)).thenReturn(portalOrganisationGroup);
    var form = ForwardWorkPlanUpcomingTenderUtil.getCompleteForm();
    var newUpcomingTenders = workPlanUpcomingTenderService.createUpcomingTender(
        projectDetail,
        form,
        userAccount
    );
    assertThat(newUpcomingTenders.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(newUpcomingTenders.getDepartmentType()).isEqualTo(ForwardWorkPlanUpcomingTenderUtil.UPCOMING_TENDER_DEPARTMENT);
    assertThat(newUpcomingTenders.getManualDepartmentType()).isNull();
    checkCommonFields(form, newUpcomingTenders);
  }

  @Test
  public void createUpcomingTender_manualDepartment() {
    when(teamService.getContributorPortalOrganisationGroup(userAccount)).thenReturn(portalOrganisationGroup);
    var form = ForwardWorkPlanUpcomingTenderUtil.getCompleteForm_manualEntry();
    var newUpcomingTender = workPlanUpcomingTenderService.createUpcomingTender(
        projectDetail,
        form,
        userAccount
    );
    assertThat(newUpcomingTender.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(newUpcomingTender.getManualDepartmentType()).isEqualTo(SearchSelectorService.removePrefix(
        ForwardWorkPlanUpcomingTenderUtil.MANUAL_TENDER_DEPARTMENT));
    assertThat(newUpcomingTender.getDepartmentType()).isNull();
    checkCommonFields(form, newUpcomingTender);
  }

  @Test
  public void createUpcomingTender_whenNoContractTermPeriod_thenNoContractTermColumnsPopulatedInEntity() {
    when(teamService.getContributorPortalOrganisationGroup(userAccount)).thenReturn(portalOrganisationGroup);
    var form = ForwardWorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setContractTermDurationPeriod(null);

    var upcomingTender = workPlanUpcomingTenderService.createUpcomingTender(
        projectDetail,
        form,
        userAccount
    );

    assertThat(upcomingTender.getContractTermDurationPeriod()).isNull();
    assertThat(upcomingTender.getContractTermDuration()).isNull();

    checkCommonFields(form, upcomingTender);
  }

  @Test
  public void createUpcomingTender_whenNoContractTermPeriodIsDays_thenNoContractTermColumnsPopulatedInEntity() {
    when(teamService.getContributorPortalOrganisationGroup(userAccount)).thenReturn(portalOrganisationGroup);

    final var expectedContractTermDuration = 10;
    final var expectedContractTermDurationPeriod = DurationPeriod.DAYS;

    var form = ForwardWorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setContractTermDurationPeriod(expectedContractTermDurationPeriod);
    form.setContractTermDayDuration(expectedContractTermDuration);
    form.setContractTermWeekDuration(11);
    form.setContractTermMonthDuration(12);
    form.setContractTermYearDuration(13);

    var upcomingTender = workPlanUpcomingTenderService.createUpcomingTender(
        projectDetail,
        form,
        userAccount
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
    when(teamService.getContributorPortalOrganisationGroup(userAccount)).thenReturn(portalOrganisationGroup);

    final var expectedContractTermDuration = 11;
    final var expectedContractTermDurationPeriod = DurationPeriod.WEEKS;

    var form = ForwardWorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setContractTermDurationPeriod(expectedContractTermDurationPeriod);
    form.setContractTermDayDuration(10);
    form.setContractTermWeekDuration(expectedContractTermDuration);
    form.setContractTermMonthDuration(12);
    form.setContractTermYearDuration(13);

    var upcomingTender = workPlanUpcomingTenderService.createUpcomingTender(
        projectDetail,
        form,
        userAccount
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
    when(teamService.getContributorPortalOrganisationGroup(userAccount)).thenReturn(portalOrganisationGroup);

    final var expectedContractTermDuration = 12;
    final var expectedContractTermDurationPeriod = DurationPeriod.MONTHS;

    var form = ForwardWorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setContractTermDurationPeriod(expectedContractTermDurationPeriod);
    form.setContractTermDayDuration(10);
    form.setContractTermWeekDuration(11);
    form.setContractTermMonthDuration(expectedContractTermDuration);
    form.setContractTermYearDuration(13);

    var upcomingTender = workPlanUpcomingTenderService.createUpcomingTender(
        projectDetail,
        form,
        userAccount
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
    when(teamService.getContributorPortalOrganisationGroup(userAccount)).thenReturn(portalOrganisationGroup);

    final var expectedContractTermDuration = 13;
    final var expectedContractTermDurationPeriod = DurationPeriod.YEARS;

    var form = ForwardWorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setContractTermDurationPeriod(expectedContractTermDurationPeriod);
    form.setContractTermDayDuration(10);
    form.setContractTermWeekDuration(11);
    form.setContractTermMonthDuration(12);
    form.setContractTermYearDuration(expectedContractTermDuration);

    var upcomingTender = workPlanUpcomingTenderService.createUpcomingTender(
        projectDetail,
        form,
        userAccount
    );

    assertExpectedContractTermDurationAndPeriod(
        upcomingTender,
        form,
        expectedContractTermDurationPeriod,
        expectedContractTermDuration
    );
  }

  @Test
  public void createUpcomingTender_whenEmptyForm() {
    when(teamService.getContributorPortalOrganisationGroup(userAccount)).thenReturn(portalOrganisationGroup);
    var form = ForwardWorkPlanUpcomingTenderUtil.getEmptyForm();

    var upcomingTender = workPlanUpcomingTenderService.createUpcomingTender(
        projectDetail,
        form,
        userAccount
    );
    assertThat(upcomingTender.getProjectDetail()).isEqualTo(projectDetail);
    checkAllFieldsAreNull(upcomingTender);
  }

  private void assertExpectedContractTermDurationAndPeriod(ForwardWorkPlanUpcomingTender upcomingTender,
                                                           ForwardWorkPlanUpcomingTenderForm form,
                                                           DurationPeriod expectedContractTermDurationPeriod,
                                                           Integer expectedContractTermDuration) {
    assertThat(upcomingTender.getContractTermDurationPeriod()).isEqualTo(expectedContractTermDurationPeriod);
    assertThat(upcomingTender.getContractTermDuration()).isEqualTo(expectedContractTermDuration);
    checkCommonFields(form, upcomingTender);
  }

  private void checkCommonFields(ForwardWorkPlanUpcomingTenderForm form, ForwardWorkPlanUpcomingTender upcomingTender) {
    assertThat(form.getDescriptionOfWork()).isEqualTo(upcomingTender.getDescriptionOfWork());
    assertThat(form.getEstimatedTenderStartDate()).isEqualTo(new QuarterYearInput(upcomingTender.getEstimatedTenderDateQuarter(), String.valueOf(upcomingTender.getEstimatedTenderDateYear())));
    assertThat(form.getContractBand()).isEqualTo(upcomingTender.getContractBand());
    assertThat(form.getContactDetail().getName()).isEqualTo(upcomingTender.getContactName());
    assertThat(form.getContactDetail().getPhoneNumber()).isEqualTo(upcomingTender.getPhoneNumber());
    assertThat(form.getContactDetail().getJobTitle()).isEqualTo(upcomingTender.getJobTitle());
    assertThat(form.getContactDetail().getEmailAddress()).isEqualTo(upcomingTender.getEmailAddress());
    assertThat(upcomingTender.getAddedByOrganisationGroup()).isEqualTo(portalOrganisationGroup.getOrgGrpId());
  }

  private void checkAllFieldsAreNull(ForwardWorkPlanUpcomingTender workPlanUpcomingTender) {
    assertThat(workPlanUpcomingTender.getDepartmentType()).isNull();
    assertThat(workPlanUpcomingTender.getManualDepartmentType()).isNull();
    assertThat(workPlanUpcomingTender.getDescriptionOfWork()).isNull();
    assertThat(workPlanUpcomingTender.getEstimatedTenderDateQuarter()).isNull();
    assertThat(workPlanUpcomingTender.getEstimatedTenderDateYear()).isNull();
    assertThat(workPlanUpcomingTender.getContactName()).isNull();
    assertThat(workPlanUpcomingTender.getPhoneNumber()).isNull();
    assertThat(workPlanUpcomingTender.getJobTitle()).isNull();
    assertThat(workPlanUpcomingTender.getEmailAddress()).isNull();
    assertThat(workPlanUpcomingTender.getContractBand()).isNull();
    assertThat(workPlanUpcomingTender.getContractTermDuration()).isNull();
    assertThat(workPlanUpcomingTender.getContractTermDurationPeriod()).isNull();
 }

  @Test
  public void getForm_whenFromListDepartmentType_assertCorrectFormValue() {
    var form = workPlanUpcomingTenderService.getForm(upcomingTender);
    assertThat(form.getDepartmentType()).isEqualTo(upcomingTender.getDepartmentType().name());
    checkCommonFields(form, upcomingTender);
  }

  @Test
  public void getForm_whenManualEntryDepartmentType_assertCorrectFormValue() {
    var manualDepartment = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender_manualEntry(projectDetail);
    var form = workPlanUpcomingTenderService.getForm(manualDepartment);
    assertThat(form.getDepartmentType()).isEqualTo(SearchSelectorService.getValueWithManualEntryPrefix(manualDepartment.getManualDepartmentType()));
    checkCommonFields(form, upcomingTender);
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

  private ForwardWorkPlanUpcomingTenderForm getFormWithContractDurationAndPeriod(DurationPeriod durationPeriod,
                                                                                 Integer duration) {
    final var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractTermDurationPeriod(durationPeriod);
    upcomingTender.setContractTermDuration(duration);

    return workPlanUpcomingTenderService.getForm(upcomingTender);
  }

  @Test
  public void validate_partial() {
    var form = new ForwardWorkPlanUpcomingTenderForm();
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
    var form = new ForwardWorkPlanUpcomingTenderForm();
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
    var upcomingTenderManualEntry = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender_manualEntry(projectDetail);

    when(workPlanUpcomingTenderRepository.findByProjectDetailOrderByIdAsc(projectDetail)).thenReturn(List.of(upcomingTender, upcomingTenderManualEntry));

    var upcomingTenders = workPlanUpcomingTenderService.getUpcomingTendersForDetail(projectDetail);

    assertThat(upcomingTenders).containsExactly(upcomingTender, upcomingTenderManualEntry);
  }

  @Test
  public void getUpcomingTendersForProjectAndVersion_whenNoneExist_thenEmptyList() {
    when(workPlanUpcomingTenderRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        projectDetail.getProject(),
        projectDetail.getVersion()
    )).thenReturn(List.of());
    var upcomingTenders = workPlanUpcomingTenderService.getUpcomingTendersForProjectAndVersion(projectDetail.getProject(), projectDetail.getVersion());
    assertThat(upcomingTenders).isEmpty();
  }

  @Test
  public void getUpcomingTendersForProjectAndVersion_whenExist_thenReturnList() {
    var upcomingTenderManualEntry = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender_manualEntry(projectDetail);
    var upcomingTenders = List.of(upcomingTenderManualEntry, upcomingTender);
    when(workPlanUpcomingTenderRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        projectDetail.getProject(),
        projectDetail.getVersion()
    )).thenReturn(upcomingTenders);

    assertThat(workPlanUpcomingTenderService.getUpcomingTendersForProjectAndVersion(projectDetail.getProject(), projectDetail.getVersion())).isEqualTo(upcomingTenders);
  }

  @Test
  public void findDepartmentTenderLikeWithManualEntry_withManualEntry() {
    var manualEntry = "manual entry";
    var results = workPlanUpcomingTenderService.findDepartmentTenderLikeWithManualEntry(manualEntry);
    assertThat(results)
        .extracting(RestSearchItem::getId)
        .containsExactly(SearchSelectablePrefix.FREE_TEXT_PREFIX+manualEntry);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getOrError_whenNotFound_thenException() {
    when(workPlanUpcomingTenderRepository.findByIdAndProjectDetail(TENDER_ID, projectDetail))
        .thenReturn(Optional.empty());

    workPlanUpcomingTenderService.getOrError(TENDER_ID, projectDetail);
  }

  @Test
  public void getOrError_whenFound_thenReturnTender() {
    final ForwardWorkPlanUpcomingTender tender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    when(workPlanUpcomingTenderRepository.findByIdAndProjectDetail(TENDER_ID, projectDetail))
        .thenReturn(Optional.of(tender));

    var result = workPlanUpcomingTenderService.getOrError(TENDER_ID, projectDetail);
    assertThat(result).isEqualTo(tender);
  }

  @Test
  public void updateUpcomingTender() {
    var form = ForwardWorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setDepartmentType(Function.DRILLING.name());
    var existingUpcomingTender = upcomingTender;
    workPlanUpcomingTenderService.updateUpcomingTender(
        existingUpcomingTender,
        form
    );
    assertThat(existingUpcomingTender.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(existingUpcomingTender.getDepartmentType()).isEqualTo(Function.DRILLING);
    checkCommonFields(form, existingUpcomingTender);
  }

  @Test
  public void updateUpcomingTender_manualFunction() {
    var form = ForwardWorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setDepartmentType(null);
    form.setDepartmentType(UpcomingTenderUtil.MANUAL_TENDER_FUNCTION);
    var existingUpcomingTender = upcomingTender;
    workPlanUpcomingTenderService.updateUpcomingTender(existingUpcomingTender, form);
    assertThat(existingUpcomingTender.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(existingUpcomingTender.getManualDepartmentType()).isEqualTo(SearchSelectorService.removePrefix(UpcomingTenderUtil.MANUAL_TENDER_FUNCTION));
    checkCommonFields(form, existingUpcomingTender);
  }

  @Test
  public void updateUpcomingTender_whenEmptyForm() {
    var form = ForwardWorkPlanUpcomingTenderUtil.getEmptyForm();
    var existingUpcomingTender = upcomingTender;
    workPlanUpcomingTenderService.updateUpcomingTender(existingUpcomingTender, form);
    assertThat(existingUpcomingTender.getProjectDetail()).isEqualTo(projectDetail);
    checkAllFieldsAreNull(existingUpcomingTender);
  }

  @Test
  public void canShowInTaskList_whenNotForwardWorkPlan_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);

    assertThat(workPlanUpcomingTenderService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR)))
        .isFalse();
  }

  @Test
  public void canShowInTaskList_whenForwardWorkPlan_thenTrue() {
    assertThat(workPlanUpcomingTenderService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR)))
        .isTrue();
  }

  @Test
  public void canShowInTaskList_userToProjectRelationshipSmokeTest() {
    ProjectFormSectionServiceTestUtil.canShowInTaskList_userToProjectRelationshipSmokeTest(
        workPlanUpcomingTenderService,
        projectDetail,
        Set.of(UserToProjectRelationship.OPERATOR, UserToProjectRelationship.CONTRIBUTOR)
    );
  }

  @Test
  public void isTaskValidForProjectDetail_whenNotForwardWorkPlan_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);

    assertThat(workPlanUpcomingTenderService.isTaskValidForProjectDetail(projectDetail)).isFalse();
  }

  @Test
  public void isTaskValidForProjectDetail_whenForwardWorkPlan_thenTrue() {
    assertThat(workPlanUpcomingTenderService.isTaskValidForProjectDetail(projectDetail)).isTrue();
  }

  @Test
  public void delete_verifyInteractions() {
    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    workPlanUpcomingTenderService.delete(upcomingTender);
    verify(workPlanUpcomingTenderRepository, times(1)).delete(upcomingTender);
  }

  @Test
  public void copySectionData_verifyInteractions() {

    // ensure the two and from details are different, so we can
    // verify the params are passed in correctly
    final var fromProjectDetail = ProjectUtil.getProjectDetails();
    fromProjectDetail.setVersion(1);

    final var toProjectDetail = ProjectUtil.getProjectDetails();
    toProjectDetail.setVersion(2);

    final var existingWorkPlanTenders = List.of(
        ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(fromProjectDetail)
    );

    when(workPlanUpcomingTenderRepository.findByProjectDetailOrderByIdAsc(fromProjectDetail)).thenReturn(
        existingWorkPlanTenders
    );

    workPlanUpcomingTenderService.copySectionData(
        fromProjectDetail,
        toProjectDetail
    );

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        existingWorkPlanTenders,
        toProjectDetail,
        ForwardWorkPlanUpcomingTender.class
    );

    verify(forwardWorkPlanTenderSetupService, times(1)).copySectionData(
        fromProjectDetail,
        toProjectDetail
    );
  }

  @Test
  public void alwaysCopySectionData_verifyFalse() {
    assertThat(workPlanUpcomingTenderService.alwaysCopySectionData(projectDetail)).isFalse();
  }

  @Test
  public void removeSectionData_verifyInteractions() {

    final var upcomingTender1 = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    final var upcomingTender2 = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

    final var upcomingTendersForDetail = List.of(upcomingTender1, upcomingTender2);

    when(workPlanUpcomingTenderRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(upcomingTendersForDetail);

    workPlanUpcomingTenderService.removeSectionData(projectDetail);

    verify(workPlanUpcomingTenderRepository, times(1)).deleteAll(upcomingTendersForDetail);

    verify(forwardWorkPlanTenderSetupService, times(1)).removeSectionData(projectDetail);
  }

  @Test
  public void getSupportedProjectTypes_verifyForwardWorkPlan() {
    assertThat(workPlanUpcomingTenderService.getSupportedProjectTypes()).containsExactly(ProjectType.FORWARD_WORK_PLAN);
  }
}