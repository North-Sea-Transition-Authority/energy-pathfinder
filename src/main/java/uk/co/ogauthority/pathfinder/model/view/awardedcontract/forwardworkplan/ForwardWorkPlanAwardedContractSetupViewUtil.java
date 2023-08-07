package uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan;

import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetup;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class ForwardWorkPlanAwardedContractSetupViewUtil {

  private ForwardWorkPlanAwardedContractSetupViewUtil() {
    throw new IllegalStateException("ForwardWorkPlanAwardedContractSetupViewUtil is a utility class and should not be instantiated");
  }

  public static ForwardWorkPlanAwardedContractSetupView from(ForwardWorkPlanAwardedContractSetup awardedContractSetup) {
    var setUpView = new ForwardWorkPlanAwardedContractSetupView();

    var hasContractsToAdd = StringDisplayUtil.yesNoFromBoolean(awardedContractSetup.getHasContractToAdd());
    setUpView.setHasContractsToAdd(hasContractsToAdd);
    return setUpView;
  }
}
