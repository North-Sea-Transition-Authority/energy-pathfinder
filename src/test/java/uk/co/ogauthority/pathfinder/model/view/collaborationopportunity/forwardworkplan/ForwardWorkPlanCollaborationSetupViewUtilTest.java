package uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetup;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationSetupViewUtilTest {

  @Test
  public void from_whenHasCollaborationToAddTrue_thenStringYesInView() {

    final var setupEntity = new ForwardWorkPlanCollaborationSetup();
    setupEntity.setHasCollaborationToAdd(true);

    final var resultingSetupView = ForwardWorkPlanCollaborationSetupViewUtil.from(setupEntity);

    assertThat(resultingSetupView.getHasCollaborationsToAdd()).isEqualTo(StringDisplayUtil.YES);
  }

  @Test
  public void from_whenHasCollaborationToAddFalse_thenStringNoInView() {

    final var setupEntity = new ForwardWorkPlanCollaborationSetup();
    setupEntity.setHasCollaborationToAdd(false);

    final var resultingSetupView = ForwardWorkPlanCollaborationSetupViewUtil.from(setupEntity);

    assertThat(resultingSetupView.getHasCollaborationsToAdd()).isEqualTo(StringDisplayUtil.NO);
  }

  @Test
  public void from_whenHasCollaborationToAddNull_thenEmptyStringInView() {

    final var setupEntity = new ForwardWorkPlanCollaborationSetup();
    setupEntity.setHasCollaborationToAdd(null);

    final var resultingSetupView = ForwardWorkPlanCollaborationSetupViewUtil.from(setupEntity);

    assertThat(resultingSetupView.getHasCollaborationsToAdd()).isEqualTo(StringUtils.EMPTY);
  }

}