package uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline;

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
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline.DecommissionedPipeline;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineForm;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.decommissionedpipeline.DecommissionedPipelineRepository;
import uk.co.ogauthority.pathfinder.service.pipeline.PipelineService;
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

  private ProjectDetail projectDetail;

  @Before
  public void setup() {
    decommissionedPipelineService = new DecommissionedPipelineService(
        pipelineService,
        decommissionedPipelineRepository,
        decommissionedPipelineFormValidator,
        validationService
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
  public void deleteDecommissionedPipeline() {
    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();
    decommissionedPipelineService.deleteDecommissionedPipeline(decommissionedPipeline);
    verify(decommissionedPipelineRepository, times(1)).delete(decommissionedPipeline);
  }

  @Test
  public void getDecommissionedPipeline_whenExists_thenReturn() {

    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();

    when(decommissionedPipelineRepository.findByIdAndProjectDetail(DECOMMISSIONED_PIPELINE_ID, projectDetail)).thenReturn(
        Optional.of(decommissionedPipeline)
    );

    var result = decommissionedPipelineService.getDecommissionedPipeline(DECOMMISSIONED_PIPELINE_ID, projectDetail);

    assertThat(result.getId()).isEqualTo(decommissionedPipeline.getId());
    assertThat(result.getProjectDetail().getId()).isEqualTo(decommissionedPipeline.getProjectDetail().getId());
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getDecommissionedPipeline_whenNotFound_thenException() {

    when(decommissionedPipelineRepository.findByIdAndProjectDetail(DECOMMISSIONED_PIPELINE_ID, projectDetail)).thenReturn(
        Optional.empty()
    );

    decommissionedPipelineService.getDecommissionedPipeline(DECOMMISSIONED_PIPELINE_ID, projectDetail);
  }
}
