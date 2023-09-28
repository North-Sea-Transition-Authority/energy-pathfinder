package uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@ExtendWith(MockitoExtension.class)
class ForwardWorkPlanAwardedContractViewUtilTest {

  private static final Integer DISPLAY_ORDER = 1;
  private final PortalOrganisationGroup addedByPortalOrganisationGroup =
      TeamTestingUtil.generateOrganisationGroup(1, "org", "org");

  @Test
  void awardedContractViewBuilder_withFromListFunction() {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setContractFunction(Function.DRILLING);
    awardedContract.setManualContractFunction(null);

    var awardedContractView = new ForwardWorkPlanAwardedContractViewUtil.ForwardWorkPlanAwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .build();
    AwardedContractTestUtil.checkCommonViewFields(awardedContract, awardedContractView, DISPLAY_ORDER, addedByPortalOrganisationGroup);
    var contractFunction = awardedContractView.getContractFunction();
    assertThat(contractFunction.getValue()).isEqualTo(awardedContract.getContractFunction().getDisplayName());
    assertThat(contractFunction.getTag()).isEqualTo(Tag.NONE);
    assertThat(awardedContractView.isValid()).isTrue();
  }

  @Test
  void awardedContractViewBuilder_withManualEntryFunction() {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setContractFunction(null);
    awardedContract.setManualContractFunction("my manual entry function");

    var awardedContractView = new ForwardWorkPlanAwardedContractViewUtil.ForwardWorkPlanAwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .build();

    AwardedContractTestUtil.checkCommonViewFields(awardedContract, awardedContractView, DISPLAY_ORDER, addedByPortalOrganisationGroup);
    var contractFunction = awardedContractView.getContractFunction();
    assertThat(contractFunction.getValue()).isEqualTo(awardedContract.getManualContractFunction());
    assertThat(contractFunction.getTag()).isEqualTo(Tag.NOT_FROM_LIST);
  }

  @Test
  void awardedContractViewBuilder_withNullContractBand() {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setContractBand(null);

    var awardedContractView = new ForwardWorkPlanAwardedContractViewUtil.ForwardWorkPlanAwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .build();

    AwardedContractTestUtil.checkCommonViewFields(awardedContract, awardedContractView, DISPLAY_ORDER, addedByPortalOrganisationGroup);
    assertThat(awardedContractView.getContractBand()).isNull();
  }

  @Test
  void awardedContractViewBuilder_withNotNullContractBand() {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setContractBand(ContractBand.GREATER_THAN_OR_EQUAL_TO_25M);

    var awardedContractView = new ForwardWorkPlanAwardedContractViewUtil.ForwardWorkPlanAwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .build();

    AwardedContractTestUtil.checkCommonViewFields(awardedContract, awardedContractView, DISPLAY_ORDER, addedByPortalOrganisationGroup);
    assertThat(awardedContractView.getContractBand()).isEqualTo(awardedContract.getContractBand().getDisplayName());
  }

  @Test
  void awardedContractViewBuilder_containsCorrectSummaryLinks() {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    var projectId = awardedContract.getProjectDetail().getProject().getId();
    var awardedContractId = awardedContract.getId();

    var awardedContractView = new ForwardWorkPlanAwardedContractViewUtil.ForwardWorkPlanAwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(true)
        .build();

    var summaryLinks = awardedContractView.getSummaryLinks();

    AwardedContractTestUtil.checkCommonViewFields(awardedContract, awardedContractView, DISPLAY_ORDER, addedByPortalOrganisationGroup);
    assertThat(summaryLinks).containsExactly(
        ForwardWorkPlanAwardedContractViewUtil.getEditLink(projectId, awardedContractId),
        ForwardWorkPlanAwardedContractViewUtil.getDeleteLink(projectId, awardedContractId, DISPLAY_ORDER)
    );
  }

  @Test
  void awardedContractViewBuilder_containsNoSummaryLinks() {
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();

    var awardedContractView = new ForwardWorkPlanAwardedContractViewUtil.ForwardWorkPlanAwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(false)
        .build();

    var summaryLinks = awardedContractView.getSummaryLinks();

    AwardedContractTestUtil.checkCommonViewFields(awardedContract, awardedContractView, DISPLAY_ORDER, addedByPortalOrganisationGroup);
    assertThat(summaryLinks).isEmpty();
  }

  @Test
  void awardedContractViewBuilder_whenIsValidTruePassedIn_validPropertyIsSetTrue() {
    var awardedContract= AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    var awardedContractView = new ForwardWorkPlanAwardedContractViewUtil.ForwardWorkPlanAwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .isValid(true)
        .build();

    AwardedContractTestUtil.checkCommonViewFields(awardedContract, awardedContractView, DISPLAY_ORDER, addedByPortalOrganisationGroup);
    assertThat(awardedContractView.isValid()).isTrue();
  }

  @Test
  void awardedContractViewBuilder_whenIsValidFalsePassedIn_validPropertyIsSetFalse() {
    var awardedContract= AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    var awardedContractView = new ForwardWorkPlanAwardedContractViewUtil.ForwardWorkPlanAwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .isValid(false)
        .build();

    AwardedContractTestUtil.checkCommonViewFields(awardedContract, awardedContractView, DISPLAY_ORDER, addedByPortalOrganisationGroup);
    assertThat(awardedContractView.isValid()).isFalse();
  }

  @Test
  void awardedContractViewBuilder_whenEmptyAddedByPortalOrgGroup_thenDefaultAddedByString() {
    var awardedContract= AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    var emptyPortalOrganisationGroup = new PortalOrganisationGroup();
    var awardedContractView = new ForwardWorkPlanAwardedContractViewUtil.ForwardWorkPlanAwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        emptyPortalOrganisationGroup
    )
        .isValid(true)
        .build();

    assertThat(awardedContractView.getAddedByPortalOrganisationGroup()).isEqualTo("Unknown organisation");
  }
}
