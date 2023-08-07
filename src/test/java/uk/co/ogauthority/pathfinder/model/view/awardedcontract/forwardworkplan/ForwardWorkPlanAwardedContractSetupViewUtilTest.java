package uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@ExtendWith(MockitoExtension.class)
class ForwardWorkPlanAwardedContractSetupViewUtilTest {
  
  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  @NullSource
  void from(Boolean hasContractsToAdd) {
    var detail = ProjectUtil.getProjectDetails();
    var contractSetUp = AwardedContractTestUtil.createForwardWorkPlanAwardedContractSetup(detail);
    contractSetUp.setHasContractToAdd(hasContractsToAdd);

    var setUpView = ForwardWorkPlanAwardedContractSetupViewUtil.from(contractSetUp);
    var expectedHasContractToSetUp = StringDisplayUtil.yesNoFromBoolean(hasContractsToAdd);
    assertThat(setUpView.getHasContractsToAdd()).isEqualTo(expectedHasContractToSetUp);
  }
  
}
