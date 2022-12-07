package uk.co.ogauthority.pathfinder.model.view.awardedcontract;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class AwardedContractViewUtilTest {

  private static final Integer DISPLAY_ORDER = 1;
  private final PortalOrganisationGroup addedByPortalOrganisationGroup =
      TeamTestingUtil.generateOrganisationGroup(1, "org", "org");

  @Test
  public void awardedContractViewBuilder_withFromListFunction() {
    var awardedContract= AwardedContractTestUtil.createAwardedContract();
    awardedContract.setContractFunction(Function.DRILLING);
    awardedContract.setManualContractFunction(null);

    AwardedContractView awardedContractView = new AwardedContractViewUtil.AwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .build();
    checkCommonFields(awardedContract, awardedContractView, DISPLAY_ORDER);
    var contractFunction = awardedContractView.getContractFunction();
    assertThat(contractFunction.getValue()).isEqualTo(awardedContract.getContractFunction().getDisplayName());
    assertThat(contractFunction.getTag()).isEqualTo(Tag.NONE);
    assertThat(awardedContractView.isValid()).isTrue();
  }

  @Test
  public void awardedContractViewBuilder_withManualEntryFunction() {
    var awardedContract= AwardedContractTestUtil.createAwardedContract();
    awardedContract.setContractFunction(null);
    awardedContract.setManualContractFunction("my manual entry function");

    AwardedContractView awardedContractView = new AwardedContractViewUtil.AwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .build();

    checkCommonFields(awardedContract, awardedContractView, DISPLAY_ORDER);
    var contractFunction = awardedContractView.getContractFunction();
    assertThat(contractFunction.getValue()).isEqualTo(awardedContract.getManualContractFunction());
    assertThat(contractFunction.getTag()).isEqualTo(Tag.NOT_FROM_LIST);
  }

  @Test
  public void awardedContractViewBuilder_withNullContractBand() {
    var awardedContract= AwardedContractTestUtil.createAwardedContract();
    awardedContract.setContractBand(null);

    AwardedContractView awardedContractView = new AwardedContractViewUtil.AwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .build();

    checkCommonFields(awardedContract, awardedContractView, DISPLAY_ORDER);
    assertThat(awardedContractView.getContractBand()).isNull();
  }

  @Test
  public void awardedContractViewBuilder_withNotNullContractBand() {
    var awardedContract= AwardedContractTestUtil.createAwardedContract();
    awardedContract.setContractBand(ContractBand.GREATER_THAN_OR_EQUAL_TO_25M);

    AwardedContractView awardedContractView = new AwardedContractViewUtil.AwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .build();

    checkCommonFields(awardedContract, awardedContractView, DISPLAY_ORDER);
    assertThat(awardedContractView.getContractBand()).isEqualTo(awardedContract.getContractBand().getDisplayName());
  }

  @Test
  public void awardedContractViewBuilder_containsCorrectSummaryLinks() {
    var awardedContract= AwardedContractTestUtil.createAwardedContract();
    var projectId = awardedContract.getProjectDetail().getProject().getId();
    var awardedContractId = awardedContract.getId();

    AwardedContractView awardedContractView = new AwardedContractViewUtil.AwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(true)
        .build();

    var summaryLinks = awardedContractView.getSummaryLinks();

    checkCommonFields(awardedContract, awardedContractView, DISPLAY_ORDER);
    assertThat(summaryLinks).containsExactly(
        AwardedContractViewUtil.getEditLink(projectId, awardedContractId),
        AwardedContractViewUtil.getDeleteLink(projectId, awardedContractId, DISPLAY_ORDER)
    );
  }

  @Test
  public void awardedContractViewBuilder_containsNoSummaryLinks() {
    var awardedContract= AwardedContractTestUtil.createAwardedContract();

    AwardedContractView awardedContractView = new AwardedContractViewUtil.AwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(false)
        .build();

    var summaryLinks = awardedContractView.getSummaryLinks();

    checkCommonFields(awardedContract, awardedContractView, DISPLAY_ORDER);
    assertThat(summaryLinks).isEmpty();
  }

  @Test
  public void awardedContractViewBuilder_whenIsValidTruePassedIn_validPropertyIsSetTrue() {
    var awardedContract= AwardedContractTestUtil.createAwardedContract();
    AwardedContractView awardedContractView = new AwardedContractViewUtil.AwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .isValid(true)
        .build();

    checkCommonFields(awardedContract, awardedContractView, DISPLAY_ORDER);
    assertThat(awardedContractView.isValid()).isTrue();
  }

  @Test
  public void awardedContractViewBuilder_whenIsValidFalsePassedIn_validPropertyIsSetFalse() {
    var awardedContract= AwardedContractTestUtil.createAwardedContract();
    AwardedContractView awardedContractView = new AwardedContractViewUtil.AwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        addedByPortalOrganisationGroup
    )
        .isValid(false)
        .build();

    checkCommonFields(awardedContract, awardedContractView, DISPLAY_ORDER);
    assertThat(awardedContractView.isValid()).isFalse();
  }

  @Test
  public void awardedContractViewBuilder_whenEmptyAddedByPortalOrgGroup_thenDefaultAddedByString() {
    var awardedContract= AwardedContractTestUtil.createAwardedContract();
    var emptyPortalOrganisationGroup = new PortalOrganisationGroup();
    AwardedContractView awardedContractView = new AwardedContractViewUtil.AwardedContractViewBuilder(
        awardedContract,
        DISPLAY_ORDER,
        emptyPortalOrganisationGroup
    )
        .isValid(true)
        .build();

    assertThat(awardedContractView.getAddedByPortalOrganisationGroup()).isEqualTo("Unknown organisation");
  }

  private void checkCommonFields(AwardedContract source, AwardedContractView destination, Integer displayOrder) {
    assertThat(destination.getDisplayOrder()).isEqualTo(displayOrder);
    assertThat(destination.getContractorName()).isEqualTo(source.getContractorName());
    assertThat(destination.getDescriptionOfWork()).isEqualTo(source.getDescriptionOfWork());
    assertThat(destination.getDateAwarded()).isEqualTo(DateUtil.formatDate(source.getDateAwarded()));

    assertThat(destination.getContactName()).isEqualTo(source.getContactName());
    assertThat(destination.getContactPhoneNumber()).isEqualTo(source.getPhoneNumber());
    assertThat(destination.getContactEmailAddress()).isEqualTo(source.getEmailAddress());
    assertThat(destination.getContactJobTitle()).isEqualTo(source.getJobTitle());
    assertThat(destination.getAddedByPortalOrganisationGroup()).isEqualTo(addedByPortalOrganisationGroup.getName());
  }

}