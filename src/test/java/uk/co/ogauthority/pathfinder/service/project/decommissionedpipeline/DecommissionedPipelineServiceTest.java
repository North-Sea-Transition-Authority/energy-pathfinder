package uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline.DecommissionedPipeline;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineForm;
import uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline.DecommissionedPipelineFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.decommissionedpipeline.DecommissionedPipelineRepository;
import uk.co.ogauthority.pathfinder.service.pipeline.PipelinesService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.DecommissionedPipelineTestUtil;
import uk.co.ogauthority.pathfinder.testutil.PipelineTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class DecommissionedPipelineServiceTest {

  private DecommissionedPipelineService decommissionedPipelineService;

  @Mock
  private PipelinesService pipelinesService;

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
        pipelinesService,
        decommissionedPipelineRepository,
        decommissionedPipelineFormValidator,
        validationService
    );

    projectDetail = ProjectUtil.getProjectDetails();

    when(decommissionedPipelineRepository.save(any(DecommissionedPipeline.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
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
  public void getPipelinesRestUrl() {
    decommissionedPipelineService.getPipelinesRestUrl();
    verify(pipelinesService, times(1)).getPipelinesRestUrl();
  }

  @Test
  public void getPreSelectedFacility() {
    var form = new DecommissionedPipelineForm();
    form.setPipeline("test");

    decommissionedPipelineService.getPreSelectedPipeline(form);
    verify(pipelinesService, times(1)).getPreSelectedPipeline(form.getPipeline());
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

    when(pipelinesService.getPipelineByIdOrError(pipelineId)).thenReturn(pipeline);

    var persistedDecommissionedPipeline = decommissionedPipelineService.createDecommissionedPipeline(projectDetail, form);

    assertThat(persistedDecommissionedPipeline.getPipeline()).isEqualTo(pipeline);
    checkCommonDecommissionedPipelineEntityFields(persistedDecommissionedPipeline, form);
  }
}
