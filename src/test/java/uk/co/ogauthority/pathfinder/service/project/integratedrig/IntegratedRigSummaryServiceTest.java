package uk.co.ogauthority.pathfinder.service.project.integratedrig;

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
import uk.co.ogauthority.pathfinder.testutil.IntegratedRigTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class IntegratedRigSummaryServiceTest {

  @Mock
  private IntegratedRigService integratedRigService;

  private IntegratedRigSummaryService integratedRigSummaryService;

  private ProjectDetail projectDetail;

  @Before
  public void setup() {
    projectDetail = ProjectUtil.getProjectDetails();
    integratedRigSummaryService = new IntegratedRigSummaryService(integratedRigService);
  }

  @Test
  public void getIntegratedRigSummaryViews_whenViews_thenReturnPopulatedList() {

    var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withManualFacility();

    when(integratedRigService.getIntegratedRigs(projectDetail)).thenReturn(
        List.of(integratedRig)
    );

    var results = integratedRigSummaryService.getIntegratedRigSummaryViews(projectDetail);

    assertThat(results).hasSize(1);
    var integratedRigView = results.get(0);
    assertThat(integratedRigView.getId()).isEqualTo(integratedRig.getId());
    assertThat(integratedRigView.getProjectId()).isEqualTo(integratedRig.getProjectDetail().getId());
    assertThat(integratedRigView.isValid()).isTrue();
  }

  @Test
  public void getIntegratedRigSummaryViews_whenNoViews_thenReturnEmptyList() {

    when(integratedRigService.getIntegratedRigs(projectDetail)).thenReturn(
        List.of()
    );

    var results = integratedRigSummaryService.getIntegratedRigSummaryViews(projectDetail);
    assertThat(results).isEmpty();
  }

  @Test
  public void getIntegratedRigSummaryView_whenFound_thenViewReturned() {

    final var integratedRigId = 1;
    var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withManualFacility();

    when(integratedRigService.getIntegratedRig(integratedRigId, projectDetail)).thenReturn(integratedRig);

    var view = integratedRigSummaryService.getIntegratedRigSummaryView(
        integratedRigId,
        projectDetail,
        1
    );

    assertThat(view.getProjectId()).isEqualTo(projectDetail.getProject().getId());
    assertThat(view.getValid()).isTrue();
  }

  @Test
  public void getValidatedIntegratedRigSummaryViews_whenValid_thenIsValidTrue() {

    var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withManualFacility();

    when(integratedRigService.isValid(integratedRig, ValidationType.FULL)).thenReturn(true);

    when(integratedRigService.getIntegratedRigs(projectDetail)).thenReturn(
        List.of(integratedRig)
    );

    var results = integratedRigSummaryService.getValidatedIntegratedRigSummaryViews(projectDetail);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).isValid()).isTrue();
  }

  @Test
  public void getValidatedIntegratedRigSummaryViews_whenInvalid_thenIsValidFalse() {

    var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withManualFacility();

    when(integratedRigService.isValid(integratedRig, ValidationType.FULL)).thenReturn(false);

    when(integratedRigService.getIntegratedRigs(projectDetail)).thenReturn(
        List.of(integratedRig)
    );

    var results = integratedRigSummaryService.getValidatedIntegratedRigSummaryViews(projectDetail);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).isValid()).isFalse();
  }

  @Test
  public void getValidatedIntegratedRigSummaryViews_whenNoViews_thenEmptyList() {

    when(integratedRigService.getIntegratedRigs(projectDetail)).thenReturn(
        List.of()
    );

    var results = integratedRigSummaryService.getValidatedIntegratedRigSummaryViews(projectDetail);

    assertThat(results).isEmpty();
  }

  @Test
  public void getIntegratedRigViewErrors_whenErrors_thenErrorsReturned() {
    var integratedRigViews = List.of(
        IntegratedRigTestUtil.createIntegratedRigView(1, true),
        IntegratedRigTestUtil.createIntegratedRigView(2, false),
        IntegratedRigTestUtil.createIntegratedRigView(3, false)
    );
    var errors = integratedRigSummaryService.getIntegratedRigViewErrors(integratedRigViews);

    assertThat(errors.size()).isEqualTo(2);

    var integratedRigView1 = errors.get(0);

    assertThat(integratedRigView1.getDisplayOrder()).isEqualTo(2);
    assertThat(integratedRigView1.getFieldName()).isEqualTo(String.format(IntegratedRigSummaryService.ERROR_FIELD_NAME, 2));
    assertThat(integratedRigView1.getErrorMessage()).isEqualTo(String.format(IntegratedRigSummaryService.ERROR_MESSAGE, 2));

    var integratedRigView2 = errors.get(1);
    assertThat(integratedRigView2.getDisplayOrder()).isEqualTo(3);
    assertThat(integratedRigView2.getFieldName()).isEqualTo(String.format(IntegratedRigSummaryService.ERROR_FIELD_NAME, 3));
    assertThat(integratedRigView2.getErrorMessage()).isEqualTo(String.format(IntegratedRigSummaryService.ERROR_MESSAGE, 3));
  }

  @Test
  public void getIntegratedRigViewErrors_whenNoErrors_thenEmptyListReturned() {
    var integratedRigViews = List.of(
        IntegratedRigTestUtil.createIntegratedRigView(1, true),
        IntegratedRigTestUtil.createIntegratedRigView(2, true)
    );
    var errors = integratedRigSummaryService.getIntegratedRigViewErrors(integratedRigViews);

    assertThat(errors).isEmpty();
  }

  @Test
  public void validateViews_whenEmpty_thenInvalid() {
    assertThat(integratedRigSummaryService.validateViews(List.of())).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  public void validateViews_whenInvalidView_thenInvalid() {
    var integratedRigView = IntegratedRigTestUtil.createIntegratedRigView(1, false);
    var validationResult = integratedRigSummaryService.validateViews(List.of(integratedRigView));
    assertThat(validationResult).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  public void validateViews_whenValidView_thenValid() {
    var integratedRigView = IntegratedRigTestUtil.createIntegratedRigView(1, true);
    var validationResult = integratedRigSummaryService.validateViews(List.of(integratedRigView));
    assertThat(validationResult).isEqualTo(ValidationResult.VALID);
  }

  @Test
  public void canShowInTaskList_whenCanShowInTaskList_thenTrue() {
    when(integratedRigService.canShowInTaskList(projectDetail)).thenReturn(true);

    assertThat(integratedRigSummaryService.canShowInTaskList(projectDetail)).isTrue();
  }

  @Test
  public void canShowInTaskList_whenCannotShowInTaskList_thenFalse() {
    when(integratedRigService.canShowInTaskList(projectDetail)).thenReturn(false);

    assertThat(integratedRigSummaryService.canShowInTaskList(projectDetail)).isFalse();
  }
}
