package uk.co.ogauthority.pathfinder.service.project.commissionedwell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWell;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.view.commissionedwell.CommissionedWellScheduleView;
import uk.co.ogauthority.pathfinder.model.view.commissionedwell.CommissionedWellScheduleViewUtil;
import uk.co.ogauthority.pathfinder.testutil.CommissionedWellTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.WellboreTestUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@ExtendWith(MockitoExtension.class)
class CommissionedWellScheduleSummaryServiceTest {

  @Mock
  private CommissionedWellScheduleService commissionedWellScheduleService;

  @Mock
  private CommissionedWellService commissionedWellService;

  @Mock
  private CommissionedWellScheduleValidationService commissionedWellScheduleValidationService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private CommissionedWellScheduleSummaryService commissionedWellScheduleSummaryService;

  @BeforeEach
  void setup() {
    commissionedWellScheduleSummaryService = new CommissionedWellScheduleSummaryService(
        commissionedWellScheduleService,
        commissionedWellService,
        commissionedWellScheduleValidationService
    );
  }

  @Test
  void getCommissionedWellScheduleViews_whenNoCommissionedWellSchedules_thenEmptyList() {
    when(commissionedWellScheduleService.getCommissionedWellSchedules(projectDetail))
        .thenReturn(Collections.emptyList());

    var resultingViews = commissionedWellScheduleSummaryService.getCommissionedWellScheduleViews(projectDetail);

    assertThat(resultingViews).isEmpty();
  }

  @Test
  void getCommissionedWellScheduleViews_whenCommissionedWellSchedules_thenVerifyExpectedProperties() {

    var firstCommissionedWellSchedule = new CommissionedWellSchedule();
    firstCommissionedWellSchedule.setProjectDetail(projectDetail);
    firstCommissionedWellSchedule.setEarliestStartYear(2020);

    var firstCommissionedWell = new CommissionedWell();
    firstCommissionedWell.setId(1);
    firstCommissionedWell.setCommissionedWellSchedule(firstCommissionedWellSchedule);
    firstCommissionedWell.setWellbore(WellboreTestUtil.createWellbore());

    var secondCommissionedWellSchedule = new CommissionedWellSchedule();
    secondCommissionedWellSchedule.setProjectDetail(projectDetail);
    secondCommissionedWellSchedule.setEarliestStartYear(2021);

    var commissionedWellSchedules = List.of(
        firstCommissionedWellSchedule,
        secondCommissionedWellSchedule
    );

    var commissionedWells = List.of(
        firstCommissionedWell
    );

    when(commissionedWellScheduleService.getCommissionedWellSchedules(projectDetail))
        .thenReturn(commissionedWellSchedules);

    when(commissionedWellService.getCommissionedWellsForSchedules(commissionedWellSchedules))
        .thenReturn(commissionedWells);

    var resultingViews = commissionedWellScheduleSummaryService.getCommissionedWellScheduleViews(projectDetail);

    var expectedViews = List.of(
        CommissionedWellScheduleViewUtil.from(firstCommissionedWellSchedule, List.of(firstCommissionedWell), 1, true),
        CommissionedWellScheduleViewUtil.from(secondCommissionedWellSchedule, List.of(), 2, true)
    );

    assertThat(resultingViews).isEqualTo(expectedViews);
  }

  @Test
  void getCommissionedWellScheduleView_verifyExpectedView() {

    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    var commissionedWellsForSchedule = List.of(CommissionedWellTestUtil.getCommissionedWell());

    var displayOrder = 10;

    when(commissionedWellService.getCommissionedWellsForSchedule(commissionedWellSchedule))
        .thenReturn(commissionedWellsForSchedule);

    var expectedCommissionedWellScheduleView = CommissionedWellScheduleViewUtil.from(
        commissionedWellSchedule,
        commissionedWellsForSchedule,
        displayOrder,
        true
    );

    var resultingCommissionedWellScheduleView = commissionedWellScheduleSummaryService.getCommissionedWellScheduleView(
        commissionedWellSchedule,
        displayOrder
    );

    assertThat(resultingCommissionedWellScheduleView).isEqualTo(expectedCommissionedWellScheduleView);
  }

  @Test
  void getValidatedCommissionedWellScheduleViews_whenNoCommissionedWellSchedules_thenEmptyList() {

    when(commissionedWellScheduleService.getCommissionedWellSchedules(projectDetail))
        .thenReturn(Collections.emptyList());

    var resultingViews = commissionedWellScheduleSummaryService.getValidatedCommissionedWellScheduleViews(projectDetail);

    assertThat(resultingViews).isEmpty();
  }

  @Test
  void getValidatedCommissionedWellScheduleViews_whenCommissionedWellSchedules_thenPopulatedListWithValidationPropertySet() {

    var firstCommissionedWellSchedule = new CommissionedWellSchedule();
    firstCommissionedWellSchedule.setProjectDetail(projectDetail);
    firstCommissionedWellSchedule.setEarliestStartYear(2020);

    var firstCommissionedWell = new CommissionedWell();
    firstCommissionedWell.setId(1);
    firstCommissionedWell.setCommissionedWellSchedule(firstCommissionedWellSchedule);
    firstCommissionedWell.setWellbore(WellboreTestUtil.createWellbore());

    var validForm = CommissionedWellTestUtil.getCompleteCommissionedWellForm();

    when(commissionedWellScheduleService.getForm(firstCommissionedWellSchedule, List.of(firstCommissionedWell)))
        .thenReturn(validForm);

    when(commissionedWellScheduleValidationService.isFormValid(validForm, ValidationType.FULL)).thenReturn(true);

    var secondCommissionedWellSchedule = new CommissionedWellSchedule();
    secondCommissionedWellSchedule.setProjectDetail(projectDetail);
    secondCommissionedWellSchedule.setEarliestStartYear(2021);

    var invalidForm = CommissionedWellTestUtil.getEmptyCommissionedWellForm();

    when(commissionedWellScheduleService.getForm(secondCommissionedWellSchedule, List.of()))
        .thenReturn(invalidForm);

    when(commissionedWellScheduleValidationService.isFormValid(invalidForm, ValidationType.FULL)).thenReturn(false);

    var commissionedWellSchedules = List.of(
        firstCommissionedWellSchedule,
        secondCommissionedWellSchedule
    );

    var commissionedWells = List.of(
        firstCommissionedWell
    );

    when(commissionedWellScheduleService.getCommissionedWellSchedules(projectDetail))
        .thenReturn(commissionedWellSchedules);

    when(commissionedWellService.getCommissionedWellsForSchedules(commissionedWellSchedules))
        .thenReturn(commissionedWells);

    var resultingViews = commissionedWellScheduleSummaryService.getValidatedCommissionedWellScheduleViews(projectDetail);

    var expectedViews = List.of(
        CommissionedWellScheduleViewUtil.from(firstCommissionedWellSchedule, List.of(firstCommissionedWell), 1, true),
        CommissionedWellScheduleViewUtil.from(secondCommissionedWellSchedule, List.of(), 2, false)
    );

    assertThat(resultingViews).isEqualTo(expectedViews);
  }

  @Test
  void determineViewValidationResult_whenNoViewsToValidation_thenInvalidValidationResult() {
    var validationResult = commissionedWellScheduleSummaryService.determineViewValidationResult(Collections.emptyList());
    assertThat(validationResult).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  void determineViewValidationResult_whenNotAllViewsValid_thenInvalidValidationResult() {

    var validView = new CommissionedWellScheduleView();
    validView.setIsValid(true);

    var invalidView = new CommissionedWellScheduleView();
    invalidView.setIsValid(false);

    var validationResult = commissionedWellScheduleSummaryService.determineViewValidationResult(
        List.of(
            validView,
            invalidView
        )
    );

    assertThat(validationResult).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  void determineViewValidationResult_whenAllViewsValid_thenValidValidationResult() {

    var validView = new CommissionedWellScheduleView();
    validView.setIsValid(true);

    var validationResult = commissionedWellScheduleSummaryService.determineViewValidationResult(
        List.of(validView)
    );

    assertThat(validationResult).isEqualTo(ValidationResult.VALID);
  }

  @Test
  void canShowInTaskList_whenCanShowInTaskList_thenTrue() {
    when(commissionedWellScheduleService.isTaskValidForProjectDetail(projectDetail)).thenReturn(true);

    var canShowInTaskList = commissionedWellScheduleSummaryService.canShowInTaskList(projectDetail);

    assertThat(canShowInTaskList).isTrue();
  }

  @Test
  void canShowInTaskList_whenCannotShowInTaskList_thenFalse() {
    when(commissionedWellScheduleService.isTaskValidForProjectDetail(projectDetail)).thenReturn(false);

    var canShowInTaskList = commissionedWellScheduleSummaryService.canShowInTaskList(projectDetail);

    assertThat(canShowInTaskList).isFalse();
  }


  @Test
  void getCommissionedWellScheduleViewViewsByProjectAndVersion_whenNoResults_thenReturnEmptyList() {

    var project = projectDetail.getProject();
    var version = projectDetail.getVersion();

    when(commissionedWellScheduleService.getCommissionedWellSchedulesByProjectAndVersion(
        project,
        version
    )).thenReturn(Collections.emptyList());

    var resultingCommissionedWellScheduleViews = commissionedWellScheduleSummaryService.getCommissionedWellScheduleViewViewsByProjectAndVersion(
        project,
        version
    );

    assertThat(resultingCommissionedWellScheduleViews).isEmpty();
  }

  @Test
  void getCommissionedWellScheduleViewViewsByProjectAndVersion_whenResults_thenVerifyExpectedProperties() {

    var project = projectDetail.getProject();
    var version = projectDetail.getVersion();

    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    var commissionedWell = CommissionedWellTestUtil.getCommissionedWell();
    commissionedWell.setCommissionedWellSchedule(commissionedWellSchedule);

    when(commissionedWellScheduleService.getCommissionedWellSchedulesByProjectAndVersion(
        project,
        version
    )).thenReturn(List.of(commissionedWellSchedule));

    when(commissionedWellService.getCommissionedWellsForSchedules(List.of(commissionedWellSchedule)))
        .thenReturn(List.of(commissionedWell));

    var resultingCommissionedWellScheduleViews = commissionedWellScheduleSummaryService.getCommissionedWellScheduleViewViewsByProjectAndVersion(
        project,
        version
    );

    assertThat(resultingCommissionedWellScheduleViews).containsExactly(
        CommissionedWellScheduleViewUtil.from(
            commissionedWellSchedule,
            List.of(commissionedWell),
            1,
            true
        )
    );
  }

}