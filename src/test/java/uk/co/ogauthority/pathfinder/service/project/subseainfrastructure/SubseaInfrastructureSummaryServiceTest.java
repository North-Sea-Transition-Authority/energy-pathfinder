package uk.co.ogauthority.pathfinder.service.project.subseainfrastructure;

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
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.SubseaInfrastructureTestUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class SubseaInfrastructureSummaryServiceTest {

  @Mock
  private SubseaInfrastructureService subseaInfrastructureService;

  private SubseaInfrastructureSummaryService subseaInfrastructureSummaryService;

  private ProjectDetail projectDetail;

  @Before
  public void setup() {
    projectDetail = ProjectUtil.getProjectDetails();
    subseaInfrastructureSummaryService = new SubseaInfrastructureSummaryService(subseaInfrastructureService);
  }

  @Test
  public void getSubseaInfrastructureSummaryViews_whenViews_thenReturnPopulatedList() {

    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();

    when(subseaInfrastructureService.getSubseaInfrastructures(projectDetail)).thenReturn(
        List.of(subseaInfrastructure)
    );

    var results = subseaInfrastructureSummaryService.getSubseaInfrastructureSummaryViews(projectDetail);

    assertThat(results).hasSize(1);
    var subseaInfrastructureView = results.get(0);
    assertThat(subseaInfrastructureView.getId()).isEqualTo(subseaInfrastructure.getId());
    assertThat(subseaInfrastructureView.getProjectId()).isEqualTo(subseaInfrastructure.getProjectDetail().getId());
    assertThat(subseaInfrastructureView.isValid()).isTrue();
  }

  @Test
  public void getSubseaInfrastructureSummaryViews_whenNoViews_thenReturnEmptyList() {

    when(subseaInfrastructureService.getSubseaInfrastructures(projectDetail)).thenReturn(
        List.of()
    );

    var results = subseaInfrastructureSummaryService.getSubseaInfrastructureSummaryViews(projectDetail);
    assertThat(results).isEmpty();
  }

  @Test
  public void getSubseaInfrastructureSummaryView_whenFound_thenViewReturned() {

    final var subseaInfrastructureId = 1;
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();

    when(subseaInfrastructureService.getSubseaInfrastructure(subseaInfrastructureId, projectDetail)).thenReturn(subseaInfrastructure);

    var view = subseaInfrastructureSummaryService.getSubseaInfrastructureSummaryView(
        subseaInfrastructureId,
        projectDetail,
        1
    );

    assertThat(view.getProjectId()).isEqualTo(projectDetail.getProject().getId());
    assertThat(view.getValid()).isTrue();
  }

  @Test
  public void getValidatedSubseaInfrastructureSummaryViews_whenValid_thenIsValidTrue() {

    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();

    when(subseaInfrastructureService.isValid(subseaInfrastructure, ValidationType.FULL)).thenReturn(true);

    when(subseaInfrastructureService.getSubseaInfrastructures(projectDetail)).thenReturn(
        List.of(subseaInfrastructure)
    );

    var results = subseaInfrastructureSummaryService.getValidatedSubseaInfrastructureSummaryViews(projectDetail);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).isValid()).isTrue();
  }

  @Test
  public void getValidatedSubseaInfrastructureSummaryViews_whenInvalid_thenIsValidFalse() {

    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();

    when(subseaInfrastructureService.isValid(subseaInfrastructure, ValidationType.FULL)).thenReturn(false);

    when(subseaInfrastructureService.getSubseaInfrastructures(projectDetail)).thenReturn(
        List.of(subseaInfrastructure)
    );

    var results = subseaInfrastructureSummaryService.getValidatedSubseaInfrastructureSummaryViews(projectDetail);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).isValid()).isFalse();
  }

  @Test
  public void getValidatedSubseaInfrastructureSummaryViews_whenNoViews_thenEmptyList() {

    when(subseaInfrastructureService.getSubseaInfrastructures(projectDetail)).thenReturn(
        List.of()
    );

    var results = subseaInfrastructureSummaryService.getValidatedSubseaInfrastructureSummaryViews(projectDetail);

    assertThat(results).isEmpty();
  }

  @Test
  public void getSubseaInfrastructureViewErrors_whenErrors_thenErrorsReturned() {
    var subseaInfrastructureViews = List.of(
        SubseaInfrastructureTestUtil.createSubseaInfrastructureView(1, true),
        SubseaInfrastructureTestUtil.createSubseaInfrastructureView(2, false),
        SubseaInfrastructureTestUtil.createSubseaInfrastructureView(3, false)
    );
    var errors = subseaInfrastructureSummaryService.getSubseaInfrastructureViewErrors(subseaInfrastructureViews);

    assertThat(errors.size()).isEqualTo(2);

    var subseaInfrastructureView1 = errors.get(0);

    assertThat(subseaInfrastructureView1.getDisplayOrder()).isEqualTo(2);
    assertThat(subseaInfrastructureView1.getFieldName()).isEqualTo(String.format(SubseaInfrastructureSummaryService.ERROR_FIELD_NAME, 2));
    assertThat(subseaInfrastructureView1.getErrorMessage()).isEqualTo(String.format(SubseaInfrastructureSummaryService.ERROR_MESSAGE, 2));

    var subseaInfrastructureView2 = errors.get(1);
    assertThat(subseaInfrastructureView2.getDisplayOrder()).isEqualTo(3);
    assertThat(subseaInfrastructureView2.getFieldName()).isEqualTo(String.format(SubseaInfrastructureSummaryService.ERROR_FIELD_NAME, 3));
    assertThat(subseaInfrastructureView2.getErrorMessage()).isEqualTo(String.format(SubseaInfrastructureSummaryService.ERROR_MESSAGE, 3));
  }

  @Test
  public void getSubseaInfrastructureViewErrors_whenNoErrors_thenEmptyListReturned() {
    var subseaInfrastructureViews = List.of(
        SubseaInfrastructureTestUtil.createSubseaInfrastructureView(1, true),
        SubseaInfrastructureTestUtil.createSubseaInfrastructureView(2, true)
    );
    var errors = subseaInfrastructureSummaryService.getSubseaInfrastructureViewErrors(subseaInfrastructureViews);

    assertThat(errors).isEmpty();
  }

  @Test
  public void validateViews_whenEmpty_thenInvalid() {
    assertThat(subseaInfrastructureSummaryService.validateViews(List.of())).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  public void validateViews_whenInvalidView_thenInvalid() {
    var subseaInfrastructureView = SubseaInfrastructureTestUtil.createSubseaInfrastructureView(1, false);
    var validationResult = subseaInfrastructureSummaryService.validateViews(List.of(subseaInfrastructureView));
    assertThat(validationResult).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  public void validateViews_whenValidView_thenValid() {
    var subseaInfrastructureView = SubseaInfrastructureTestUtil.createSubseaInfrastructureView(1, true);
    var validationResult = subseaInfrastructureSummaryService.validateViews(List.of(subseaInfrastructureView));
    assertThat(validationResult).isEqualTo(ValidationResult.VALID);
  }
}