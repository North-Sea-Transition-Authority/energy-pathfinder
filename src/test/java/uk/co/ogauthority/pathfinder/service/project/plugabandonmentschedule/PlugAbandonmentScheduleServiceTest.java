package uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule.PlugAbandonmentScheduleForm;
import uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule.PlugAbandonmentScheduleFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.plugabandonmentschedule.PlugAbandonmentScheduleRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.service.wellbore.WellboreService;
import uk.co.ogauthority.pathfinder.testutil.PlugAbandonmentScheduleTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class PlugAbandonmentScheduleServiceTest {

  @Mock
  private WellboreService wellboreService;

  @Mock
  private ValidationService validationService;

  @Mock
  private PlugAbandonmentScheduleFormValidator plugAbandonmentScheduleFormValidator;

  @Mock
  private PlugAbandonmentScheduleRepository plugAbandonmentScheduleRepository;

  @Mock
  private PlugAbandonmentWellService plugAbandonmentWellService;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private PlugAbandonmentScheduleService plugAbandonmentScheduleService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    plugAbandonmentScheduleService = new PlugAbandonmentScheduleService(
        wellboreService,
        validationService,
        plugAbandonmentScheduleFormValidator,
        plugAbandonmentScheduleRepository,
        plugAbandonmentWellService,
        projectSetupService,
        entityDuplicationService
    );

    when(plugAbandonmentScheduleRepository.save(any(PlugAbandonmentSchedule.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void getWellboreRestUrl() {
    wellboreService.getWellboreRestUrl();
    verify(wellboreService, times(1)).getWellboreRestUrl();
  }

  @Test
  public void validate_whenPartial() {
    var form = new PlugAbandonmentScheduleForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    plugAbandonmentScheduleService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );

    verify(validationService).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_whenFull() {
    var form = new PlugAbandonmentScheduleForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    plugAbandonmentScheduleService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void createPlugAbandonmentSchedule() {
    var form = PlugAbandonmentScheduleTestUtil.getCompletedForm();
    var projectDetail = ProjectUtil.getProjectDetails();
    var plugAbandonmentSchedule = plugAbandonmentScheduleService.createPlugAbandonmentSchedule(form, projectDetail);

    checkCommonEntityFields(form, plugAbandonmentSchedule, projectDetail);
  }

  @Test
  public void updatePlugAbandonmentSchedule_whenPlugAbandonmentScheduleEntityFoundAndNoManualEntry() {

    final Integer plugAbandonmentScheduleId = 1;
    final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

    var form = PlugAbandonmentScheduleTestUtil.getCompletedForm();

    var plugAbandonmentSchedule = new PlugAbandonmentSchedule();
    plugAbandonmentSchedule.setProjectDetail(projectDetail);

    when(plugAbandonmentScheduleRepository.findByIdAndProjectDetail(plugAbandonmentScheduleId, projectDetail))
        .thenReturn(Optional.of(plugAbandonmentSchedule));

    plugAbandonmentSchedule = plugAbandonmentScheduleService.updatePlugAbandonmentSchedule(
        plugAbandonmentScheduleId,
        projectDetail,
        form
    );

    checkCommonEntityFields(form, plugAbandonmentSchedule, projectDetail);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void updatePlugAbandonmentSchedule_whenPlugAbandonmentScheduleEntityNotFound_thenException() {

    final Integer plugAbandonmentScheduleId = 1;
    final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

    when(plugAbandonmentScheduleRepository.findByIdAndProjectDetail(plugAbandonmentScheduleId, projectDetail))
        .thenReturn(Optional.empty());

    plugAbandonmentScheduleService.updatePlugAbandonmentSchedule(plugAbandonmentScheduleId, projectDetail, new PlugAbandonmentScheduleForm());
    verify(plugAbandonmentScheduleRepository, times(0)).save(any());
  }

  @Test
  public void getForm() {
    var plugAbandonmentSchedule = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule();

    var form = plugAbandonmentScheduleService.getForm(plugAbandonmentSchedule);

    var plugAbandonmentDate = form.getPlugAbandonmentDate();
    assertThat(plugAbandonmentDate.getMinYear()).isEqualTo(Integer.toString(plugAbandonmentSchedule.getEarliestStartYear()));
    assertThat(plugAbandonmentDate.getMaxYear()).isEqualTo(Integer.toString(plugAbandonmentSchedule.getLatestCompletionYear()));
  }

  private void checkCommonEntityFields(PlugAbandonmentScheduleForm form,
                                       PlugAbandonmentSchedule plugAbandonmentSchedule,
                                       ProjectDetail projectDetail) {
    assertThat(plugAbandonmentSchedule.getProjectDetail()).isEqualTo(projectDetail);
    var plugAbandonmentDate = form.getPlugAbandonmentDate();
    assertThat(plugAbandonmentSchedule.getEarliestStartYear()).isEqualTo(Integer.parseInt(plugAbandonmentDate.getMinYear()));
    assertThat(plugAbandonmentSchedule.getLatestCompletionYear()).isEqualTo(Integer.parseInt(plugAbandonmentDate.getMaxYear()));
  }

  @Test
  public void getPlugAbandonmentScheduleOrError_whenFound_thenReturn() {
    var plugAbandonmentScheduleId = 1;
    var plugAbandonmentSchedule = new PlugAbandonmentSchedule();

    when(plugAbandonmentScheduleRepository.findByIdAndProjectDetail(plugAbandonmentScheduleId, detail)).thenReturn(Optional.of(plugAbandonmentSchedule));

    var result = plugAbandonmentScheduleService.getPlugAbandonmentScheduleOrError(plugAbandonmentScheduleId, detail);
    assertThat(result).isEqualTo(plugAbandonmentSchedule);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getPlugAbandonmentScheduleOrError_whenNotFound_thenException() {
    var plugAbandonmentScheduleId = 1;

    when(plugAbandonmentScheduleRepository.findByIdAndProjectDetail(plugAbandonmentScheduleId, detail)).thenReturn(Optional.empty());

    plugAbandonmentScheduleService.getPlugAbandonmentScheduleOrError(plugAbandonmentScheduleId, detail);
  }

  @Test
  public void getPlugAbandonmentSchedulesForProjectDetail_whenResults_thenReturnPopulatedList() {

    final var plugAbandonmentSchedule1 = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule();
    final var plugAbandonmentSchedule2 = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule();
    final var plugAbandonmentSchedules = List.of(plugAbandonmentSchedule1, plugAbandonmentSchedule2);

    when(plugAbandonmentScheduleRepository.findByProjectDetailOrderByIdAsc(detail)).thenReturn(plugAbandonmentSchedules);

    final var result = plugAbandonmentScheduleService.getPlugAbandonmentSchedulesForProjectDetail(detail);

    assertThat(result).containsExactly(
        plugAbandonmentSchedule1,
        plugAbandonmentSchedule2
    );
  }

  @Test
  public void getPlugAbandonmentSchedulesForProjectDetail_whenNoResults_thenReturnEmptyList() {

    when(plugAbandonmentScheduleRepository.findByProjectDetailOrderByIdAsc(detail)).thenReturn(List.of());

    final var result = plugAbandonmentScheduleService.getPlugAbandonmentSchedulesForProjectDetail(detail);

    assertThat(result).isEmpty();
  }

  @Test
  public void canShowInTaskList_true() {
    when(projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.WELLS)).thenReturn(true);
    assertThat(plugAbandonmentScheduleService.canShowInTaskList(detail)).isTrue();
  }

  @Test
  public void canShowInTaskList_false() {
    when(projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.WELLS)).thenReturn(false);
    assertThat(plugAbandonmentScheduleService.canShowInTaskList(detail)).isFalse();
  }

  @Test
  public void removeSectionData_verifyInteractions() {
    plugAbandonmentScheduleService.removeSectionData(detail);

    verify(plugAbandonmentScheduleRepository, times(1)).deleteAllByProjectDetail(detail);
  }

  @Test
  public void copySectionData_verifyDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    final var plugAbandonmentSchedules = List.of(PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule());
    when(plugAbandonmentScheduleRepository.findByProjectDetailOrderByIdAsc(fromProjectDetail)).thenReturn(plugAbandonmentSchedules);

    plugAbandonmentScheduleService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        plugAbandonmentSchedules,
        toProjectDetail,
        PlugAbandonmentSchedule.class
    );
  }

}