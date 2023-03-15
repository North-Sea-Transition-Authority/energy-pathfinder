package uk.co.ogauthority.pathfinder.model.view.decommissionedpipeline;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline.DecommissionedPipeline;
import uk.co.ogauthority.pathfinder.testutil.DecommissionedPipelineTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class DecommissionedPipelineViewUtilTest {

  private static final Integer DISPLAY_ORDER = 1;
  private static final boolean IS_VALID = true;

  private void checkCommonFields(DecommissionedPipelineView decommissionedPipelineView,
                                 Integer displayOrder,
                                 boolean isValid,
                                 DecommissionedPipeline decommissionedPipeline) {
    assertThat(decommissionedPipelineView.getDisplayOrder()).isEqualTo(displayOrder);
    assertThat(decommissionedPipelineView.isValid()).isEqualTo(isValid);
    assertThat(decommissionedPipelineView.getProjectId()).isEqualTo(decommissionedPipeline.getProjectDetail().getProject().getId());
    assertThat(decommissionedPipelineView.getStatus()).isEqualTo(decommissionedPipeline.getStatus().getDisplayName());
    assertThat(decommissionedPipelineView.getRemovalPremise()).isEqualTo(decommissionedPipeline.getRemovalPremise().getDisplayName());
  }

  @Test
  public void from_withPipeline() {
    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();
    var decommissionedPipelineView = DecommissionedPipelineViewUtil.from(decommissionedPipeline, DISPLAY_ORDER, IS_VALID);

    assertThat(decommissionedPipelineView.getPipeline()).isEqualTo(decommissionedPipeline.getPipeline().getSelectionText());
    checkCommonFields(decommissionedPipelineView, DISPLAY_ORDER, IS_VALID, decommissionedPipeline);
  }

  @Test
  public void from_withDecommissioningPeriodProvided() {
    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();
    decommissionedPipeline.setEarliestRemovalYear(String.valueOf(2019));
    decommissionedPipeline.setLatestRemovalYear(String.valueOf(2020));

    var decommissionedPipelineView = DecommissionedPipelineViewUtil.from(decommissionedPipeline, DISPLAY_ORDER, IS_VALID);

    assertThat(decommissionedPipelineView.getDecommissioningEarliestYear()).isEqualTo(
        String.format(DecommissionedPipelineViewUtil.EARLIEST_DECOM_YEAR_TEXT, decommissionedPipeline.getEarliestRemovalYear())
    );
    assertThat(decommissionedPipelineView.getDecommissioningLatestYear()).isEqualTo(
        String.format(DecommissionedPipelineViewUtil.LATEST_DECOM_YEAR_TEXT, decommissionedPipeline.getLatestRemovalYear())
    );

    checkCommonFields(decommissionedPipelineView, DISPLAY_ORDER, IS_VALID, decommissionedPipeline);
  }

  @Test
  public void from_withDecommissioningPeriodEmpty() {

    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();
    decommissionedPipeline.setEarliestRemovalYear(null);
    decommissionedPipeline.setLatestRemovalYear(null);

    var decommissionedPipelineView = DecommissionedPipelineViewUtil.from(decommissionedPipeline, DISPLAY_ORDER, IS_VALID);

    assertThat(decommissionedPipelineView.getDecommissioningEarliestYear()).isEqualTo(
        String.format(DecommissionedPipelineViewUtil.EARLIEST_DECOM_YEAR_TEXT, DecommissionedPipelineViewUtil.DEFAULT_DECOM_YEAR_TEXT)
    );
    assertThat(decommissionedPipelineView.getDecommissioningLatestYear()).isEqualTo(
        String.format(DecommissionedPipelineViewUtil.LATEST_DECOM_YEAR_TEXT, DecommissionedPipelineViewUtil.DEFAULT_DECOM_YEAR_TEXT)
    );

    checkCommonFields(decommissionedPipelineView, DISPLAY_ORDER, IS_VALID, decommissionedPipeline);
  }

  @Test
  public void from_withoutIsValidSet_defaultIsTrue() {
    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();

    var decommissionedPipelineView = DecommissionedPipelineViewUtil.from(decommissionedPipeline, 2);
    checkCommonFields(decommissionedPipelineView, 2, IS_VALID, decommissionedPipeline);
  }

  @Test
  public void from_whenIsValidFalse_thenFalseInView() {
    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();

    var decommissionedPipelineView = DecommissionedPipelineViewUtil.from(decommissionedPipeline, DISPLAY_ORDER, false);
    checkCommonFields(decommissionedPipelineView, DISPLAY_ORDER, false, decommissionedPipeline);
  }
}
