package uk.co.ogauthority.pathfinder.service.project.integratedrig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.integratedrig.IntegratedRig;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.integratedrig.IntegratedRigForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.integratedrig.IntegratedRigRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFacilitiesService;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.DevUkTestUtil;
import uk.co.ogauthority.pathfinder.testutil.IntegratedRigTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectFormSectionServiceTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class IntegratedRigServiceTest {

  private static final Integer INTEGRATED_RIG_ID = 1;

  private IntegratedRigService integratedRigService;

  @Mock
  private DevUkFacilitiesService devUkFacilitiesService;

  @Mock
  private IntegratedRigRepository integratedRigRepository;

  @Mock
  private ValidationService validationService;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private ProjectDetail projectDetail;

  @Before
  public void setup() {
    var searchSelectorService = new SearchSelectorService();
    integratedRigService = new IntegratedRigService(
        devUkFacilitiesService,
        integratedRigRepository,
        searchSelectorService,
        validationService,
        projectSetupService,
        entityDuplicationService
    );

    projectDetail = ProjectUtil.getProjectDetails();

    when(integratedRigRepository.save(any(IntegratedRig.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  private void checkCommonIntegratedRigFormFields(IntegratedRigForm form,
                                                  IntegratedRig integratedRig) {
    assertThat(form.getName()).isEqualTo(integratedRig.getName());
    assertThat(form.getStatus()).isEqualTo(integratedRig.getStatus());
    assertThat(form.getIntentionToReactivate()).isEqualTo(integratedRig.getIntentionToReactivate());
  }

  private void checkCommonIntegratedRigEntityFields(IntegratedRig integratedRig,
                                                    IntegratedRigForm form) {
    assertThat(integratedRig.getName()).isEqualTo(form.getName());
    assertThat(integratedRig.getStatus()).isEqualTo(form.getStatus());
    assertThat(integratedRig.getIntentionToReactivate()).isEqualTo(form.getIntentionToReactivate());
  }

  private void assertCorrectValidation(IntegratedRigForm form, ValidationType validationType) {

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    integratedRigService.validate(
        form,
        bindingResult,
        validationType
    );

    verify(validationService, times(1)).validate(form, bindingResult, validationType);
  }

  @Test
  public void getForm_withStructureFromList() {
    var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withDevUkFacility();

    when(integratedRigRepository.findByIdAndProjectDetail(INTEGRATED_RIG_ID, projectDetail))
        .thenReturn(Optional.of(integratedRig));

    var form = integratedRigService.getForm(INTEGRATED_RIG_ID, projectDetail);

    checkCommonIntegratedRigFormFields(form, integratedRig);

    assertThat(form.getStructure()).isEqualTo(String.valueOf(integratedRig.getFacility().getId()));
  }

  @Test
  public void getForm_withManualEntryStructure() {
    var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withManualFacility();

    when(integratedRigRepository.findByIdAndProjectDetail(INTEGRATED_RIG_ID, projectDetail))
        .thenReturn(Optional.of(integratedRig));

    var form = integratedRigService.getForm(INTEGRATED_RIG_ID, projectDetail);

    checkCommonIntegratedRigFormFields(form, integratedRig);

    assertThat(form.getStructure()).isEqualTo(
        SearchSelectablePrefix.FREE_TEXT_PREFIX + integratedRig.getManualFacility()
    );
  }

  @Test
  public void getFacilityRestUrl() {
    integratedRigService.getFacilityRestUrl();
    verify(devUkFacilitiesService, times(1)).getFacilitiesRestUrl();
  }

  @Test
  public void getPreSelectedFacility() {
    var form = new IntegratedRigForm();
    form.setStructure("test");

    integratedRigService.getPreSelectedFacility(form);
    verify(devUkFacilitiesService, times(1)).getPreSelectedFacility(form.getStructure());
  }

  @Test
  public void validate_whenPartial() {
    var form = new IntegratedRigForm();
    assertCorrectValidation(form, ValidationType.PARTIAL);
  }

  @Test
  public void validate_whenFull() {
    var form = new IntegratedRigForm();
    assertCorrectValidation(form, ValidationType.FULL);
  }

  @Test
  public void createIntegratedRig_whenFacilityFromDevUk() {

    final var devUkFacilityId = 123;
    final var devUkFacility = DevUkTestUtil.getDevUkFacility(devUkFacilityId, "test");

    var form = IntegratedRigTestUtil.createIntegratedRigForm();
    form.setStructure(String.valueOf(devUkFacilityId));



    when(devUkFacilitiesService.getFacilityAsList(form.getStructure())).thenReturn(List.of(devUkFacility));

    var persistedIntegratedRig = integratedRigService.createIntegratedRig(projectDetail, form);

    assertThat(persistedIntegratedRig.getFacility()).isEqualTo(devUkFacility);
    assertThat(persistedIntegratedRig.getManualFacility()).isNull();
    checkCommonIntegratedRigEntityFields(persistedIntegratedRig, form);
  }

  @Test
  public void createIntegratedRig_whenFacilityIsManual() {

    final var manualEntryFacility = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my manual facility";

    var form = IntegratedRigTestUtil.createIntegratedRigForm();
    form.setStructure(manualEntryFacility);

    var persistedIntegratedRig = integratedRigService.createIntegratedRig(projectDetail, form);

    assertThat(persistedIntegratedRig.getFacility()).isNull();
    assertThat(persistedIntegratedRig.getManualFacility()).isEqualTo(SearchSelectorService.removePrefix(manualEntryFacility));
    checkCommonIntegratedRigEntityFields(persistedIntegratedRig, form);
  }

  @Test
  public void updateIntegratedRig_whenFacilityFromDevUk() {

    final var devUkFacilityId = 123;
    final var devUkFacility = DevUkTestUtil.getDevUkFacility(devUkFacilityId, "test");

    var form = IntegratedRigTestUtil.createIntegratedRigForm();
    form.setStructure(String.valueOf(devUkFacilityId));

    when(devUkFacilitiesService.getFacilityAsList(form.getStructure())).thenReturn(List.of(devUkFacility));

    when(integratedRigRepository.findByIdAndProjectDetail(INTEGRATED_RIG_ID, projectDetail))
        .thenReturn(Optional.of(new IntegratedRig()));

    var persistedIntegratedRig = integratedRigService.updateIntegratedRig(
        INTEGRATED_RIG_ID,
        projectDetail,
        form
    );

    assertThat(persistedIntegratedRig.getFacility()).isEqualTo(devUkFacility);
    assertThat(persistedIntegratedRig.getManualFacility()).isNull();
    checkCommonIntegratedRigEntityFields(persistedIntegratedRig, form);
  }

  @Test
  public void updateIntegratedRig_whenFacilityIsManual() {

    final var manualEntryFacility = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my manual facility";

    var form = IntegratedRigTestUtil.createIntegratedRigForm();
    form.setStructure(manualEntryFacility);

    when(integratedRigRepository.findByIdAndProjectDetail(INTEGRATED_RIG_ID, projectDetail))
        .thenReturn(Optional.of(new IntegratedRig()));

    var persistedIntegratedRig = integratedRigService.updateIntegratedRig(
        INTEGRATED_RIG_ID,
        projectDetail,
        form
    );

    assertThat(persistedIntegratedRig.getFacility()).isNull();
    assertThat(persistedIntegratedRig.getManualFacility()).isEqualTo(SearchSelectorService.removePrefix(manualEntryFacility));
    checkCommonIntegratedRigEntityFields(persistedIntegratedRig, form);
  }

  @Test
  public void getIntegratedRigs_whenExist_thenReturnList() {
    var integratedRig1 = IntegratedRigTestUtil.createIntegratedRig_withDevUkFacility();
    var integratedRig2 = IntegratedRigTestUtil.createIntegratedRig_withManualFacility();

    when(integratedRigRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of(integratedRig1, integratedRig2));

    var integratedRigs = integratedRigService.getIntegratedRigs(projectDetail);
    assertThat(integratedRigs).containsExactly(integratedRig1, integratedRig2);
  }

  @Test
  public void getIntegratedRigs_whenNoneExist_thenEmptyList() {

    when(integratedRigRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of());

    var integratedRigs = integratedRigService.getIntegratedRigs(projectDetail);
    assertThat(integratedRigs).isEmpty();
  }

  @Test
  public void getIntegratedRigsByProjectAndVersion_whenExist_thenReturnList() {
    var project = projectDetail.getProject();
    var version = projectDetail.getVersion();
    var integratedRigs = List.of(
        IntegratedRigTestUtil.createIntegratedRig_withDevUkFacility(),
        IntegratedRigTestUtil.createIntegratedRig_withManualFacility()
    );

    when(integratedRigRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version))
        .thenReturn(integratedRigs);

    assertThat(integratedRigService.getIntegratedRigsByProjectAndVersion(project, version)).isEqualTo(integratedRigs);
  }

  @Test
  public void getIntegratedRigsByProjectAndVersion_whenNoneExist_thenEmptyList() {
    var project = projectDetail.getProject();
    var version = projectDetail.getVersion();

    when(integratedRigRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version))
        .thenReturn(Collections.emptyList());

    assertThat(integratedRigService.getIntegratedRigsByProjectAndVersion(project, version)).isEmpty();
  }

  @Test
  public void deleteIntegratedRig() {
    var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withDevUkFacility();
    integratedRigService.deleteIntegratedRig(integratedRig);
    verify(integratedRigRepository, times(1)).delete(integratedRig);
  }

  @Test
  public void getIntegratedRig_whenExists_thenReturn() {

    var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withManualFacility();

    when(integratedRigRepository.findByIdAndProjectDetail(INTEGRATED_RIG_ID, projectDetail)).thenReturn(
        Optional.of(integratedRig)
    );

    var result = integratedRigService.getIntegratedRig(INTEGRATED_RIG_ID, projectDetail);

    assertThat(result.getId()).isEqualTo(integratedRig.getId());
    assertThat(result.getProjectDetail().getId()).isEqualTo(integratedRig.getProjectDetail().getId());
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getIntegratedRig_whenNotFound_thenException() {

    when(integratedRigRepository.findByIdAndProjectDetail(INTEGRATED_RIG_ID, projectDetail)).thenReturn(
        Optional.empty()
    );

    integratedRigService.getIntegratedRig(INTEGRATED_RIG_ID, projectDetail);
  }

  @Test
  public void canShowInTaskList_true() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, ProjectTask.INTEGRATED_RIGS)).thenReturn(true);
    assertThat(integratedRigService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR))).isTrue();
  }

  @Test
  public void canShowInTaskList_false() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, ProjectTask.INTEGRATED_RIGS)).thenReturn(false);
    assertThat(integratedRigService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR))).isFalse();
  }

  @Test
  public void canShowInTaskList_userToProjectRelationshipSmokeTest() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, ProjectTask.INTEGRATED_RIGS)).thenReturn(true);
    ProjectFormSectionServiceTestUtil.canShowInTaskList_userToProjectRelationshipSmokeTest(
        integratedRigService,
        projectDetail,
        Set.of(UserToProjectRelationship.OPERATOR)
    );
  }

  @Test
  public void isTaskValidForProjectDetail_true() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, ProjectTask.INTEGRATED_RIGS)).thenReturn(true);
    assertThat(integratedRigService.isTaskValidForProjectDetail(projectDetail)).isTrue();
  }

  @Test
  public void isTaskValidForProjectDetail_false() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, ProjectTask.INTEGRATED_RIGS)).thenReturn(false);
    assertThat(integratedRigService.isTaskValidForProjectDetail(projectDetail)).isFalse();
  }

  @Test
  public void removeSectionData_verifyInteractions() {
    integratedRigService.removeSectionData(projectDetail);

    verify(integratedRigRepository, times(1)).deleteAllByProjectDetail(projectDetail);
  }

  @Test
  public void copySectionData_verifyDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    final var integratedRigs = List.of(IntegratedRigTestUtil.createIntegratedRig_withDevUkFacility());

    when(integratedRigRepository.findByProjectDetailOrderByIdAsc(fromProjectDetail)).thenReturn(integratedRigs);

    integratedRigService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        integratedRigs,
        toProjectDetail,
        IntegratedRig.class
    );
  }

  @Test
  public void getSupportedProjectTypes_verifyInfrastructure() {
    assertThat(integratedRigService.getSupportedProjectTypes()).containsExactly(ProjectType.INFRASTRUCTURE);
  }

  @Test
  public void alwaysCopySectionData_verifyFalse() {
    assertThat(integratedRigService.alwaysCopySectionData(projectDetail)).isFalse();
  }

  @Test
  public void allowSectionDataCleanUp_verifyIsTrue() {
    final var allowSectionDateCleanUp = integratedRigService.allowSectionDataCleanUp(projectDetail);
    assertThat(allowSectionDateCleanUp).isTrue();
  }
}
