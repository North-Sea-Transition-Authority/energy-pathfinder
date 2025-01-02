package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.InfrastructureCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.IntegratedRigTestUtil;
import uk.co.ogauthority.pathfinder.testutil.LicenceBlockTestUtil;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;

class InfrastructureProjectJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var projectLocationBlocks = List.of(
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "12/34"),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "12/56")
    );

    var upcomingTender1 = UpcomingTenderUtil.getUpcomingTender(1, projectDetail);
    var upcomingTender2 = UpcomingTenderUtil.getUpcomingTender(2, projectDetail);

    var infrastructureAwardedContract1 = AwardedContractTestUtil.createInfrastructureAwardedContract(1, projectDetail);
    var infrastructureAwardedContract2 = AwardedContractTestUtil.createInfrastructureAwardedContract(2, projectDetail);

    var infrastructureCollaborationOpportunity1 =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(1, projectDetail);
    var infrastructureCollaborationOpportunity2 =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(2, projectDetail);

    var platformFpso1 = PlatformFpsoTestUtil.getPlatformFpso(1, projectDetail);
    var platformFpso2 = PlatformFpsoTestUtil.getPlatformFpso(2, projectDetail);

    var integratedRig1 = IntegratedRigTestUtil.createIntegratedRig(1, projectDetail);
    var integratedRig2 = IntegratedRigTestUtil.createIntegratedRig(2, projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        projectLocation,
        projectLocationBlocks,
        List.of(upcomingTender1, upcomingTender2),
        List.of(infrastructureAwardedContract1, infrastructureAwardedContract2),
        List.of(infrastructureCollaborationOpportunity1, infrastructureCollaborationOpportunity2),
        List.of(platformFpso1, platformFpso2),
        List.of(integratedRig1, integratedRig2)
    );

    var expectedInfrastructureProjectJson = new InfrastructureProjectJson(
        projectDetail.getProject().getId(),
        InfrastructureProjectDetailsJson.from(projectOperator, projectInformation),
        ContactJson.from(projectInformation),
        null,
        InfrastructureProjectLocationJson.from(projectLocation, projectLocationBlocks),
        Set.of(
            InfrastructureProjectUpcomingTenderJson.from(upcomingTender1),
            InfrastructureProjectUpcomingTenderJson.from(upcomingTender2)
        ),
        Set.of(
            InfrastructureProjectAwardedContractJson.from(infrastructureAwardedContract1),
            InfrastructureProjectAwardedContractJson.from(infrastructureAwardedContract2)
        ),
        Set.of(
            InfrastructureProjectCollaborationOpportunityJson.from(infrastructureCollaborationOpportunity1),
            InfrastructureProjectCollaborationOpportunityJson.from(infrastructureCollaborationOpportunity2)
        ),
        Set.of(
            InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson.from(platformFpso1),
            InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson.from(platformFpso2)
        ),
        Set.of(
            InfrastructureProjectIntegratedRigToBeDecommissionedJson.from(integratedRig1),
            InfrastructureProjectIntegratedRigToBeDecommissionedJson.from(integratedRig2)
        ),
        LocalDateTime.ofInstant(projectDetail.getSubmittedInstant(), ZoneId.systemDefault())
    );

    assertThat(infrastructureProjectJson).isEqualTo(expectedInfrastructureProjectJson);
  }

  @Test
  void from_firstProductionDateQuarterAndYearIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    projectInformation.setFirstProductionDateQuarter(null);
    projectInformation.setFirstProductionDateYear(null);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.firstProductionDate()).isNull();
  }

  @Test
  void from_firstProductionDateQuarterAndYearIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    projectInformation.setFirstProductionDateQuarter(Quarter.Q1);
    projectInformation.setFirstProductionDateYear(2025);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.firstProductionDate())
        .isEqualTo(InfrastructureProjectFirstProductionDateJson.from(projectInformation));
  }

  @Test
  void from_projectLocationIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.location()).isNull();
  }

  @Test
  void from_projectLocationIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var projectLocationBlocks = List.of(
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "12/34"),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "12/56")
    );

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        projectLocation,
        projectLocationBlocks,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.location())
        .isEqualTo(InfrastructureProjectLocationJson.from(projectLocation, projectLocationBlocks));
  }

  @Test
  void from_upcomingTendersListIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.upcomingTenders()).isNull();
  }

  @Test
  void from_upcomingTendersListIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var upcomingTender1 = UpcomingTenderUtil.getUpcomingTender(1, projectDetail);
    var upcomingTender2 = UpcomingTenderUtil.getUpcomingTender(2, projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        List.of(upcomingTender1, upcomingTender2),
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.upcomingTenders()).containsExactlyInAnyOrder(
        InfrastructureProjectUpcomingTenderJson.from(upcomingTender1),
        InfrastructureProjectUpcomingTenderJson.from(upcomingTender2)
    );
  }

  @Test
  void from_infrastructureAwardedContractsIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.awardedContracts()).isNull();
  }

  @Test
  void from_infrastructureAwardedContractsIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var infrastructureAwardedContract1 = AwardedContractTestUtil.createInfrastructureAwardedContract(1, projectDetail);
    var infrastructureAwardedContract2 = AwardedContractTestUtil.createInfrastructureAwardedContract(2, projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        null,
        List.of(infrastructureAwardedContract1, infrastructureAwardedContract2),
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.awardedContracts()).containsExactlyInAnyOrder(
        InfrastructureProjectAwardedContractJson.from(infrastructureAwardedContract1),
        InfrastructureProjectAwardedContractJson.from(infrastructureAwardedContract2)
    );
  }

  @Test
  void from_infrastructureCollaborationOpportunitiesIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.collaborationOpportunities()).isNull();
  }

  @Test
  void from_infrastructureCollaborationOpportunitiesIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var infrastructureCollaborationOpportunity1 =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(1, projectDetail);
    var infrastructureCollaborationOpportunity2 =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(2, projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        null,
        null,
        List.of(infrastructureCollaborationOpportunity1, infrastructureCollaborationOpportunity2),
        null,
        null
    );

    assertThat(infrastructureProjectJson.collaborationOpportunities()).containsExactlyInAnyOrder(
        InfrastructureProjectCollaborationOpportunityJson.from(infrastructureCollaborationOpportunity1),
        InfrastructureProjectCollaborationOpportunityJson.from(infrastructureCollaborationOpportunity2)
    );
  }

  @Test
  void from_platformFpsosIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.platformOrFpsosToBeDecommissioned()).isNull();
  }

  @Test
  void from_platformFpsosIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var platformFpso1 = PlatformFpsoTestUtil.getPlatformFpso(1, projectDetail);
    var platformFpso2 = PlatformFpsoTestUtil.getPlatformFpso(2, projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        null,
        null,
        null,
        List.of(platformFpso1, platformFpso2),
        null
    );

    assertThat(infrastructureProjectJson.platformOrFpsosToBeDecommissioned()).containsExactlyInAnyOrder(
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson.from(platformFpso1),
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson.from(platformFpso2)
    );
  }

  @Test
  void from_integratedRigsIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.integratedRigsToBeDecommissioned()).isNull();
  }

  @Test
  void from_integratedRigsIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var integratedRig1 = IntegratedRigTestUtil.createIntegratedRig(1, projectDetail);
    var integratedRig2 = IntegratedRigTestUtil.createIntegratedRig(2, projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        null,
        null,
        null,
        null,
        List.of(integratedRig1, integratedRig2)
    );

    assertThat(infrastructureProjectJson.integratedRigsToBeDecommissioned()).containsExactlyInAnyOrder(
        InfrastructureProjectIntegratedRigToBeDecommissionedJson.from(integratedRig1),
        InfrastructureProjectIntegratedRigToBeDecommissionedJson.from(integratedRig2)
    );
  }
}
