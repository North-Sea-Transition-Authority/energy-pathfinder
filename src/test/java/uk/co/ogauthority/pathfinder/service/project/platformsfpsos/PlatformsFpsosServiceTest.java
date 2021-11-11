package uk.co.ogauthority.pathfinder.service.project.platformsfpsos;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkFacility;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.PlatformFpsoInfrastructureType;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.SubstructureRemovalPremise;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoForm;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoFormValidator;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.platformsfpsos.PlatformFpsoRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFacilitiesService;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.DevUkTestUtil;
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
  public void createPlatformFpso_whenPlatformFromList_assertEntityProperties() {

    final var selectedPlatform = PlatformFpsoTestUtil.FACILITY;

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatform();
    form.setPlatformStructure(selectedPlatform.getSelectionId());

    when(devUkFacilitiesService.getOrError(selectedPlatform.getId())).thenReturn(selectedPlatform);

    final var platformFpso = platformsFpsosService.createPlatformFpso(detail, form);

    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);
    assertThat(platformFpso.getStructure()).isEqualTo(PlatformFpsoTestUtil.FACILITY);
    assertThat(platformFpso.getManualStructureName()).isNull();

    assertCommonFieldsMatch(platformFpso, form);

    assertFpsoSpecificQuestionsAreNull(platformFpso);

    verify(platformFpsoRepository, times(1)).save(platformFpso);
  }

  @Test
  public void createPlatformFpso_whenPlatformNotFromList_assertEntityProperties() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatform_manualStructure();

    final var platformFpso = platformsFpsosService.createPlatformFpso(detail, form);

    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);
    assertThat(platformFpso.getStructure()).isNull();
    assertThat(platformFpso.getManualStructureName()).isEqualTo(SearchSelectorService.removePrefix(form.getPlatformStructure()));

    assertCommonFieldsMatch(platformFpso, form);

    assertFpsoSpecificQuestionsAreNull(platformFpso);

    verify(platformFpsoRepository, times(1)).save(platformFpso);
  }

  @Test
  public void createPlatformFpso_whenFpsoFromList_assertEntityProperties() {

    final var selectedFpso = PlatformFpsoTestUtil.FACILITY;

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpsoAndSubstructuresToBeRemoved();

    when(devUkFacilitiesService.getOrError(selectedFpso.getId())).thenReturn(selectedFpso);

    final var platformFpso = platformsFpsosService.createPlatformFpso(detail, form);

    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);
    assertThat(platformFpso.getStructure()).isEqualTo(selectedFpso);
    assertThat(platformFpso.getManualStructureName()).isNull();

    assertCommonFieldsMatch(platformFpso, form);

    assertFpsoSpecificProperties(form, platformFpso);

    verify(platformFpsoRepository, times(1)).save(platformFpso);
  }

  @Test
  public void createPlatformFpso_whenFpsoNotFromList_assertEntityProperties() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();

    final var platformFpso = platformsFpsosService.createPlatformFpso(detail, form);

    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);
    assertThat(platformFpso.getStructure()).isNull();
    assertThat(platformFpso.getManualStructureName()).isEqualTo(SearchSelectorService.removePrefix(form.getFpsoStructure()));

    assertCommonFieldsMatch(platformFpso, form);

    assertFpsoSpecificProperties(form, platformFpso);

    verify(platformFpsoRepository, times(1)).save(platformFpso);
  }

  @Test
  public void createPlatformFpso_whenFpsoAndNoSubstructuresToRemove_assertEntityProperties() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();
    form.setSubstructureExpectedToBeRemoved(false);

    // set the hidden substructure properties to ensure they are not mapped to the entity
    form.setSubstructureRemovalMass(10);
    form.setSubstructureRemovalPremise(SubstructureRemovalPremise.FULL);
    form.setSubstructureRemovalYears(new MinMaxDateInput("2021", "2025"));

    final var platformFpso = platformsFpsosService.createPlatformFpso(detail, form);

    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);

    assertCommonFieldsMatch(platformFpso, form);

    assertFpsoSpecificProperties(form, platformFpso);

    verify(platformFpsoRepository, times(1)).save(platformFpso);
  }

  @Test
  public void createPlatformFpso_whenFpsoAndSubstructuresToRemove_assertEntityProperties() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();
    form.setSubstructureExpectedToBeRemoved(true);
    form.setSubstructureRemovalMass(10);
    form.setSubstructureRemovalPremise(SubstructureRemovalPremise.FULL);
    form.setSubstructureRemovalYears(new MinMaxDateInput("2021", "2025"));

    final var platformFpso = platformsFpsosService.createPlatformFpso(detail, form);

    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);

    assertCommonFieldsMatch(platformFpso, form);

    assertFpsoSpecificProperties(form, platformFpso);

    verify(platformFpsoRepository, times(1)).save(platformFpso);
  }

  @Test
  public void updatePlatformFpso_whenPlatformFromList_assertEntityProperties() {

    final var selectedPlatform = PlatformFpsoTestUtil.FACILITY;

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatform();
    form.setPlatformStructure(selectedPlatform.getSelectionId());

    when(devUkFacilitiesService.getOrError(selectedPlatform.getId())).thenReturn(selectedPlatform);

    final var platformFpso = platformsFpsosService.updatePlatformFpso(detail, new PlatformFpso(), form);

    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);
    assertThat(platformFpso.getStructure()).isEqualTo(PlatformFpsoTestUtil.FACILITY);
    assertThat(platformFpso.getManualStructureName()).isNull();

    assertCommonFieldsMatch(platformFpso, form);

    assertFpsoSpecificQuestionsAreNull(platformFpso);

    verify(platformFpsoRepository, times(1)).save(platformFpso);
  }

  @Test
  public void updatePlatformFpso_whenPlatformNotFromList_assertEntityProperties() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatform_manualStructure();

    final var platformFpso = platformsFpsosService.updatePlatformFpso(detail, new PlatformFpso(), form);

    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);
    assertThat(platformFpso.getStructure()).isNull();
    assertThat(platformFpso.getManualStructureName()).isEqualTo(SearchSelectorService.removePrefix(form.getPlatformStructure()));

    assertCommonFieldsMatch(platformFpso, form);

    assertFpsoSpecificQuestionsAreNull(platformFpso);

    verify(platformFpsoRepository, times(1)).save(platformFpso);
  }

  @Test
  public void updatePlatformFpso_whenFpsoFromList_assertEntityProperties() {

    final var selectedFpso = PlatformFpsoTestUtil.FACILITY;

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpsoAndSubstructuresToBeRemoved();

    when(devUkFacilitiesService.getOrError(selectedFpso.getId())).thenReturn(selectedFpso);

    final var platformFpso = platformsFpsosService.updatePlatformFpso(detail, new PlatformFpso(), form);

    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);
    assertThat(platformFpso.getStructure()).isEqualTo(selectedFpso);
    assertThat(platformFpso.getManualStructureName()).isNull();

    assertCommonFieldsMatch(platformFpso, form);

    assertFpsoSpecificProperties(form, platformFpso);

    verify(platformFpsoRepository, times(1)).save(platformFpso);
  }

  @Test
  public void updatePlatformFpso_whenFpsoNotFromList_assertEntityProperties() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();

    final var platformFpso = platformsFpsosService.updatePlatformFpso(detail, new PlatformFpso(), form);

    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);
    assertThat(platformFpso.getStructure()).isNull();
    assertThat(platformFpso.getManualStructureName()).isEqualTo(SearchSelectorService.removePrefix(form.getFpsoStructure()));

    assertCommonFieldsMatch(platformFpso, form);

    assertFpsoSpecificProperties(form, platformFpso);

    verify(platformFpsoRepository, times(1)).save(platformFpso);
  }

  @Test
  public void updatePlatformFpso_whenFpsoAndNoSubstructuresToRemove_assertEntityProperties() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();
    form.setSubstructureExpectedToBeRemoved(false);

    // set the hidden substructure properties to ensure they are not mapped to the entity
    form.setSubstructureRemovalMass(10);
    form.setSubstructureRemovalPremise(SubstructureRemovalPremise.FULL);
    form.setSubstructureRemovalYears(new MinMaxDateInput("2021", "2025"));

    final var platformFpso = platformsFpsosService.updatePlatformFpso(detail, new PlatformFpso(), form);

    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);

    assertCommonFieldsMatch(platformFpso, form);

    assertFpsoSpecificProperties(form, platformFpso);

    verify(platformFpsoRepository, times(1)).save(platformFpso);
  }

  @Test
  public void updatePlatformFpso_whenFpsoAndSubstructuresToRemove_assertEntityProperties() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();
    form.setSubstructureExpectedToBeRemoved(true);
    form.setSubstructureRemovalMass(10);
    form.setSubstructureRemovalPremise(SubstructureRemovalPremise.FULL);
    form.setSubstructureRemovalYears(new MinMaxDateInput("2021", "2025"));

    final var platformFpso = platformsFpsosService.updatePlatformFpso(detail, new PlatformFpso(), form);

    assertThat(platformFpso.getProjectDetail()).isEqualTo(detail);

    assertCommonFieldsMatch(platformFpso, form);

    assertFpsoSpecificProperties(form, platformFpso);

    verify(platformFpsoRepository, times(1)).save(platformFpso);
  }

  @Test
  public void getPlatformsFpsosByProjectDetail_whenPlatformsFpsos_thenReturnPopulatedList() {
    var platformsFpsos = List.of(
        PlatformFpsoTestUtil.getPlatformFpso_withPlatform(detail),
        PlatformFpsoTestUtil.getPlatformFpso_withPlatform_manualStructure(detail)
    );

    when(platformFpsoRepository.findAllByProjectDetailOrderByIdAsc(detail)).thenReturn(platformsFpsos);

    assertThat(platformsFpsosService.getPlatformsFpsosByProjectDetail(detail)).isEqualTo(platformsFpsos);
  }

  @Test
  public void getPlatformsFpsosByProjectDetail_whenNoPlatformsFpsos_thenReturnEmptyList() {
    when(platformFpsoRepository.findAllByProjectDetailOrderByIdAsc(detail)).thenReturn(Collections.emptyList());

    assertThat(platformsFpsosService.getPlatformsFpsosByProjectDetail(detail)).isEmpty();
  }

  @Test
  public void getPlatformsFpsosByProjectAndVersion_whenPlatformsFpsos_thenReturnPopulatedList() {
    var project = detail.getProject();
    var version = detail.getVersion();
    var platformsFpsos = List.of(
        PlatformFpsoTestUtil.getPlatformFpso_withPlatform(detail),
        PlatformFpsoTestUtil.getPlatformFpso_withPlatform_manualStructure(detail)
    );

    when(platformFpsoRepository.findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version))
        .thenReturn(platformsFpsos);

    assertThat(platformsFpsosService.getPlatformsFpsosByProjectAndVersion(project, version)).isEqualTo(platformsFpsos);
  }

  @Test
  public void getPlatformsFpsosByProjectAndVersion_whenNoPlatformsFpsos_thenReturnEmptyList() {
    var project = detail.getProject();
    var version = detail.getVersion();

    when(platformFpsoRepository.findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version))
        .thenReturn(Collections.emptyList());

    assertThat(platformsFpsosService.getPlatformsFpsosByProjectAndVersion(project, version)).isEmpty();
  }

  @Test
  public void getForm_platform() {
    var platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withPlatform(detail);
    var form = platformsFpsosService.getForm(platformFpso);
    assertThat(form.getPlatformStructure()).isEqualTo(platformFpso.getStructure().getId().toString());
    assertThat(form.getFpsoStructure()).isNull();
    assertCommonFormFieldsMatch(form, platformFpso);
  }

  @Test
  public void getForm_platformAndManualEntry() {
    var platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withPlatform(detail);
    var newStructure = "new structure";
    platformFpso.setManualStructureName(newStructure);
    platformFpso.setStructure(null);
    var form = platformsFpsosService.getForm(platformFpso);
    assertThat(form.getPlatformStructure()).isEqualTo(SearchSelectorService.getValueWithManualEntryPrefix(platformFpso.getManualStructureName()));
    assertThat(form.getFpsoStructure()).isNull();
    assertCommonFormFieldsMatch(form, platformFpso);
  }

  @Test
  public void getForm_fpso() {
    var platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withFpsoAndSubstructuresRemoved(detail);
    var form = platformsFpsosService.getForm(platformFpso);
    assertThat(form.getPlatformStructure()).isNull();
    assertThat(form.getFpsoStructure()).isEqualTo(platformFpso.getStructure().getId().toString());
    assertCommonFormFieldsMatch(form, platformFpso);
  }

  @Test
  public void getForm_fpsoAndManualEntry() {
    var newStructure = "new structure";
    var existingPlatformFpso = PlatformFpsoTestUtil.getPlatformFpso_withFpsoAndSubstructuresRemoved(detail);
    existingPlatformFpso.setManualStructureName(newStructure);
    existingPlatformFpso.setStructure(null);
    var form = platformsFpsosService.getForm(existingPlatformFpso);
    assertThat(form.getPlatformStructure()).isNull();
    assertThat(form.getFpsoStructure()).isEqualTo(SearchSelectorService.getValueWithManualEntryPrefix(existingPlatformFpso.getManualStructureName()));
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
    assertThat(platformFpso.getFuturePlans()).isEqualTo(form.getFuturePlans());
    assertThat(platformFpso.getSubstructuresExpectedToBeRemoved()).isEqualTo(form.getSubstructureExpectedToBeRemoved());

    if (BooleanUtils.isTrue(form.getSubstructureExpectedToBeRemoved())) {
      assertThat(platformFpso.getSubstructureRemovalPremise()).isEqualTo(form.getSubstructureRemovalPremise());
      assertThat(platformFpso.getSubstructureRemovalMass()).isEqualTo(form.getSubstructureRemovalMass());
      assertThat(platformFpso.getSubStructureRemovalEarliestYear()).isEqualTo(form.getSubstructureRemovalYears().getMinYear());
      assertThat(platformFpso.getSubStructureRemovalLatestYear()).isEqualTo(form.getSubstructureRemovalYears().getMaxYear());
    } else {
      assertThat(platformFpso.getSubstructureRemovalPremise()).isNull();
      assertThat(platformFpso.getSubstructureRemovalMass()).isNull();
      assertThat(platformFpso.getSubStructureRemovalEarliestYear()).isNull();
      assertThat(platformFpso.getSubStructureRemovalLatestYear()).isNull();
    }
  }

  private void assertCommonFormFieldsMatch(PlatformFpsoForm form, PlatformFpso platformFpso) {
    assertThat(form.getFpsoType()).isEqualTo(platformFpso.getFpsoType());
    assertThat(form.getFpsoDimensions()).isEqualTo(platformFpso.getFpsoDimensions());
    assertThat(form.getTopsideFpsoMass()).isEqualTo(platformFpso.getTopsideFpsoMass());
    assertThat(form.getTopsideRemovalYears().getMinYear()).isEqualTo(platformFpso.getEarliestRemovalYear());
    assertThat(form.getTopsideRemovalYears().getMaxYear()).isEqualTo(platformFpso.getLatestRemovalYear());
    assertThat(form.getSubstructureExpectedToBeRemoved()).isEqualTo(platformFpso.getSubstructuresExpectedToBeRemoved());
    assertThat(form.getSubstructureRemovalPremise()).isEqualTo(platformFpso.getSubstructureRemovalPremise());
    assertThat(form.getSubstructureRemovalMass()).isEqualTo(platformFpso.getSubstructureRemovalMass());
    assertThat(form.getSubstructureRemovalYears().getMinYear()).isEqualTo(platformFpso.getSubStructureRemovalEarliestYear());
    assertThat(form.getSubstructureRemovalYears().getMaxYear()).isEqualTo(platformFpso.getSubStructureRemovalLatestYear());
    assertThat(form.getFuturePlans()).isEqualTo(platformFpso.getFuturePlans());
  }

  @Test
  public void getPreselectedPlatformStructure_whenPlatformAndPlatformStructureIsNull_thenEmptyMap() {
    var form = new PlatformFpsoForm();
    form.setInfrastructureType(PlatformFpsoInfrastructureType.PLATFORM);
    form.setPlatformStructure(null);

    var result = platformsFpsosService.getPreselectedPlatformStructure(form);
    assertThat(result).isEmpty();
  }

  @Test
  public void getPreselectedPlatformStructure_whenPlatformAndPlatformStructureIsManualEntry_thenManualEntryResult() {
    final String manualFormValue = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my manual platform structure";
    var form = new PlatformFpsoForm();
    form.setInfrastructureType(PlatformFpsoInfrastructureType.PLATFORM);
    form.setPlatformStructure(manualFormValue);

    var result = platformsFpsosService.getPreselectedPlatformStructure(form);
    assertThat(result).containsExactly(
        entry(manualFormValue, SearchSelectorService.removePrefix(manualFormValue))
    );
  }

  @Test
  public void getPreselectedPlatformStructure_whenPlatformAndPlatformStructureIsFromListEntry_thenFromListResult() {
    final Integer fromListSelectionId = 1234;
    final DevUkFacility facility = DevUkTestUtil.getDevUkFacility(fromListSelectionId, "A DevUK facility");
    var form = new PlatformFpsoForm();
    form.setInfrastructureType(PlatformFpsoInfrastructureType.PLATFORM);
    form.setPlatformStructure(String.valueOf(fromListSelectionId));

    when(devUkFacilitiesService.getOrError(fromListSelectionId))
        .thenReturn(facility);

    var result = platformsFpsosService.getPreselectedPlatformStructure(form);
    assertThat(result).containsExactly(
        entry(facility.getSelectionId(), facility.getSelectionText())
    );
  }

  @Test
  public void getPreselectedPlatformStructure_whenNotPlatform_thenEmptyMap() {
    var form = new PlatformFpsoForm();
    form.setInfrastructureType(PlatformFpsoInfrastructureType.FPSO);

    var result = platformsFpsosService.getPreselectedPlatformStructure(form);
    assertThat(result).isEmpty();
  }

  @Test
  public void getPreselectedFpsoStructure_whenFpsoAndFpsoStructureIsNull_thenEmptyMap() {
    var form = new PlatformFpsoForm();
    form.setInfrastructureType(PlatformFpsoInfrastructureType.FPSO);
    form.setFpsoStructure(null);

    var result = platformsFpsosService.getPreselectedFpsoStructure(form);
    assertThat(result).isEmpty();
  }

  @Test
  public void getPreselectedFpsoStructure_whenFpsoAndFpsoStructureIsManualEntry_thenManualEntryResult() {
    final String manualFormValue = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my manual platform structure";
    var form = new PlatformFpsoForm();
    form.setInfrastructureType(PlatformFpsoInfrastructureType.FPSO);
    form.setFpsoStructure(manualFormValue);

    var result = platformsFpsosService.getPreselectedFpsoStructure(form);
    assertThat(result).containsExactly(
        entry(manualFormValue, SearchSelectorService.removePrefix(manualFormValue))
    );
  }

  @Test
  public void getPreselectedFpsoStructure_whenFpsoAndFpsoStructureIsFromListEntry_thenFromListResult() {
    final Integer fromListSelectionId = 1234;
    final DevUkFacility facility = DevUkTestUtil.getDevUkFacility(fromListSelectionId, "A DevUK facility");
    var form = new PlatformFpsoForm();
    form.setInfrastructureType(PlatformFpsoInfrastructureType.FPSO);
    form.setFpsoStructure(String.valueOf(fromListSelectionId));

    when(devUkFacilitiesService.getOrError(fromListSelectionId))
        .thenReturn(facility);

    var result = platformsFpsosService.getPreselectedFpsoStructure(form);
    assertThat(result).containsExactly(
        entry(facility.getSelectionId(), facility.getSelectionText())
    );
  }

  @Test
  public void getPreselectedPlatformStructure_whenNotFpso_thenEmptyMap() {
    var form = new PlatformFpsoForm();
    form.setInfrastructureType(PlatformFpsoInfrastructureType.PLATFORM);

    var result = platformsFpsosService.getPreselectedFpsoStructure(form);
    assertThat(result).isEmpty();
  }

  @Test
  public void canShowInTaskList_true() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.PLATFORM_FPSO)).thenReturn(true);
    assertThat(platformsFpsosService.canShowInTaskList(detail)).isTrue();
  }

  @Test
  public void canShowInTaskList_false() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.PLATFORM_FPSO)).thenReturn(false);
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

    final var platformsFpsos = List.of(PlatformFpsoTestUtil.getPlatformFpso_withPlatform(fromProjectDetail));
    when(platformFpsoRepository.findAllByProjectDetailOrderByIdAsc(fromProjectDetail)).thenReturn(platformsFpsos);

    platformsFpsosService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        platformsFpsos,
        toProjectDetail,
        PlatformFpso.class
    );
  }

  @Test
  public void getSupportedProjectTypes_verifyInfrastructure() {
    assertThat(platformsFpsosService.getSupportedProjectTypes()).containsExactly(ProjectType.INFRASTRUCTURE);
  }
  private void assertFpsoSpecificQuestionsAreNull(PlatformFpso sourceEntity) {
    assertThat(sourceEntity.getFpsoType()).isNull();
    assertThat(sourceEntity.getFpsoDimensions()).isNull();
  }

  private void assertFpsoSpecificProperties(PlatformFpsoForm sourceForm, PlatformFpso destinationEntity) {
    assertThat(destinationEntity.getFpsoType()).isEqualTo(sourceForm.getFpsoType());
    assertThat(destinationEntity.getFpsoDimensions()).isEqualTo(sourceForm.getFpsoDimensions());
  }

}
