package uk.co.ogauthority.pathfinder.model.view.integratedrig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.integratedrig.IntegratedRigController;
import uk.co.ogauthority.pathfinder.model.entity.project.integratedrig.IntegratedRig;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.IntegratedRigTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class IntegratedRigViewUtilTest {

  private static final Integer DISPLAY_ORDER = 1;
  private static final boolean IS_VALID = true;

  @Test
  public void from_withFromListStructure_thenNoTag() {

    final var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withDevUkFacility();
    final var integratedRigView = IntegratedRigViewUtil.from(integratedRig, DISPLAY_ORDER, IS_VALID);

    assertThat(integratedRigView.getStructure()).isEqualTo(
        new StringWithTag(integratedRig.getFacility().getSelectionText(), Tag.NONE)
    );
    checkCommonFields(integratedRigView, integratedRig, DISPLAY_ORDER, IS_VALID);

  }

  @Test
  public void from_withNotFromListStructure_thenNotFromListTag() {

    final var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withManualFacility();
    final var integratedRigView = IntegratedRigViewUtil.from(integratedRig, DISPLAY_ORDER, IS_VALID);

    assertThat(integratedRigView.getStructure()).isEqualTo(
        new StringWithTag(integratedRig.getManualFacility(), Tag.NOT_FROM_LIST)
    );
    checkCommonFields(integratedRigView, integratedRig, DISPLAY_ORDER, IS_VALID);

  }

  @Test
  public void from_withNullStatus_thenStatusViewPropertyIsNull() {

    var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withDevUkFacility();
    integratedRig.setStatus(null);

    final var integratedRigView = IntegratedRigViewUtil.from(integratedRig, DISPLAY_ORDER, IS_VALID);
    assertThat(integratedRigView.getStatus()).isNull();

  }

  @Test
  public void from_withNullIntentionToReactive_thenIntentionToReactiveViewPropertyIsNull() {

    var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withDevUkFacility();
    integratedRig.setIntentionToReactivate(null);

    final var integratedRigView = IntegratedRigViewUtil.from(integratedRig, DISPLAY_ORDER, IS_VALID);
    assertThat(integratedRigView.getIntentionToReactivate()).isNull();

  }

  @Test
  public void from_whenIsValidTrue_thenIsValidInViewTrue() {
    final var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withDevUkFacility();
    final var isValid = IS_VALID;
    final var integratedRigView = IntegratedRigViewUtil.from(integratedRig, DISPLAY_ORDER, isValid);
    checkCommonFields(integratedRigView, integratedRig, DISPLAY_ORDER, isValid);
  }

  @Test
  public void from_whenIsValidFalse_thenIsValidInViewFalse() {
    final var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withDevUkFacility();
    final var isValid = false;
    final var integratedRigView = IntegratedRigViewUtil.from(integratedRig, DISPLAY_ORDER, isValid);
    checkCommonFields(integratedRigView, integratedRig, DISPLAY_ORDER, isValid);
  }

  private void checkCommonFields(IntegratedRigView integratedRigView,
                                 IntegratedRig integratedRig,
                                 Integer displayOrder,
                                 boolean isValid) {
    assertThat(integratedRigView.getId()).isEqualTo(integratedRig.getId());
    assertThat(integratedRigView.getDisplayOrder()).isEqualTo(displayOrder);
    assertThat(integratedRigView.getName()).isEqualTo(integratedRig.getName());
    assertThat(integratedRigView.getStatus()).isEqualTo(integratedRig.getStatus().getDisplayName());
    assertThat(integratedRigView.getIntentionToReactivate()).isEqualTo(integratedRig.getIntentionToReactivate().getDisplayName());
    assertThat(integratedRigView.getValid()).isEqualTo(isValid);

    var editLink = new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(IntegratedRigController.class).getIntegratedRig(
            integratedRig.getProjectDetail().getProject().getId(),
            integratedRig.getId(),
            null
        ))
    );

    var removeLink = new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(IntegratedRigController.class).removeIntegratedRig(
            integratedRig.getProjectDetail().getProject().getId(),
            integratedRig.getId(),
            displayOrder,
            null
        ))
    );

    assertThat(integratedRigView.getSummaryLinks()).containsExactly(editLink, removeLink);

  }

}