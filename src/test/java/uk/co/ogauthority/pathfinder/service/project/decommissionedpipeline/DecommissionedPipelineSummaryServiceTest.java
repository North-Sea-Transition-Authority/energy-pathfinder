package uk.co.ogauthority.pathfinder.service.project.decommissionedpipeline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.testutil.DecommissionedPipelineTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class DecommissionedPipelineSummaryServiceTest {

  @Mock
  private DecommissionedPipelineService decommissionedPipelineService;

  private DecommissionedPipelineSummaryService decommissionedPipelineSummaryService;

  private ProjectDetail projectDetail;

  @Before
  public void setup() {
    projectDetail = ProjectUtil.getProjectDetails();
    decommissionedPipelineSummaryService = new DecommissionedPipelineSummaryService(decommissionedPipelineService);
  }

  @Test
  public void getDecommissionedPipelineSummaryViews_whenViews_thenReturnPopulatedList() {

    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();

    when(decommissionedPipelineService.getDecommissionedPipelines(projectDetail)).thenReturn(
        List.of(decommissionedPipeline)
    );

    var results = decommissionedPipelineSummaryService.getDecommissionedPipelineSummaryViews(projectDetail);

    assertThat(results).hasSize(1);
    var decommissionedPipelineView = results.get(0);
    assertThat(decommissionedPipelineView.getId()).isEqualTo(decommissionedPipeline.getId());
    assertThat(decommissionedPipelineView.getProjectId()).isEqualTo(decommissionedPipeline.getProjectDetail().getId());
    assertThat(decommissionedPipelineView.isValid()).isTrue();
  }

  @Test
  public void getDecommissionedPipelineSummaryViews_whenNoViews_thenReturnEmptyList() {

    when(decommissionedPipelineService.getDecommissionedPipelines(projectDetail)).thenReturn(
        List.of()
    );

    var results = decommissionedPipelineSummaryService.getDecommissionedPipelineSummaryViews(projectDetail);
    assertThat(results).isEmpty();
  }

  @Test
  public void getDecommissionedPipelineSummaryView_whenFound_thenViewReturned() {

    final var decommissionedPipelineId = 1;
    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();

    when(decommissionedPipelineService.getDecommissionedPipeline(decommissionedPipelineId, projectDetail)).thenReturn(decommissionedPipeline);

    var view = decommissionedPipelineSummaryService.getDecommissionedPipelineSummaryView(
        decommissionedPipelineId,
        projectDetail,
        1
    );

    assertThat(view.getProjectId()).isEqualTo(projectDetail.getProject().getId());
    assertThat(view.getValid()).isTrue();
  }

  @Test
  public void getValidatedDecommissionedPipelineSummaryViews_whenValid_thenIsValidTrue() {

    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();

    when(decommissionedPipelineService.isValid(decommissionedPipeline, ValidationType.FULL)).thenReturn(true);

    when(decommissionedPipelineService.getDecommissionedPipelines(projectDetail)).thenReturn(
        List.of(decommissionedPipeline)
    );

    var results = decommissionedPipelineSummaryService.getValidatedDecommissionedPipelineSummaryViews(projectDetail);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).isValid()).isTrue();
  }

  @Test
  public void getValidatedDecommissionedPipelineSummaryViews_whenInvalid_thenIsValidFalse() {

    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();

    when(decommissionedPipelineService.isValid(decommissionedPipeline, ValidationType.FULL)).thenReturn(false);

    when(decommissionedPipelineService.getDecommissionedPipelines(projectDetail)).thenReturn(
        List.of(decommissionedPipeline)
    );

    var results = decommissionedPipelineSummaryService.getValidatedDecommissionedPipelineSummaryViews(projectDetail);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).isValid()).isFalse();
  }

  @Test
  public void getValidatedDecommissionedPipelineSummaryViews_whenNoViews_thenEmptyList() {

    when(decommissionedPipelineService.getDecommissionedPipelines(projectDetail)).thenReturn(
        List.of()
    );

    var results = decommissionedPipelineSummaryService.getValidatedDecommissionedPipelineSummaryViews(projectDetail);

    assertThat(results).isEmpty();
  }

  @Test
  public void getDecommissionedPipelineViewErrors_whenErrors_thenErrorsReturned() {
    var decommissionedPipelineViews = List.of(
        DecommissionedPipelineTestUtil.createDecommissionedPipelineView(1, true),
        DecommissionedPipelineTestUtil.createDecommissionedPipelineView(2, false),
        DecommissionedPipelineTestUtil.createDecommissionedPipelineView(3, false)
    );
    var errors = decommissionedPipelineSummaryService.getDecommissionedPipelineViewErrors(decommissionedPipelineViews);

    assertThat(errors.size()).isEqualTo(2);

    var decommissionedPipelineView1 = errors.get(0);

    assertThat(decommissionedPipelineView1.getDisplayOrder()).isEqualTo(2);
    assertThat(decommissionedPipelineView1.getFieldName()).isEqualTo(String.format(DecommissionedPipelineSummaryService.ERROR_FIELD_NAME, 2));
    assertThat(decommissionedPipelineView1.getErrorMessage()).isEqualTo(String.format(DecommissionedPipelineSummaryService.ERROR_MESSAGE, 2));

    var decommissionedPipelineView2 = errors.get(1);
    assertThat(decommissionedPipelineView2.getDisplayOrder()).isEqualTo(3);
    assertThat(decommissionedPipelineView2.getFieldName()).isEqualTo(String.format(DecommissionedPipelineSummaryService.ERROR_FIELD_NAME, 3));
    assertThat(decommissionedPipelineView2.getErrorMessage()).isEqualTo(String.format(DecommissionedPipelineSummaryService.ERROR_MESSAGE, 3));
  }

  @Test
  public void getDecommissionedPipelineViewErrors_whenNoErrors_thenEmptyListReturned() {
    var decommissionedPipelineViews = List.of(
        DecommissionedPipelineTestUtil.createDecommissionedPipelineView(1, true),
        DecommissionedPipelineTestUtil.createDecommissionedPipelineView(2, true)
    );
    var errors = decommissionedPipelineSummaryService.getDecommissionedPipelineViewErrors(decommissionedPipelineViews);

    assertThat(errors).isEmpty();
  }

  @Test
  public void validateViews_whenEmpty_thenInvalid() {
    assertThat(decommissionedPipelineSummaryService.validateViews(List.of())).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  public void validateViews_whenInvalidView_thenInvalid() {
    var decommissionedPipelineViews = DecommissionedPipelineTestUtil.createDecommissionedPipelineView(1, false);
    var validationResult = decommissionedPipelineSummaryService.validateViews(List.of(decommissionedPipelineViews));
    assertThat(validationResult).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  public void validateViews_whenValidView_thenValid() {
    var decommissionedPipelineViews = DecommissionedPipelineTestUtil.createDecommissionedPipelineView(1, true);
    var validationResult = decommissionedPipelineSummaryService.validateViews(List.of(decommissionedPipelineViews));
    assertThat(validationResult).isEqualTo(ValidationResult.VALID);
  }
}
