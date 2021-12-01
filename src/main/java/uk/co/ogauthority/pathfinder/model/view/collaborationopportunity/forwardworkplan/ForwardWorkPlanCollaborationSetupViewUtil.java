package uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan;

import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetup;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class ForwardWorkPlanCollaborationSetupViewUtil {

  private ForwardWorkPlanCollaborationSetupViewUtil() {
    throw new IllegalStateException("ForwardWorkPlanCollaborationSetupViewUtil is a utility class and should not be instantiated");
  }

  public static ForwardWorkPlanCollaborationSetupView from(ForwardWorkPlanCollaborationSetup forwardWorkPlanCollaborationSetup) {
    final var setupView = new ForwardWorkPlanCollaborationSetupView();

    final var hasCollaborationsToAdd = StringDisplayUtil.yesNoFromBoolean(forwardWorkPlanCollaborationSetup.getHasCollaborationToAdd());
    setupView.setHasCollaborationsToAdd(hasCollaborationsToAdd);

    return setupView;
  }
}
