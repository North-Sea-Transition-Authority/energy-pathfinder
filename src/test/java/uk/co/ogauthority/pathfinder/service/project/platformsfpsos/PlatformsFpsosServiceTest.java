package uk.co.ogauthority.pathfinder.service.project.platformsfpsos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoForm;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.platformsfpsos.PlatformFpsoRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFacilitiesService;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class PlatformsFpsosServiceTest {
  @Mock
  private PlatformFpsoRepository platformFpsoRepository;

  @Mock
  private DevUkFacilitiesService devUkFacilitiesService;

  @Mock
  private PlatformFpsoFormValidator platformFpsoFormValidator;

  @Mock
  private ValidationService validationService;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private PlatformsFpsosService platformsFpsosService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final PlatformFpso platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withSubstructuresRemoved(detail);

  @Before
  public void setUp() {
    platformsFpsosService = new PlatformsFpsosService(
        platformFpsoRepository,
        devUkFacilitiesService,
        new SearchSelectorService(),
        platformFpsoFormValidator,
        validationService,
        projectSetupService,
        entityDuplicationService
    );

    when(platformFpsoRepository.save(any(PlatformFpso.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createPlatformFpso() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withSubstructuresToBeRemoved();
    when(devUkFacilitiesService.getOrError(any())).thenReturn(PlatformFpsoTestUtil.FACILITY);
    var platformFpso = platformsFpsosService.createPlatformFpso(detail, form);
    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);
    assertThat(platformFpso.getStructure()).isEqualTo(PlatformFpsoTestUtil.FACILITY);
    assertThat(platformFpso.getManualStructureName()).isNull();
    assertCommonFieldsMatch(platformFpso, form);
  }

  @Test
  public void createPlatformFpso_noSubStructureExpectedToBeRemoved() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_noSubstructuresToBeRemoved();
    when(devUkFacilitiesService.getOrError(any())).thenReturn(PlatformFpsoTestUtil.FACILITY);
    var platformFpso = platformsFpsosService.createPlatformFpso(detail, form);
    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);
    assertThat(platformFpso.getStructure()).isEqualTo(PlatformFpsoTestUtil.FACILITY);
    assertThat(platformFpso.getManualStructureName()).isNull();
    assertCommonFieldsMatch(platformFpso, form);
  }

  @Test
  public void createPlatformFpso_manualStructure() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withSubstructuresToBeRemoved_manualStructure();
    var platformFpso = platformsFpsosService.createPlatformFpso(detail, form);
    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);
    assertThat(platformFpso.getStructure()).isNull();
    assertThat(platformFpso.getManualStructureName()).isEqualTo(SearchSelectorService.removePrefix(form.getStructure()));
    assertCommonFieldsMatch(platformFpso, form);
  }

  @Test
  public void createPlatformFpso_noSubStructureExpectedToBeRemoved_manualStructure() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_noSubstructuresToBeRemoved_manualStructure();
    var platformFpso = platformsFpsosService.createPlatformFpso(detail, form);
    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);
    assertThat(platformFpso.getStructure()).isNull();
    assertThat(platformFpso.getManualStructureName()).isEqualTo(SearchSelectorService.removePrefix(form.getStructure()));
    assertCommonFieldsMatch(platformFpso, form);
  }

  @Test
  public void updatePlatformFpso() {
    var newStructure = "new structure";
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withSubstructuresToBeRemoved();
    form.setStructure(SearchSelectorService.getValueWithManualEntryPrefix(newStructure));
    var existingPlatformFpso = platformFpso;
    platformsFpsosService.updatePlatformFpso(detail, existingPlatformFpso, form);
    assertThat(existingPlatformFpso.getManualStructureName()).isEqualTo(newStructure);
    assertThat(existingPlatformFpso.getStructure()).isNull();
    assertCommonFieldsMatch(existingPlatformFpso, form);
  }

  @Test
  public void updatePlatformFpso_removeSubstructures() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_noSubstructuresToBeRemoved();
    var existingPlatformFpso = platformFpso;
    platformsFpsosService.updatePlatformFpso(detail, existingPlatformFpso, form);
    assertCommonFieldsMatch(existingPlatformFpso, form);
  }

  @Test
  public void getForm() {
    var form = platformsFpsosService.getForm(platformFpso);
    assertThat(form.getStructure()).isEqualTo(platformFpso.getStructure().getId().toString());
    assertCommonFormFieldsMatch(form, platformFpso);
  }

  @Test
  public void getForm_manualEntry() {
    var newStructure = "new structure";
    var existingPlatformFpso = platformFpso;
    existingPlatformFpso.setManualStructureName(newStructure);
    existingPlatformFpso.setStructure(null);
    var form = platformsFpsosService.getForm(existingPlatformFpso);
    assertThat(form.getStructure()).isEqualTo(SearchSelectorService.getValueWithManualEntryPrefix(existingPlatformFpso.getManualStructureName()));
    assertCommonFormFieldsMatch(form, existingPlatformFpso);
  }

  @Test
  public void validate_whenPartial() {
    var form = new PlatformFpsoForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    platformsFpsosService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );

    verify(validationService).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_whenFull() {
    var form = new PlatformFpsoForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    platformsFpsosService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService).validate(form, bindingResult, ValidationType.FULL);
  }

  private void assertCommonFieldsMatch(PlatformFpso platformFpso, PlatformFpsoForm form) {
    assertThat(platformFpso.getTopsideFpsoMass()).isEqualTo(form.getTopsideFpsoMass());
    assertThat(platformFpso.getEarliestRemovalYear()).isEqualTo(form.getTopsideRemovalYears().getMinYear());
    assertThat(platformFpso.getLatestRemovalYear()).isEqualTo(form.getTopsideRemovalYears().getMaxYear());
    assertThat(platformFpso.getSubstructuresExpectedToBeRemoved()).isEqualTo(form.getSubstructureExpectedToBeRemoved());
    if (form.getSubstructureExpectedToBeRemoved()) {
      assertThat(platformFpso.getSubstructureRemovalPremise()).isEqualTo(form.getSubstructureRemovalPremise());
      assertThat(platformFpso.getSubstructureRemovalMass()).isEqualTo(form.getSubstructureRemovalMass());
      assertThat(platformFpso.getSubStructureRemovalEarliestYear()).isEqualTo(form.getSubstructureRemovalYears().getMinYear());
      assertThat(platformFpso.getSubStructureRemovalLatestYear()).isEqualTo(form.getSubstructureRemovalYears().getMaxYear());
    }
    if (!form.getSubstructureExpectedToBeRemoved()) {
      assertThat(platformFpso.getSubstructureRemovalPremise()).isNull();
      assertThat(platformFpso.getSubstructureRemovalMass()).isNull();
      assertThat(platformFpso.getSubStructureRemovalEarliestYear()).isNull();
      assertThat(platformFpso.getSubStructureRemovalLatestYear()).isNull();
    }
    assertThat(platformFpso.getFpsoType()).isEqualTo(form.getFpsoType());
    assertThat(platformFpso.getFpsoDimensions()).isEqualTo(form.getFpsoDimensions());
    assertThat(platformFpso.getFuturePlans()).isEqualTo(form.getFuturePlans());
  }

  private void assertCommonFormFieldsMatch(PlatformFpsoForm form, PlatformFpso platformFpso) {
    assertThat(form.getTopsideFpsoMass()).isEqualTo(platformFpso.getTopsideFpsoMass());
    assertThat(form.getTopsideRemovalYears().getMinYear()).isEqualTo(platformFpso.getEarliestRemovalYear());
    assertThat(form.getTopsideRemovalYears().getMaxYear()).isEqualTo(platformFpso.getLatestRemovalYear());
    assertThat(form.getSubstructureExpectedToBeRemoved()).isEqualTo(platformFpso.getSubstructuresExpectedToBeRemoved());
    assertThat(form.getSubstructureRemovalPremise()).isEqualTo(platformFpso.getSubstructureRemovalPremise());
    assertThat(form.getSubstructureRemovalMass()).isEqualTo(platformFpso.getSubstructureRemovalMass());
    assertThat(form.getSubstructureRemovalYears().getMinYear()).isEqualTo(platformFpso.getSubStructureRemovalEarliestYear());
    assertThat(form.getSubstructureRemovalYears().getMaxYear()).isEqualTo(platformFpso.getSubStructureRemovalLatestYear());
    assertThat(form.getFpsoType()).isEqualTo(platformFpso.getFpsoType());
    assertThat(form.getFpsoDimensions()).isEqualTo(platformFpso.getFpsoDimensions());
    assertThat(form.getFuturePlans()).isEqualTo(platformFpso.getFuturePlans());
  }

  @Test
  public void canShowInTaskList_true() {
    when(projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.PLATFORM_FPSO)).thenReturn(true);
    assertThat(platformsFpsosService.canShowInTaskList(detail)).isTrue();
  }

  @Test
  public void canShowInTaskList_false() {
    when(projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.PLATFORM_FPSO)).thenReturn(false);
    assertThat(platformsFpsosService.canShowInTaskList(detail)).isFalse();
  }

  @Test
  public void removeSectionData_verifyInteractions() {
    platformsFpsosService.removeSectionData(detail);

    verify(platformFpsoRepository, times(1)).deleteAllByProjectDetail(detail);
  }

  @Test
  public void copySectionData_verifyDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    final var platformsFpsos = List.of(PlatformFpsoTestUtil.getPlatformFpso_NoSubstructuresRemoved(fromProjectDetail));
    when(platformFpsoRepository.findAllByProjectDetailOrderByIdAsc(fromProjectDetail)).thenReturn(platformsFpsos);

    platformsFpsosService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        platformsFpsos,
        toProjectDetail,
        PlatformFpso.class
    );
  }

}
