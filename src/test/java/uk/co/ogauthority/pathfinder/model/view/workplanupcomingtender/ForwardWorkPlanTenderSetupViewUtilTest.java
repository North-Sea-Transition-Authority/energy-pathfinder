package uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanTenderSetup;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanTenderSetupViewUtilTest {

  @Test
  public void from_whenHasTendersToAddIsNull_thenEmptyString() {

    final var setup = new ForwardWorkPlanTenderSetup();
    setup.setHasTendersToAdd(null);

    final var setupView = ForwardWorkPlanTenderSetupViewUtil.from(setup);

    assertThat(setupView.getHasTendersToAdd()).isEqualTo(StringUtils.EMPTY);
  }

  @Test
  public void from_whenHasTendersToAddIsTrue_thenYesString() {

    final var setup = new ForwardWorkPlanTenderSetup();
    setup.setHasTendersToAdd(true);

    final var setupView = ForwardWorkPlanTenderSetupViewUtil.from(setup);

    assertThat(setupView.getHasTendersToAdd()).isEqualTo(StringDisplayUtil.YES);
  }

  @Test
  public void from_whenHasTendersToAddIsFalse_thenNoString() {

    final var setup = new ForwardWorkPlanTenderSetup();
    setup.setHasTendersToAdd(false);

    final var setupView = ForwardWorkPlanTenderSetupViewUtil.from(setup);

    assertThat(setupView.getHasTendersToAdd()).isEqualTo(StringDisplayUtil.NO);

  }

}