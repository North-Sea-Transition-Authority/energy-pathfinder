package uk.co.ogauthority.pathfinder.service.project.commissionedwell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWell;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.commissionedwell.CommissionedWellForm;
import uk.co.ogauthority.pathfinder.repository.project.commissionedwell.CommissionedWellScheduleRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.testutil.CommissionedWellTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.WellboreTestUtil;

@ExtendWith(MockitoExtension.class)
class CommissionedWellScheduleServiceTest {

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private CommissionedWellScheduleRepository commissionedWellScheduleRepository;

  @Mock
  private CommissionedWellService commissionedWellService;

  @Mock
  private CommissionedWellScheduleValidationService commissionedWellScheduleValidationService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private ProjectDetail projectDetail;

  private CommissionedWellScheduleService commissionedWellScheduleService;

  @BeforeEach
  void setup() {
    commissionedWellScheduleService = new CommissionedWellScheduleService(
        projectSetupService,
        commissionedWellScheduleRepository,
        commissionedWellService,
        commissionedWellScheduleValidationService,
        entityDuplicationService
    );

    projectDetail = ProjectUtil.getProjectDetails();
  }

  @Test
  void isComplete_whenNoSchedules_thenFalse() {

    when(commissionedWellScheduleRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(Collections.emptyList());

    var isComplete = commissionedWellScheduleService.isComplete(projectDetail);

    assertFalse(isComplete);
  }

  @Test
  void isComplete_whenNoSchedulesComplete_thenFalse() {

    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    when(commissionedWellScheduleRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of(commissionedWellSchedule));

    when(commissionedWellScheduleValidationService.areAllFormsValid(any(), any())).thenReturn(false);

    var isComplete = commissionedWellScheduleService.isComplete(projectDetail);

    assertFalse(isComplete);
  }

  @Test
  void isComplete_whenAllSchedulesComplete_thenTrue() {

    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    when(commissionedWellScheduleRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of(commissionedWellSchedule));

    when(commissionedWellScheduleValidationService.areAllFormsValid(any(), any())).thenReturn(true);

    var isComplete = commissionedWellScheduleService.isComplete(projectDetail);

    assertTrue(isComplete);
  }

  @Test
  void canShowInTaskList_whenSelectedSection_thenReturnTrue() {

    when(projectSetupService.taskValidAndSelectedForProjectDetail(
        projectDetail,
        ProjectTask.COMMISSIONED_WELLS
    )).thenReturn(true);

    var canShowInTaskList = commissionedWellScheduleService.canShowInTaskList(projectDetail);

    assertTrue(canShowInTaskList);
  }

  @Test
  void canShowInTaskList_whenNotSelectedSection_thenReturnFalse() {

    when(projectSetupService.taskValidAndSelectedForProjectDetail(
        projectDetail,
        ProjectTask.COMMISSIONED_WELLS
    )).thenReturn(false);

    var canShowInTaskList = commissionedWellScheduleService.canShowInTaskList(projectDetail);

    assertFalse(canShowInTaskList);

  }

  @Test
  void createCommissionedWellSchedule_whenValidForm_verifyInteractions() {

    var selectedWellIds = List.of(1);

    var commissionedWellForm = new CommissionedWellForm();
    commissionedWellForm.setCommissioningSchedule(new MinMaxDateInput("2020", String.valueOf(LocalDate.now().getYear())));
    commissionedWellForm.setWells(selectedWellIds);

    when(commissionedWellScheduleRepository.save(any(CommissionedWellSchedule.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

    commissionedWellScheduleService.createCommissionWellSchedule(
        commissionedWellForm,
        projectDetail
    );

    var commissionedWellScheduleCaptor = ArgumentCaptor.forClass(CommissionedWellSchedule.class);
    verify(commissionedWellScheduleRepository, times(1)).save(commissionedWellScheduleCaptor.capture());

    var resultingCommissionedWellSchedule = commissionedWellScheduleCaptor.getValue();

    verify(commissionedWellService, times(1)).saveCommissionedWells(
        resultingCommissionedWellSchedule,
        selectedWellIds
    );

    assertThat(resultingCommissionedWellSchedule.getProjectDetail()).isEqualTo(projectDetail);

    var expectedCommissioningSchedule = commissionedWellForm.getCommissioningSchedule();
    assertThat(resultingCommissionedWellSchedule.getEarliestStartYear()).isEqualTo(Integer.parseInt(expectedCommissioningSchedule.getMinYear()));
    assertThat(resultingCommissionedWellSchedule.getLatestCompletionYear()).isEqualTo(Integer.parseInt(expectedCommissioningSchedule.getMaxYear()));
  }

  @Test
  void createCommissionedWellSchedule_whenEmptyForm_verifyInteractions() {

    List<Integer> selectedWellIds = Collections.emptyList();

    var commissionedWellForm = new CommissionedWellForm();
    commissionedWellForm.setCommissioningSchedule(new MinMaxDateInput(null, null));
    commissionedWellForm.setWells(selectedWellIds);

    when(commissionedWellScheduleRepository.save(any(CommissionedWellSchedule.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

    commissionedWellScheduleService.createCommissionWellSchedule(
        commissionedWellForm,
        projectDetail
    );

    var commissionedWellScheduleCaptor = ArgumentCaptor.forClass(CommissionedWellSchedule.class);
    verify(commissionedWellScheduleRepository, times(1)).save(commissionedWellScheduleCaptor.capture());

    var resultingCommissionedWellSchedule = commissionedWellScheduleCaptor.getValue();

    verify(commissionedWellService, times(1)).saveCommissionedWells(
        resultingCommissionedWellSchedule,
        selectedWellIds
    );

    assertThat(resultingCommissionedWellSchedule.getProjectDetail()).isEqualTo(projectDetail);

    assertThat(resultingCommissionedWellSchedule.getEarliestStartYear()).isNull();
    assertThat(resultingCommissionedWellSchedule.getLatestCompletionYear()).isNull();
  }

  @Test
  void updateCommissionedWellSchedule_whenValidForm_verifyInteractions() {

    var commissioningWellSchedule = new CommissionedWellSchedule();

    var selectedWellIds = List.of(1);

    var commissionedWellForm = new CommissionedWellForm();
    commissionedWellForm.setCommissioningSchedule(new MinMaxDateInput("2020", String.valueOf(LocalDate.now().getYear())));
    commissionedWellForm.setWells(selectedWellIds);

    when(commissionedWellScheduleRepository.save(any(CommissionedWellSchedule.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

    commissionedWellScheduleService.updateCommissionedWellSchedule(
        commissioningWellSchedule,
        commissionedWellForm
    );

    var commissionedWellScheduleCaptor = ArgumentCaptor.forClass(CommissionedWellSchedule.class);
    verify(commissionedWellScheduleRepository, times(1)).save(commissionedWellScheduleCaptor.capture());

    var resultingCommissionedWellSchedule = commissionedWellScheduleCaptor.getValue();

    verify(commissionedWellService, times(1)).saveCommissionedWells(
        resultingCommissionedWellSchedule,
        selectedWellIds
    );

    var expectedCommissioningSchedule = commissionedWellForm.getCommissioningSchedule();
    assertThat(resultingCommissionedWellSchedule.getEarliestStartYear()).isEqualTo(Integer.parseInt(expectedCommissioningSchedule.getMinYear()));
    assertThat(resultingCommissionedWellSchedule.getLatestCompletionYear()).isEqualTo(Integer.parseInt(expectedCommissioningSchedule.getMaxYear()));
  }

  @Test
  void updateCommissionedWellSchedule_whenEmptyForm_verifyInteractions() {

    var commissioningWellSchedule = new CommissionedWellSchedule();

    List<Integer> selectedWellIds = Collections.emptyList();

    var commissionedWellForm = new CommissionedWellForm();
    commissionedWellForm.setCommissioningSchedule(new MinMaxDateInput(null, null));
    commissionedWellForm.setWells(selectedWellIds);

    when(commissionedWellScheduleRepository.save(any(CommissionedWellSchedule.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

    commissionedWellScheduleService.updateCommissionedWellSchedule(
        commissioningWellSchedule,
        commissionedWellForm
    );

    var commissionedWellScheduleCaptor = ArgumentCaptor.forClass(CommissionedWellSchedule.class);
    verify(commissionedWellScheduleRepository, times(1)).save(commissionedWellScheduleCaptor.capture());

    var resultingCommissionedWellSchedule = commissionedWellScheduleCaptor.getValue();

    verify(commissionedWellService, times(1)).saveCommissionedWells(
        resultingCommissionedWellSchedule,
        selectedWellIds
    );

    assertThat(resultingCommissionedWellSchedule.getEarliestStartYear()).isNull();
    assertThat(resultingCommissionedWellSchedule.getLatestCompletionYear()).isNull();
  }

  @Test
  void getCommissionedWellSchedules_whenNoResultsFound_thenEmptyList() {
    when(commissionedWellScheduleRepository.findByProjectDetailOrderByIdAsc(projectDetail)).thenReturn(List.of());
    var result = commissionedWellScheduleService.getCommissionedWellSchedules(projectDetail);
    assertThat(result).isEmpty();
  }

  @Test
  void getCommissionedWellSchedules_whenResultsFound_thenPopulatedList() {

    var expectedCommissionedWellSchedules = List.of(new CommissionedWellSchedule());

    when(commissionedWellScheduleRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(expectedCommissionedWellSchedules);

    var result = commissionedWellScheduleService.getCommissionedWellSchedules(projectDetail);

    assertThat(result).isEqualTo(expectedCommissionedWellSchedules);
  }

  @Test
  void getForm_whenScheduleDateIsNull_thenVerifyFormPropertiesAreNull() {

    var commissionedWellSchedule = new CommissionedWellSchedule();
    commissionedWellSchedule.setEarliestStartYear(null);
    commissionedWellSchedule.setLatestCompletionYear(null);

    var resultingForm = commissionedWellScheduleService.getForm(
        commissionedWellSchedule,
        List.of()
    );

    var resultingCommissioningSchedule = resultingForm.getCommissioningSchedule();
    assertThat(resultingCommissioningSchedule.getMinYear()).isNull();
    assertThat(resultingCommissioningSchedule.getMaxYear()).isNull();
  }

  @Test
  void getForm_whenScheduleDateIsNotNull_thenVerifyFormPropertiesArePopulated() {

    var commissionedWellSchedule = new CommissionedWellSchedule();
    commissionedWellSchedule.setEarliestStartYear(2021);
    commissionedWellSchedule.setLatestCompletionYear(2025);

    var resultingForm = commissionedWellScheduleService.getForm(
        commissionedWellSchedule,
        List.of()
    );

    var resultingCommissioningSchedule = resultingForm.getCommissioningSchedule();
    assertThat(resultingCommissioningSchedule.getMinYear()).isEqualTo(String.valueOf(commissionedWellSchedule.getEarliestStartYear()));
    assertThat(resultingCommissioningSchedule.getMaxYear()).isEqualTo(String.valueOf(commissionedWellSchedule.getLatestCompletionYear()));
  }

  @Test
  void getForm_whenWellListIsEmpty_thenVerifyFormPropertyIsEmpty() {

    var commissionedWellSchedule = new CommissionedWellSchedule();

    var resultingForm = commissionedWellScheduleService.getForm(
        commissionedWellSchedule,
        List.of()
    );

    assertThat(resultingForm.getWells()).isEmpty();
  }

  @Test
  void getForm_whenWellListIsPopulated_thenVerifyFormPropertyIsPopulated() {

    var commissionedWellSchedule = new CommissionedWellSchedule();

    var commissionedWell1 = new CommissionedWell();
    commissionedWell1.setWellbore(WellboreTestUtil.createWellbore(1));

    var commissionedWell2 = new CommissionedWell();
    commissionedWell2.setWellbore(WellboreTestUtil.createWellbore(2));

    var inputCommissionedWells = List.of(
        commissionedWell1,
        commissionedWell2
    );

    var resultingForm = commissionedWellScheduleService.getForm(
        commissionedWellSchedule,
        inputCommissionedWells
    );

    var expectedWellboreIdList = inputCommissionedWells
        .stream()
        .map(commissionedWell -> commissionedWell.getWellbore().getId())
        .collect(Collectors.toUnmodifiableList());

    assertThat(resultingForm.getWells()).isEqualTo(expectedWellboreIdList);

  }

  @Test
  void getCommissionedWellsForSchedule_whenNoResultsFound_thenEmptyList() {

    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    when(commissionedWellService.getCommissionedWellsForSchedule(commissionedWellSchedule))
        .thenReturn(Collections.emptyList());

    var resultingCommissionedWells = commissionedWellScheduleService.getCommissionedWellsForSchedule(commissionedWellSchedule);

    assertThat(resultingCommissionedWells).isEmpty();
  }

  @Test
  void getCommissionedWellsForSchedule_whenResultsFound_thenPopulatedList() {

    var commissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    var expectedCommissionedWells = List.of(
        CommissionedWellTestUtil.getCommissionedWell()
    );

    when(commissionedWellService.getCommissionedWellsForSchedule(commissionedWellSchedule))
        .thenReturn(expectedCommissionedWells);

    var resultingCommissionedWells = commissionedWellScheduleService.getCommissionedWellsForSchedule(commissionedWellSchedule);

    assertThat(resultingCommissionedWells).isEqualTo(expectedCommissionedWells);
  }

  @Test
  void getCommissionedWellSchedule_whenNoResultFound_thenEmptyOptional() {

    var commissionedWellScheduleId = 10;

    when(commissionedWellScheduleRepository.findById(commissionedWellScheduleId)).thenReturn(Optional.empty());

    var resultingCommissionedWellSchedule = commissionedWellScheduleService.getCommissionedWellSchedule(commissionedWellScheduleId);

    assertThat(resultingCommissionedWellSchedule).isEmpty();
  }

  @Test
  void getCommissionedWellSchedule_whenResultFound_thenPopulatedOptional() {

    var commissionedWellScheduleId = 10;
    var expectedCommissionedWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    when(commissionedWellScheduleRepository.findById(commissionedWellScheduleId))
        .thenReturn(Optional.of(expectedCommissionedWellSchedule));

    var resultingCommissionedWellSchedule = commissionedWellScheduleService.getCommissionedWellSchedule(commissionedWellScheduleId);

    assertThat(resultingCommissionedWellSchedule).contains(expectedCommissionedWellSchedule);

  }

  @Test
  void getSupportedProjectTypes_verifyInfrastructure() {
    var resultingSupportedProjectTypes = commissionedWellScheduleService.getSupportedProjectTypes();
    assertThat(resultingSupportedProjectTypes).containsExactly(ProjectType.INFRASTRUCTURE);
  }

  @Test
  void deleteCommissionedWellSchedule_verifyInteractions() {

    var commissionWellSchedule = CommissionedWellTestUtil.getCommissionedWellSchedule();

    commissionedWellScheduleService.deleteCommissionedWellSchedule(commissionWellSchedule);

    verify(commissionedWellService, times(1)).deleteCommissionedWells(commissionWellSchedule);
    verify(commissionedWellScheduleRepository, times(1)).delete(commissionWellSchedule);
  }

  @Test
  void getCommissionedWellSchedulesByProjectAndVersion_whenNoResultsFound_thenEmptyListReturned() {

    var project = projectDetail.getProject();
    var version = projectDetail.getVersion();

    when(commissionedWellScheduleRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        project,
        version
    )).thenReturn(Collections.emptyList());

    var resultingCommissionedWellSchedules = commissionedWellScheduleService.getCommissionedWellSchedulesByProjectAndVersion(
        project,
        version
    );

    assertThat(resultingCommissionedWellSchedules).isEmpty();
  }

  @Test
  void getCommissionedWellSchedulesByProjectAndVersion_whenResultsFound_thenEmptyListReturned() {

    var project = projectDetail.getProject();
    var version = projectDetail.getVersion();

    var commissionedWellSchedules = List.of(CommissionedWellTestUtil.getCommissionedWellSchedule());

    when(commissionedWellScheduleRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        project,
        version
    )).thenReturn(commissionedWellSchedules);

    var resultingCommissionedWellSchedules = commissionedWellScheduleService.getCommissionedWellSchedulesByProjectAndVersion(
        project,
        version
    );

    assertThat(resultingCommissionedWellSchedules).isEqualTo(commissionedWellSchedules);
  }

  @Test
  void copySectionData_verifyDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    final var commissionedWellSchedules = List.of(CommissionedWellTestUtil.getCommissionedWellSchedule());
    when(commissionedWellScheduleRepository.findByProjectDetailOrderByIdAsc(fromProjectDetail)).thenReturn(commissionedWellSchedules);

    commissionedWellScheduleService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        commissionedWellSchedules,
        toProjectDetail,
        CommissionedWellSchedule.class
    );

    verify(commissionedWellService, times(1)).copyCommissionedWellsToNewSchedules(anyMap());
  }

  @Test
  void removeSectionData_verifyInteractions() {

    var commissionedWellScheduleToRemove = CommissionedWellTestUtil.getCommissionedWellSchedule();

    when(commissionedWellScheduleRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of(commissionedWellScheduleToRemove));

    commissionedWellScheduleService.removeSectionData(projectDetail);

    verify(commissionedWellService, times(1)).deleteCommissionedWells(
        List.of(commissionedWellScheduleToRemove)
    );

    verify(commissionedWellScheduleRepository, times(1)).deleteAll(
        List.of(commissionedWellScheduleToRemove)
    );
  }
}