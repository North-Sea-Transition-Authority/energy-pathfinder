package uk.co.ogauthority.pathfinder.service.project.decommissioningschedule;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.controller.project.decommissioningschedule.DecommissioningScheduleController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissioningschedule.DecommissioningSchedule;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.CessationOfProductionDateType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.DecommissioningStartDateType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.project.decommissioningschedule.DecommissioningScheduleForm;
import uk.co.ogauthority.pathfinder.model.form.project.decommissioningschedule.DecommissioningScheduleFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.decommissioningschedule.DecommissioningScheduleRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.DecommissioningScheduleTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class DecommissioningScheduleServiceTest {

  @Mock
  private DecommissioningScheduleRepository decommissioningScheduleRepository;

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private ValidationService validationService;

  @Mock
  private DecommissioningScheduleFormValidator decommissioningScheduleFormValidator;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private BreadcrumbService breadcrumbService;

  private DecommissioningScheduleService decommissioningScheduleService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Before
  public void setUp() {
    decommissioningScheduleService = new DecommissioningScheduleService(
        decommissioningScheduleRepository,
        projectInformationService,
        validationService,
        decommissioningScheduleFormValidator,
        entityDuplicationService,
        breadcrumbService
    );

    when(decommissioningScheduleRepository.save(any(DecommissioningSchedule.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void validate() {
    var form = new DecommissioningScheduleForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    decommissioningScheduleService.validate(form, bindingResult, ValidationType.FULL);

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void createOrUpdate_whenExactDecommissioningStartDateType() {
    var form = new DecommissioningScheduleForm();
    form.setDecommissioningStartDateType(DecommissioningStartDateType.EXACT);

    var decommissioningStartDate = LocalDate.now();
    form.setExactDecommissioningStartDate(new ThreeFieldDateInput(decommissioningStartDate));

    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(DecommissioningScheduleTestUtil.createDecommissioningSchedule())
    );

    var decommissioningSchedule = decommissioningScheduleService.createOrUpdate(projectDetail, form);

    assertThat(decommissioningSchedule.getDecommissioningStartDateType()).isEqualTo(DecommissioningStartDateType.EXACT);
    assertThat(decommissioningSchedule.getExactDecommissioningStartDate()).isEqualTo(decommissioningStartDate);

    assertThat(decommissioningSchedule.getEstimatedDecommissioningStartDateQuarter()).isNull();
    assertThat(decommissioningSchedule.getEstimatedDecommissioningStartDateYear()).isNull();
    assertThat(decommissioningSchedule.getDecommissioningStartDateNotProvidedReason()).isNull();

    verify(decommissioningScheduleRepository, times(1)).save(decommissioningSchedule);
  }

  @Test
  public void createOrUpdate_whenEstimatedDecommissioningStartDateType() {
    var form = new DecommissioningScheduleForm();
    form.setDecommissioningStartDateType(DecommissioningStartDateType.ESTIMATED);

    var decommissioningStartDateQuarter = Quarter.Q2;
    var decommissioningStartDateYear = 2020;
    form.setEstimatedDecommissioningStartDate(
        new QuarterYearInput(decommissioningStartDateQuarter, Integer.toString(decommissioningStartDateYear))
    );

    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(DecommissioningScheduleTestUtil.createDecommissioningSchedule())
    );

    var decommissioningSchedule = decommissioningScheduleService.createOrUpdate(projectDetail, form);

    assertThat(decommissioningSchedule.getDecommissioningStartDateType()).isEqualTo(DecommissioningStartDateType.ESTIMATED);
    assertThat(decommissioningSchedule.getEstimatedDecommissioningStartDateQuarter()).isEqualTo(decommissioningStartDateQuarter);
    assertThat(decommissioningSchedule.getEstimatedDecommissioningStartDateYear()).isEqualTo(decommissioningStartDateYear);

    assertThat(decommissioningSchedule.getExactDecommissioningStartDate()).isNull();
    assertThat(decommissioningSchedule.getDecommissioningStartDateNotProvidedReason()).isNull();

    verify(decommissioningScheduleRepository, times(1)).save(decommissioningSchedule);
  }

  @Test
  public void createOrUpdate_whenUnknownDecommissioningStartDateType() {
    var form = new DecommissioningScheduleForm();
    form.setDecommissioningStartDateType(DecommissioningStartDateType.UNKNOWN);

    var decommissioningStartDateNotProvidedReason = "Test reason";
    form.setDecommissioningStartDateNotProvidedReason(decommissioningStartDateNotProvidedReason);

    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(DecommissioningScheduleTestUtil.createDecommissioningSchedule())
    );

    var decommissioningSchedule = decommissioningScheduleService.createOrUpdate(projectDetail, form);

    assertThat(decommissioningSchedule.getDecommissioningStartDateType()).isEqualTo(DecommissioningStartDateType.UNKNOWN);
    assertThat(decommissioningSchedule.getDecommissioningStartDateNotProvidedReason()).isEqualTo(decommissioningStartDateNotProvidedReason);

    assertThat(decommissioningSchedule.getExactDecommissioningStartDate()).isNull();
    assertThat(decommissioningSchedule.getEstimatedDecommissioningStartDateQuarter()).isNull();
    assertThat(decommissioningSchedule.getEstimatedDecommissioningStartDateYear()).isNull();

    verify(decommissioningScheduleRepository, times(1)).save(decommissioningSchedule);
  }

  @Test
  public void createOrUpdate_whenExactCessationOfProductionDateType() {
    var form = new DecommissioningScheduleForm();
    form.setCessationOfProductionDateType(CessationOfProductionDateType.EXACT);

    var cessationOfProductionDate = LocalDate.now();
    form.setExactCessationOfProductionDate(new ThreeFieldDateInput(cessationOfProductionDate));

    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(DecommissioningScheduleTestUtil.createDecommissioningSchedule())
    );

    var decommissioningSchedule = decommissioningScheduleService.createOrUpdate(projectDetail, form);

    assertThat(decommissioningSchedule.getCessationOfProductionDateType()).isEqualTo(CessationOfProductionDateType.EXACT);
    assertThat(decommissioningSchedule.getExactCessationOfProductionDate()).isEqualTo(cessationOfProductionDate);

    assertThat(decommissioningSchedule.getEstimatedCessationOfProductionDateQuarter()).isNull();
    assertThat(decommissioningSchedule.getEstimatedCessationOfProductionDateYear()).isNull();
    assertThat(decommissioningSchedule.getCessationOfProductionDateNotProvidedReason()).isNull();

    verify(decommissioningScheduleRepository, times(1)).save(decommissioningSchedule);
  }

  @Test
  public void createOrUpdate_whenEstimatedCessationOfProductionDateType() {
    var form = new DecommissioningScheduleForm();
    form.setCessationOfProductionDateType(CessationOfProductionDateType.ESTIMATED);

    var cessationOfProductionDateQuarter = Quarter.Q2;
    var cessationOfProductionDateYear = 2020;
    form.setEstimatedCessationOfProductionDate(
        new QuarterYearInput(cessationOfProductionDateQuarter, Integer.toString(cessationOfProductionDateYear))
    );

    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(DecommissioningScheduleTestUtil.createDecommissioningSchedule())
    );

    var decommissioningSchedule = decommissioningScheduleService.createOrUpdate(projectDetail, form);

    assertThat(decommissioningSchedule.getCessationOfProductionDateType()).isEqualTo(CessationOfProductionDateType.ESTIMATED);
    assertThat(decommissioningSchedule.getEstimatedCessationOfProductionDateQuarter()).isEqualTo(cessationOfProductionDateQuarter);
    assertThat(decommissioningSchedule.getEstimatedCessationOfProductionDateYear()).isEqualTo(cessationOfProductionDateYear);

    assertThat(decommissioningSchedule.getExactCessationOfProductionDate()).isNull();
    assertThat(decommissioningSchedule.getCessationOfProductionDateNotProvidedReason()).isNull();

    verify(decommissioningScheduleRepository, times(1)).save(decommissioningSchedule);
  }

  @Test
  public void createOrUpdate_whenUnknownCessationOfProductionDateType() {
    var form = new DecommissioningScheduleForm();
    form.setCessationOfProductionDateType(CessationOfProductionDateType.UNKNOWN);

    var cessationOfProductionDateNotProvidedReason = "Test reason";
    form.setCessationOfProductionDateNotProvidedReason(cessationOfProductionDateNotProvidedReason);

    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(DecommissioningScheduleTestUtil.createDecommissioningSchedule())
    );

    var decommissioningSchedule = decommissioningScheduleService.createOrUpdate(projectDetail, form);

    assertThat(decommissioningSchedule.getCessationOfProductionDateType()).isEqualTo(CessationOfProductionDateType.UNKNOWN);
    assertThat(decommissioningSchedule.getCessationOfProductionDateNotProvidedReason()).isEqualTo(cessationOfProductionDateNotProvidedReason);

    assertThat(decommissioningSchedule.getExactCessationOfProductionDate()).isNull();
    assertThat(decommissioningSchedule.getEstimatedCessationOfProductionDateQuarter()).isNull();
    assertThat(decommissioningSchedule.getEstimatedCessationOfProductionDateYear()).isNull();

    verify(decommissioningScheduleRepository, times(1)).save(decommissioningSchedule);
  }

  @Test
  public void getDecommissioningSchedule_whenFound_thenReturn() {
    var decommissioningSchedule = new DecommissioningSchedule();

    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(decommissioningSchedule));

    assertThat(decommissioningScheduleService.getDecommissioningSchedule(projectDetail)).contains(decommissioningSchedule);
  }

  @Test
  public void getDecommissioningSchedule_whenNotFound_thenReturnEmpty() {
    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    assertThat(decommissioningScheduleService.getDecommissioningSchedule(projectDetail)).isEmpty();
  }

  @Test
  public void getDecommissioningScheduleOrError_whenFound_thenReturn() {
    var decommissioningSchedule = new DecommissioningSchedule();

    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(decommissioningSchedule));

    assertThat(decommissioningScheduleService.getDecommissioningScheduleOrError(projectDetail)).isEqualTo(decommissioningSchedule);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getDecommissioningScheduleOrError_whenNotFound_thenError() {
    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    decommissioningScheduleService.getDecommissioningScheduleOrError(projectDetail);
  }

  @Test
  public void getDecommissioningScheduleByProjectAndVersion_whenFound_thenReturn() {
    var project = projectDetail.getProject();
    var version = projectDetail.getVersion() - 1;

    var decommissioningSchedule = new DecommissioningSchedule();

    when(decommissioningScheduleRepository.findByProjectDetail_ProjectAndProjectDetail_Version(project, version)).thenReturn(
        Optional.of(decommissioningSchedule)
    );

    assertThat(decommissioningScheduleService.getDecommissioningScheduleByProjectAndVersion(project, version)).contains(decommissioningSchedule);
  }


  @Test
  public void getDecommissioningScheduleByProjectAndVersion_whenNotFound_thenReturnEmpty() {
    var project = projectDetail.getProject();
    var version = projectDetail.getVersion() - 1;

    when(decommissioningScheduleRepository.findByProjectDetail_ProjectAndProjectDetail_Version(project, version)).thenReturn(
        Optional.empty()
    );

    assertThat(decommissioningScheduleService.getDecommissioningScheduleByProjectAndVersion(project, version)).isEmpty();
  }

  @Test
  public void getForm_whenDecommissioningScheduleNotFound() {
    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.empty()
    );

    var form = decommissioningScheduleService.getForm(projectDetail);

    assertThat(form.getDecommissioningStartDateType()).isNull();
    assertThat(form.getExactDecommissioningStartDate()).isNull();
    assertThat(form.getEstimatedDecommissioningStartDate()).isNull();
    assertThat(form.getDecommissioningStartDateNotProvidedReason()).isNull();
    assertThat(form.getCessationOfProductionDateType()).isNull();
    assertThat(form.getExactCessationOfProductionDate()).isNull();
    assertThat(form.getEstimatedCessationOfProductionDate()).isNull();
    assertThat(form.getCessationOfProductionDateNotProvidedReason()).isNull();
  }

  @Test
  public void getForm_whenExactDecommissioningStartDateType() {
    var decommissioningSchedule = new DecommissioningSchedule();
    decommissioningSchedule.setDecommissioningStartDateType(DecommissioningStartDateType.EXACT);

    var decommissioningStartDate = LocalDate.now();
    decommissioningSchedule.setExactDecommissioningStartDate(decommissioningStartDate);

    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(decommissioningSchedule)
    );

    var form = decommissioningScheduleService.getForm(projectDetail);
    assertThat(form.getDecommissioningStartDateType()).isEqualTo(DecommissioningStartDateType.EXACT);
    assertThat(form.getExactDecommissioningStartDate()).isEqualTo(
        new ThreeFieldDateInput(decommissioningStartDate)
    );
  }

  @Test
  public void getForm_whenEstimatedDecommissioningStartDateType() {
    var decommissioningSchedule = new DecommissioningSchedule();
    decommissioningSchedule.setDecommissioningStartDateType(DecommissioningStartDateType.ESTIMATED);

    var decommissioningStartDateQuarter = Quarter.Q2;
    var decommissioningStartDateYear = 2020;
    decommissioningSchedule.setEstimatedDecommissioningStartDateQuarter(decommissioningStartDateQuarter);
    decommissioningSchedule.setEstimatedDecommissioningStartDateYear(decommissioningStartDateYear);

    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(decommissioningSchedule)
    );

    var form = decommissioningScheduleService.getForm(projectDetail);
    assertThat(form.getDecommissioningStartDateType()).isEqualTo(DecommissioningStartDateType.ESTIMATED);
    assertThat(form.getEstimatedDecommissioningStartDate()).isEqualTo(
        new QuarterYearInput(
            decommissioningStartDateQuarter,
            Integer.toString(decommissioningStartDateYear)
        )
    );
  }

  @Test
  public void getForm_whenUnknownDecommissioningStartDateType() {
    var decommissioningSchedule = new DecommissioningSchedule();
    decommissioningSchedule.setDecommissioningStartDateType(DecommissioningStartDateType.UNKNOWN);

    var decommissioningStartDateNotProvidedReason = "Test reason";
    decommissioningSchedule.setDecommissioningStartDateNotProvidedReason(decommissioningStartDateNotProvidedReason);

    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(decommissioningSchedule)
    );

    var form = decommissioningScheduleService.getForm(projectDetail);
    assertThat(form.getDecommissioningStartDateType()).isEqualTo(DecommissioningStartDateType.UNKNOWN);
    assertThat(form.getDecommissioningStartDateNotProvidedReason()).isEqualTo(decommissioningStartDateNotProvidedReason);
  }

  @Test
  public void getForm_whenExactCessationOfProductionDateType() {
    var decommissioningSchedule = new DecommissioningSchedule();
    decommissioningSchedule.setCessationOfProductionDateType(CessationOfProductionDateType.EXACT);

    var cessationOfProductionDate = LocalDate.now();
    decommissioningSchedule.setExactCessationOfProductionDate(cessationOfProductionDate);

    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(decommissioningSchedule)
    );

    var form = decommissioningScheduleService.getForm(projectDetail);
    assertThat(form.getCessationOfProductionDateType()).isEqualTo(CessationOfProductionDateType.EXACT);
    assertThat(form.getExactCessationOfProductionDate()).isEqualTo(
        new ThreeFieldDateInput(cessationOfProductionDate)
    );
  }

  @Test
  public void getForm_whenEstimatedCessationOfProductionDateType() {
    var decommissioningSchedule = new DecommissioningSchedule();
    decommissioningSchedule.setCessationOfProductionDateType(CessationOfProductionDateType.ESTIMATED);

    var cessationOfProductionDateQuarter = Quarter.Q2;
    var cessationOfProductionDateYear = 2020;
    decommissioningSchedule.setEstimatedCessationOfProductionDateQuarter(cessationOfProductionDateQuarter);
    decommissioningSchedule.setEstimatedCessationOfProductionDateYear(cessationOfProductionDateYear);

    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(decommissioningSchedule)
    );

    var form = decommissioningScheduleService.getForm(projectDetail);
    assertThat(form.getCessationOfProductionDateType()).isEqualTo(CessationOfProductionDateType.ESTIMATED);
    assertThat(form.getEstimatedCessationOfProductionDate()).isEqualTo(
        new QuarterYearInput(
            cessationOfProductionDateQuarter,
            Integer.toString(cessationOfProductionDateYear)
        )
    );
  }

  @Test
  public void getForm_whenUnknownCessationOfProductionDateType() {
    var decommissioningSchedule = new DecommissioningSchedule();
    decommissioningSchedule.setCessationOfProductionDateType(CessationOfProductionDateType.UNKNOWN);

    var cessationOfProductionDateNotProvidedReason = "Test reason";
    decommissioningSchedule.setCessationOfProductionDateNotProvidedReason(cessationOfProductionDateNotProvidedReason);

    when(decommissioningScheduleRepository.findByProjectDetail(projectDetail)).thenReturn(
        Optional.of(decommissioningSchedule)
    );

    var form = decommissioningScheduleService.getForm(projectDetail);
    assertThat(form.getCessationOfProductionDateType()).isEqualTo(CessationOfProductionDateType.UNKNOWN);
    assertThat(form.getCessationOfProductionDateNotProvidedReason()).isEqualTo(cessationOfProductionDateNotProvidedReason);
  }

  @Test
  public void getDecommissioningScheduleModelAndView() {
    var projectId = projectDetail.getProject().getId();
    var form = new DecommissioningScheduleForm();

    var modelAndView = decommissioningScheduleService.getDecommissioningScheduleModelAndView(
        projectId,
        form
    );

    assertThat(modelAndView.getViewName()).isEqualTo(DecommissioningScheduleService.TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("form", form),
        entry("pageName", DecommissioningScheduleController.PAGE_NAME),
        entry("exactDecommissioningStartDateType",
            DecommissioningStartDateType.getEntryAsMap(DecommissioningStartDateType.EXACT)),
        entry("estimatedDecommissioningStartDateType",
            DecommissioningStartDateType.getEntryAsMap(DecommissioningStartDateType.ESTIMATED)),
        entry("unknownDecommissioningStartDateType",
            DecommissioningStartDateType.getEntryAsMap(DecommissioningStartDateType.UNKNOWN)),
        entry("exactCessationOfProductionDateType",
            CessationOfProductionDateType.getEntryAsMap(CessationOfProductionDateType.EXACT)),
        entry("estimatedCessationOfProductionDateType",
            CessationOfProductionDateType.getEntryAsMap(CessationOfProductionDateType.ESTIMATED)),
        entry("unknownCessationOfProductionDateType",
            CessationOfProductionDateType.getEntryAsMap(CessationOfProductionDateType.UNKNOWN)),
        entry("quarters", Quarter.getAllAsMap())
    );

    verify(breadcrumbService, times(1)).fromTaskList(projectId, modelAndView, DecommissioningScheduleController.PAGE_NAME);
  }

  @Test
  public void isComplete_whenValid_thenTrue() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();
    var projectDetail = decommissioningSchedule.getProjectDetail();

    when(validationService.validate(any(), any(), any(ValidationType.class))).thenAnswer(invocation -> invocation.getArguments()[1]);

    var isComplete = decommissioningScheduleService.isComplete(projectDetail);
    assertThat(isComplete).isTrue();
  }

  @Test
  public void isComplete_whenInvalid_thenFalse() {
    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule();
    decommissioningSchedule.setDecommissioningStartDateType(DecommissioningStartDateType.ESTIMATED);
    decommissioningSchedule.setEstimatedDecommissioningStartDateQuarter(null);

    var projectDetail = decommissioningSchedule.getProjectDetail();

    doAnswer(invocation -> {
      BindingResult result = invocation.getArgument(1);
      result.addError(new FieldError("Error", "ErrorMessage", "default message"));
      return result;
    }).when(validationService).validate(any(), any(), any(ValidationType.class));

    var isComplete = decommissioningScheduleService.isComplete(projectDetail);
    assertThat(isComplete).isFalse();
  }

  @Test
  public void canShowInTaskList_whenNotInfrastructureProject_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

    assertThat(decommissioningScheduleService.canShowInTaskList(projectDetail)).isFalse();

    verify(projectInformationService, never()).isDecomRelated(projectDetail);
  }

  @Test
  public void canShowInTaskList_whenInfrastructureProjectAndDecomRelated_thenTrue() {
    when(projectInformationService.isDecomRelated(projectDetail)).thenReturn(true);

    assertThat(decommissioningScheduleService.canShowInTaskList(projectDetail)).isTrue();
  }

  @Test
  public void canShowInTaskList_whenInfrastructureProjectAndNotDecomRelated_thenFalse() {
    when(projectInformationService.isDecomRelated(projectDetail)).thenReturn(false);

    assertThat(decommissioningScheduleService.canShowInTaskList(projectDetail)).isFalse();
  }

  @Test
  public void removeSectionData() {
    decommissioningScheduleService.removeSectionData(projectDetail);

    verify(decommissioningScheduleRepository, times(1)).deleteByProjectDetail(projectDetail);
  }

  @Test
  public void copySectionData() {
    var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    var fromDecommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule(fromProjectDetail);
    when(decommissioningScheduleRepository.findByProjectDetail(fromProjectDetail))
        .thenReturn(Optional.of(fromDecommissioningSchedule));

    decommissioningScheduleService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntityAndSetNewParent(
        fromDecommissioningSchedule,
        toProjectDetail,
        DecommissioningSchedule.class
    );
  }

  @Test
  public void alwaysCopySectionData_verifyFalse() {
    assertThat(decommissioningScheduleService.alwaysCopySectionData(projectDetail)).isFalse();
  }

  @Test
  public void allowSectionDataCleanUp_verifyIsTrue() {
    final var allowSectionDateCleanUp = decommissioningScheduleService.allowSectionDataCleanUp(projectDetail);
    assertThat(allowSectionDateCleanUp).isTrue();
  }
}
