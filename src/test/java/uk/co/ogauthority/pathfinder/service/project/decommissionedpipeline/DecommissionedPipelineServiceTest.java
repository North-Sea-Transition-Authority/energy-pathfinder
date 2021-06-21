package uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
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
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline.DecommissionedPipeline;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineForm;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.decommissionedpipeline.DecommissionedPipelineRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.pipeline.PipelineService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.DecommissionedPipelineTestUtil;
import uk.co.ogauthority.pathfinder.testutil.PipelineTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class DecommissionedPipelineServiceTest {

  private static final Integer DECOMMISSIONED_PIPELINE_ID = 1;

  private DecommissionedPipelineService decommissionedPipelineService;

  @Mock
  private PipelineService pipelineService;

  @Mock
  private DecommissionedPipelineRepository decommissionedPipelineRepository;

  @Mock
  private DecommissionedPipelineFormValidator decommissionedPipelineFormValidator;

  @Mock
  private ValidationService validationService;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private ProjectDetail projectDetail;

  @Before
  public void setup() {
    decommissionedPipelineService = new DecommissionedPipelineService(
        pipelineService,
        decommissionedPipelineRepository,
        decommissionedPipelineFormValidator,
        validationService,
        projectSetupService,
        entityDuplicationService
    );

    projectDetail = ProjectUtil.getProjectDetails();

    when(decommissionedPipelineRepository.save(any(DecommissionedPipeline.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  private void checkCommonDecommissionedPipelineFormFields(DecommissionedPipelineForm form,
                                                           DecommissionedPipeline decommissionedPipeline) {
    assertThat(form.getMaterialType()).isEqualTo(decommissionedPipeline.getMaterialType());
    assertThat(form.getStatus()).isEqualTo(decommissionedPipeline.getStatus());
    assertThat(form.getDecommissioningDate().getMinYear()).isEqualTo(decommissionedPipeline.getEarliestRemovalYear());
    assertThat(form.getDecommissioningDate().getMaxYear()).isEqualTo(decommissionedPipeline.getLatestRemovalYear());
    assertThat(form.getRemovalPremise()).isEqualTo(decommissionedPipeline.getRemovalPremise());
  }

  private void checkCommonDecommissionedPipelineEntityFields(DecommissionedPipeline decommissionedPipeline,
                                                             DecommissionedPipelineForm form) {
    assertThat(decommissionedPipeline.getMaterialType()).isEqualTo(form.getMaterialType());
    assertThat(decommissionedPipeline.getStatus()).isEqualTo(form.getStatus());
    assertThat(decommissionedPipeline.getEarliestRemovalYear()).isEqualTo(form.getDecommissioningDate().getMinYear());
    assertThat(decommissionedPipeline.getLatestRemovalYear()).isEqualTo(form.getDecommissioningDate().getMaxYear());
    assertThat(decommissionedPipeline.getRemovalPremise()).isEqualTo(form.getRemovalPremise());
  }


  private void assertCorrectValidation(DecommissionedPipelineForm form, ValidationType validationType) {

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    decommissionedPipelineService.validate(
        form,
        bindingResult,
        validationType
    );

    verify(decommissionedPipelineFormValidator, times(1)).validate(any(), any(), any());
    verify(validationService, times(1)).validate(form, bindingResult, validationType);
  }

  @Test
  public void getForm() {
    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();

    when(decommissionedPipelineRepository.findByIdAndProjectDetail(DECOMMISSIONED_PIPELINE_ID, projectDetail))
        .thenReturn(Optional.of(decommissionedPipeline));

    var form = decommissionedPipelineService.getForm(DECOMMISSIONED_PIPELINE_ID, projectDetail);

    checkCommonDecommissionedPipelineFormFields(form, decommissionedPipeline);

    assertThat(form.getPipeline()).isEqualTo(String.valueOf(decommissionedPipeline.getPipeline().getId()));
  }

  @Test
  public void getPipelineRestUrl() {
    decommissionedPipelineService.getPipelineRestUrl();
    verify(pipelineService, times(1)).getPipelineRestUrl();
  }

  @Test
  public void getPreSelectedFacility() {
    var form = new DecommissionedPipelineForm();
    form.setPipeline("test");

    decommissionedPipelineService.getPreSelectedPipeline(form);
    verify(pipelineService, times(1)).getPreSelectedPipeline(form.getPipeline());
  }

  @Test
  public void validate_whenPartial() {
    var form = new DecommissionedPipelineForm();
    assertCorrectValidation(form, ValidationType.PARTIAL);
  }

  @Test
  public void validate_whenFull() {
    var form = new DecommissionedPipelineForm();
    assertCorrectValidation(form, ValidationType.FULL);
  }

  @Test
  public void createDecommissionedPipeline() {
    final var pipelineId = 123;
    final var pipeline = PipelineTestUtil.getPipeline(pipelineId, "test");

    var form = DecommissionedPipelineTestUtil.createDecommissionedPipelineForm();
    form.setPipeline(String.valueOf(pipelineId));

    when(pipelineService.getPipelineByIdOrError(pipelineId)).thenReturn(pipeline);

    var persistedDecommissionedPipeline = decommissionedPipelineService.createDecommissionedPipeline(projectDetail, form);

    assertThat(persistedDecommissionedPipeline.getPipeline()).isEqualTo(pipeline);
    checkCommonDecommissionedPipelineEntityFields(persistedDecommissionedPipeline, form);
  }

  @Test
  public void updateDecommissionedPipeline() {

    final var pipelineId = 123;
    final var pipeline = PipelineTestUtil.getPipeline(pipelineId, "test");

    var form = DecommissionedPipelineTestUtil.createDecommissionedPipelineForm();
    form.setPipeline(String.valueOf(pipelineId));

    when(pipelineService.getPipelineByIdOrError(Integer.parseInt(form.getPipeline()))).thenReturn(pipeline);

    when(decommissionedPipelineRepository.findByIdAndProjectDetail(DECOMMISSIONED_PIPELINE_ID, projectDetail))
        .thenReturn(Optional.of(new DecommissionedPipeline()));

    var persistedDecommissionedPipeline = decommissionedPipelineService.updateDecommissionedPipeline(
        DECOMMISSIONED_PIPELINE_ID,
        projectDetail,
        form
    );

    assertThat(persistedDecommissionedPipeline.getPipeline()).isEqualTo(pipeline);
    checkCommonDecommissionedPipelineEntityFields(persistedDecommissionedPipeline, form);
  }

 @Test
  public void getDecommissionedPipelines_whenExist_thenReturnList() {
    var decommissionedPipeline1 = DecommissionedPipelineTestUtil.createDecommissionedPipeline();
    var decommissionedPipeline2 = DecommissionedPipelineTestUtil.createDecommissionedPipeline();

    when(decommissionedPipelineRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of(decommissionedPipeline1, decommissionedPipeline2));

    var decommissionedPipelines = decommissionedPipelineService.getDecommissionedPipelines(projectDetail);
    assertThat(decommissionedPipelines).containsExactly(decommissionedPipeline1, decommissionedPipeline2);
  }

  @Test
  public void getDecommissionedPipelines_whenNoneExist_thenEmptyList() {

    when(decommissionedPipelineRepository.findByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of());

    var decommissionedPipelines = decommissionedPipelineService.getDecommissionedPipelines(projectDetail);
    assertThat(decommissionedPipelines).isEmpty();
  }

  @Test
  public void getDecommissionedPipelinesByProjectAndVersion_whenExist_thenReturnList() {
    var project = projectDetail.getProject();
    var version = projectDetail.getVersion();
    var decommissionedPipelines = List.of(
        DecommissionedPipelineTestUtil.createDecommissionedPipeline(),
        DecommissionedPipelineTestUtil.createDecommissionedPipeline()
    );

    when(decommissionedPipelineRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version))
        .thenReturn(decommissionedPipelines);

    assertThat(decommissionedPipelineService.getDecommissionedPipelinesByProjectAndVersion(project, version)).isEqualTo(decommissionedPipelines);
  }

  @Test
  public void getDecommissionedPipelinesByProjectAndVersion_whenNoneExist_thenEmptyList() {
    var project = projectDetail.getProject();
    var version = projectDetail.getVersion();

    when(decommissionedPipelineRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version))
        .thenReturn(Collections.emptyList());

    assertThat(decommissionedPipelineService.getDecommissionedPipelinesByProjectAndVersion(project, version)).isEmpty();
  }

  @Test
  public void deleteDecommissionedPipeline() {
    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();
    decommissionedPipelineService.deleteDecommissionedPipeline(decommissionedPipeline);
    verify(decommissionedPipelineRepository, times(1)).delete(decommissionedPipeline);
  }

  @Test
  public void getDecommissionedPipelineOrError_whenExists_thenReturn() {

    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();

    when(decommissionedPipelineRepository.findByIdAndProjectDetail(DECOMMISSIONED_PIPELINE_ID, projectDetail)).thenReturn(
        Optional.of(decommissionedPipeline)
    );

    var result = decommissionedPipelineService.getDecommissionedPipelineOrError(DECOMMISSIONED_PIPELINE_ID, projectDetail);

    assertThat(result.getId()).isEqualTo(decommissionedPipeline.getId());
    assertThat(result.getProjectDetail().getId()).isEqualTo(decommissionedPipeline.getProjectDetail().getId());
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getDecommissionedPipelineOrError_whenNotFound_thenException() {

    when(decommissionedPipelineRepository.findByIdAndProjectDetail(DECOMMISSIONED_PIPELINE_ID, projectDetail)).thenReturn(
        Optional.empty()
    );

    decommissionedPipelineService.getDecommissionedPipelineOrError(DECOMMISSIONED_PIPELINE_ID, projectDetail);
  }

  // Pipelines disabled: PAT-457
  @Test
  public void canShowInTaskList() {
    assertThat(decommissionedPipelineService.canShowInTaskList(projectDetail)).isFalse();

    verify(projectSetupService, never()).taskValidAndSelectedForProjectDetail(projectDetail, ProjectTask.PIPELINES);
  }

  // @Test
  // public void canShowInTaskList_true() {
  //   when(projectSetupService.taskSelectedForProjectDetail(projectDetail, ProjectTask.PIPELINES)).thenReturn(true);
  //   assertThat(decommissionedPipelineService.canShowInTaskList(projectDetail)).isTrue();
  // }
  //
  // @Test
  // public void canShowInTaskList_false() {
  //   when(projectSetupService.taskSelectedForProjectDetail(projectDetail, ProjectTask.PIPELINES)).thenReturn(false);
  //   assertThat(decommissionedPipelineService.canShowInTaskList(projectDetail)).isFalse();
  // }

  @Test
  public void removeSectionData_verifyInteractions() {
    decommissionedPipelineService.removeSectionData(projectDetail);

    verify(decommissionedPipelineRepository, times(1)).deleteAllByProjectDetail(projectDetail);
  }

  @Test
  public void copySectionData_verifyDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    decommissionedPipelineService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, never()).duplicateEntitiesAndSetNewParent(any(), any(), any());
  }

  // @Test
  // public void copySectionData_verifyDuplicationServiceInteraction() {
  //
  //   final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
  //   final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);
  //
  //   final var pipelines = List.of(DecommissionedPipelineTestUtil.createDecommissionedPipeline());
  //
  //   when(decommissionedPipelineRepository.findByProjectDetailOrderByIdAsc(fromProjectDetail)).thenReturn(pipelines);
  //
  //   decommissionedPipelineService.copySectionData(fromProjectDetail, toProjectDetail);
  //
  //   verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
  //       pipelines,
  //       toProjectDetail,
  //       DecommissionedPipeline.class
  //   );
  // }

  @Test
  public void alwaysCopySectionData_verifyFalse() {
    assertThat(decommissionedPipelineService.alwaysCopySectionData(projectDetail)).isFalse();
  }

  @Test
  public void allowSectionDataCleanUp_verifyIsTrue() {
    final var allowSectionDateCleanUp = decommissionedPipelineService.allowSectionDataCleanUp(projectDetail);
    assertThat(allowSectionDateCleanUp).isTrue();
  }
}
