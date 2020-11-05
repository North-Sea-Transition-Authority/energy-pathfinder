package uk.co.ogauthority.pathfinder.model.view.subseainfrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.subseainfrastructure.SubseaInfrastructure;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.testutil.SubseaInfrastructureTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class SubseaInfrastructureViewUtilTest {

  private static final Integer DISPLAY_ORDER = 1;
  private static final boolean IS_VALID = true;

  private void checkCommonFields(SubseaInfrastructureView subseaInfrastructureView,
                                 Integer displayOrder,
                                 boolean isValid,
                                 SubseaInfrastructure subseaInfrastructure) {
    assertThat(subseaInfrastructureView.getDisplayOrder()).isEqualTo(displayOrder);
    assertThat(subseaInfrastructureView.isValid()).isEqualTo(isValid);
    assertThat(subseaInfrastructureView.getProjectId()).isEqualTo(subseaInfrastructure.getProjectDetail().getProject().getId());
    assertThat(subseaInfrastructureView.getDescription()).isEqualTo(subseaInfrastructure.getDescription());
    assertThat(subseaInfrastructureView.getStatus()).isEqualTo(subseaInfrastructure.getStatus().getDisplayName());
  }

  @Test
  public void from_withDevUkFacility() {

    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withDevUkFacility();
    var subseaInfrastructureView = SubseaInfrastructureViewUtil.from(subseaInfrastructure, DISPLAY_ORDER, IS_VALID);

    var structure = subseaInfrastructureView.getStructure();
    assertThat(structure.getValue()).isEqualTo(subseaInfrastructure.getFacility().getSelectionText());
    assertThat(structure.getTag()).isEqualTo(Tag.NONE);
    checkCommonFields(subseaInfrastructureView, DISPLAY_ORDER, IS_VALID, subseaInfrastructure);
  }

  @Test
  public void from_withManualFacility() {

    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();
    var subseaInfrastructureView = SubseaInfrastructureViewUtil.from(subseaInfrastructure, DISPLAY_ORDER, IS_VALID);

    var structure = subseaInfrastructureView.getStructure();
    assertThat(structure.getValue()).isEqualTo(subseaInfrastructure.getManualFacility());
    assertThat(structure.getTag()).isEqualTo(Tag.NOT_FROM_LIST);
    checkCommonFields(subseaInfrastructureView, DISPLAY_ORDER, IS_VALID, subseaInfrastructure);
  }

  @Test
  public void from_withDecommissioningPeriodProvided() {

    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();
    subseaInfrastructure.setEarliestDecommissioningStartYear(2019);
    subseaInfrastructure.setLatestDecommissioningCompletionYear(2020);

    var subseaInfrastructureView = SubseaInfrastructureViewUtil.from(subseaInfrastructure, DISPLAY_ORDER, IS_VALID);

    assertThat(subseaInfrastructureView.getEarliestDecommissioningStartYear()).isEqualTo(
        String.format(SubseaInfrastructureViewUtil.EARLIEST_DECOM_YEAR_TEXT, subseaInfrastructure.getEarliestDecommissioningStartYear())
    );
    assertThat(subseaInfrastructureView.getLatestDecommissioningCompletionYear()).isEqualTo(
        String.format(SubseaInfrastructureViewUtil.LATEST_DECOM_YEAR_TEXT, subseaInfrastructure.getLatestDecommissioningCompletionYear())
    );

    checkCommonFields(subseaInfrastructureView, DISPLAY_ORDER, IS_VALID, subseaInfrastructure);
  }

  @Test
  public void from_withDecommissioningPeriodEmpty() {

    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();
    subseaInfrastructure.setEarliestDecommissioningStartYear(null);
    subseaInfrastructure.setLatestDecommissioningCompletionYear(null);

    var subseaInfrastructureView = SubseaInfrastructureViewUtil.from(subseaInfrastructure, DISPLAY_ORDER, IS_VALID);

    assertThat(subseaInfrastructureView.getEarliestDecommissioningStartYear()).isEqualTo(
        String.format(SubseaInfrastructureViewUtil.EARLIEST_DECOM_YEAR_TEXT, SubseaInfrastructureViewUtil.DEFAULT_DECOM_YEAR_TEXT)
    );
    assertThat(subseaInfrastructureView.getLatestDecommissioningCompletionYear()).isEqualTo(
        String.format(SubseaInfrastructureViewUtil.LATEST_DECOM_YEAR_TEXT, SubseaInfrastructureViewUtil.DEFAULT_DECOM_YEAR_TEXT)
    );

    checkCommonFields(subseaInfrastructureView, DISPLAY_ORDER, IS_VALID, subseaInfrastructure);
  }

  @Test
  public void from_withConcreteMattressInfrastructureType() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withConcreteMattresses();

    var subseaInfrastructureView = SubseaInfrastructureViewUtil.from(subseaInfrastructure, DISPLAY_ORDER, IS_VALID);

    assertThat(subseaInfrastructureView.getInfrastructureType()).isEqualTo(subseaInfrastructure.getInfrastructureType().getDisplayName());
    assertThat(subseaInfrastructureView.getNumberOfMattresses()).isEqualTo(subseaInfrastructure.getNumberOfMattresses());
    assertThat(subseaInfrastructureView.getTotalEstimatedMattressMass()).isEqualTo(
        SubseaInfrastructureViewUtil.getMassString(subseaInfrastructure.getTotalEstimatedMattressMass())
    );
    assertThat(subseaInfrastructureView.getTotalEstimatedSubseaMass()).isNull();
    assertThat(subseaInfrastructureView.getOtherInfrastructureType()).isNull();
    assertThat(subseaInfrastructureView.getTotalEstimatedOtherMass()).isNull();

    checkCommonFields(subseaInfrastructureView, DISPLAY_ORDER, IS_VALID, subseaInfrastructure);
  }

  @Test
  public void from_withConcreteMattressInfrastructureType_whenNullTotalEstimatesMattressMass() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withConcreteMattresses();
    subseaInfrastructure.setTotalEstimatedMattressMass(null);

    var subseaInfrastructureView = SubseaInfrastructureViewUtil.from(subseaInfrastructure, DISPLAY_ORDER, IS_VALID);

    assertThat(subseaInfrastructureView.getInfrastructureType()).isEqualTo(subseaInfrastructure.getInfrastructureType().getDisplayName());
    assertThat(subseaInfrastructureView.getNumberOfMattresses()).isEqualTo(subseaInfrastructure.getNumberOfMattresses());
    assertThat(subseaInfrastructureView.getTotalEstimatedMattressMass()).isEqualTo("");
    assertThat(subseaInfrastructureView.getTotalEstimatedSubseaMass()).isNull();
    assertThat(subseaInfrastructureView.getOtherInfrastructureType()).isNull();
    assertThat(subseaInfrastructureView.getTotalEstimatedOtherMass()).isNull();

    checkCommonFields(subseaInfrastructureView, DISPLAY_ORDER, IS_VALID, subseaInfrastructure);
  }

  @Test
  public void from_withSubseaStructureInfrastructureType() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withSubseaStructure();

    var subseaInfrastructureView = SubseaInfrastructureViewUtil.from(subseaInfrastructure, DISPLAY_ORDER, IS_VALID);

    assertThat(subseaInfrastructureView.getInfrastructureType()).isEqualTo(subseaInfrastructure.getInfrastructureType().getDisplayName());
    assertThat(subseaInfrastructureView.getTotalEstimatedSubseaMass()).isEqualTo(subseaInfrastructure.getTotalEstimatedSubseaMass().getDisplayName());
    assertThat(subseaInfrastructureView.getNumberOfMattresses()).isNull();
    assertThat(subseaInfrastructureView.getTotalEstimatedMattressMass()).isNull();
    assertThat(subseaInfrastructureView.getOtherInfrastructureType()).isNull();
    assertThat(subseaInfrastructureView.getTotalEstimatedOtherMass()).isNull();

    checkCommonFields(subseaInfrastructureView, DISPLAY_ORDER, IS_VALID, subseaInfrastructure);
  }

  @Test
  public void from_withOtherInfrastructureInfrastructureType() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withOtherInfrastructure();

    var subseaInfrastructureView = SubseaInfrastructureViewUtil.from(subseaInfrastructure, DISPLAY_ORDER, IS_VALID);

    assertThat(subseaInfrastructureView.getInfrastructureType()).isEqualTo(subseaInfrastructure.getInfrastructureType().getDisplayName());
    assertThat(subseaInfrastructureView.getOtherInfrastructureType()).isEqualTo(subseaInfrastructure.getOtherInfrastructureType());
    assertThat(subseaInfrastructureView.getTotalEstimatedOtherMass()).isEqualTo(
        SubseaInfrastructureViewUtil.getMassString(subseaInfrastructure.getTotalEstimatedOtherMass())
    );
    assertThat(subseaInfrastructureView.getTotalEstimatedSubseaMass()).isNull();
    assertThat(subseaInfrastructureView.getNumberOfMattresses()).isNull();
    assertThat(subseaInfrastructureView.getTotalEstimatedMattressMass()).isNull();

    checkCommonFields(subseaInfrastructureView, DISPLAY_ORDER, IS_VALID, subseaInfrastructure);
  }

  @Test
  public void from_withOtherInfrastructureInfrastructureType_whenNullTotalEstimatesOtherMass() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withOtherInfrastructure();
    subseaInfrastructure.setTotalEstimatedOtherMass(null);

    var subseaInfrastructureView = SubseaInfrastructureViewUtil.from(subseaInfrastructure, DISPLAY_ORDER, IS_VALID);

    assertThat(subseaInfrastructureView.getInfrastructureType()).isEqualTo(subseaInfrastructure.getInfrastructureType().getDisplayName());
    assertThat(subseaInfrastructureView.getOtherInfrastructureType()).isEqualTo(subseaInfrastructure.getOtherInfrastructureType());
    assertThat(subseaInfrastructureView.getTotalEstimatedOtherMass()).isEqualTo("");
    assertThat(subseaInfrastructureView.getTotalEstimatedSubseaMass()).isNull();
    assertThat(subseaInfrastructureView.getNumberOfMattresses()).isNull();
    assertThat(subseaInfrastructureView.getTotalEstimatedMattressMass()).isNull();

    checkCommonFields(subseaInfrastructureView, DISPLAY_ORDER, IS_VALID, subseaInfrastructure);
  }

  @Test
  public void from_withNullInfrastructureType() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();
    subseaInfrastructure.setInfrastructureType(null);

    var subseaInfrastructureView = SubseaInfrastructureViewUtil.from(subseaInfrastructure, DISPLAY_ORDER, IS_VALID);

    assertThat(subseaInfrastructureView.getNumberOfMattresses()).isNull();
    assertThat(subseaInfrastructureView.getTotalEstimatedMattressMass()).isNull();
    assertThat(subseaInfrastructureView.getTotalEstimatedSubseaMass()).isNull();
    assertThat(subseaInfrastructureView.getOtherInfrastructureType()).isNull();
    assertThat(subseaInfrastructureView.getTotalEstimatedOtherMass()).isNull();

    checkCommonFields(subseaInfrastructureView, DISPLAY_ORDER, IS_VALID, subseaInfrastructure);
  }

  @Test
  public void from_withoutIsValidSet_defaultIsTrue() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();

    var subseaInfrastructureView = SubseaInfrastructureViewUtil.from(subseaInfrastructure, 2);
    checkCommonFields(subseaInfrastructureView, 2, IS_VALID, subseaInfrastructure);
  }

  @Test
  public void from_whenIsValidFalse_thenFalseInView() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withManualFacility();

    var subseaInfrastructureView = SubseaInfrastructureViewUtil.from(subseaInfrastructure, DISPLAY_ORDER, false);
    checkCommonFields(subseaInfrastructureView, DISPLAY_ORDER, false, subseaInfrastructure);
  }
}