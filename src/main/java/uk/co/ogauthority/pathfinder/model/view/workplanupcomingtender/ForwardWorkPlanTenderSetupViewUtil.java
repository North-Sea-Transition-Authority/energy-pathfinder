package uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender;

import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanTenderSetup;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class ForwardWorkPlanTenderSetupViewUtil {

  private ForwardWorkPlanTenderSetupViewUtil() {
    throw new IllegalStateException("ForwardWorkPlanTenderSetupViewUtil is a utility class and should not be instantiated");
  }

  public static ForwardWorkPlanTenderSetupView from(ForwardWorkPlanTenderSetup forwardWorkPlanTenderSetup) {
    final var setupView = new ForwardWorkPlanTenderSetupView();

    final var hasTendersToAdd = StringDisplayUtil.yesNoFromBoolean(forwardWorkPlanTenderSetup.getHasTendersToAdd());
    setupView.setHasTendersToAdd(hasTendersToAdd);

    return setupView;
  }
}
