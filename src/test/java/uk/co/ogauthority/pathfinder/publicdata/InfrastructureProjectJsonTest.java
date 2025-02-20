package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.utils.MapUtils;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.CampaignInformationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.CampaignProjectTestUtil;
import uk.co.ogauthority.pathfinder.testutil.CommissionedWellTestUtil;
import uk.co.ogauthority.pathfinder.testutil.DecommissionedPipelineTestUtil;
import uk.co.ogauthority.pathfinder.testutil.DecommissioningScheduleTestUtil;
import uk.co.ogauthority.pathfinder.testutil.InfrastructureCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.IntegratedRigTestUtil;
import uk.co.ogauthority.pathfinder.testutil.LicenceBlockTestUtil;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.PlugAbandonmentScheduleTestUtil;
import uk.co.ogauthority.pathfinder.testutil.PlugAbandonmentWellTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.SubseaInfrastructureTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderFileLinkUtil;
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

    var upcomingTenderFileLink = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink(1, upcomingTender1);

    var infrastructureAwardedContract1 = AwardedContractTestUtil.createInfrastructureAwardedContract(1, projectDetail);
    var infrastructureAwardedContract2 = AwardedContractTestUtil.createInfrastructureAwardedContract(2, projectDetail);

    var infrastructureCollaborationOpportunity1 =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(1, projectDetail);
    var infrastructureCollaborationOpportunity2 =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(2, projectDetail);

    var infrastructureCollaborationOpportunityFileLink = InfrastructureCollaborationOpportunityTestUtil
        .createCollaborationOpportunityFileLink(1, infrastructureCollaborationOpportunity1);

    var campaignInformation = CampaignInformationTestUtil.createCampaignInformation(1, projectDetail);

    var campaignProject1 = CampaignProjectTestUtil.newBuilder().withId(1).withCampaignInformation(campaignInformation).build();
    var campaignProject2 = CampaignProjectTestUtil.newBuilder().withId(2).withCampaignInformation(campaignInformation).build();

    var commissionedWellSchedule1 = CommissionedWellTestUtil.getCommissionedWellSchedule(1, projectDetail);
    var commissionedWellSchedule2 = CommissionedWellTestUtil.getCommissionedWellSchedule(2, projectDetail);

    var commissionedWell1 = CommissionedWellTestUtil.getCommissionedWell(1, commissionedWellSchedule1);
    var commissionedWell2 = CommissionedWellTestUtil.getCommissionedWell(2, commissionedWellSchedule1);

    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule(projectDetail);

    var plugAbandonmentSchedule1 = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule(1, projectDetail);
    var plugAbandonmentSchedule2 = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule(2, projectDetail);

    var plugAbandonmentWell1 = PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(1, plugAbandonmentSchedule1);
    var plugAbandonmentWell2 = PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(2, plugAbandonmentSchedule1);

    var platformFpso1 = PlatformFpsoTestUtil.getPlatformFpso(1, projectDetail);
    var platformFpso2 = PlatformFpsoTestUtil.getPlatformFpso(2, projectDetail);

    var integratedRig1 = IntegratedRigTestUtil.createIntegratedRig(1, projectDetail);
    var integratedRig2 = IntegratedRigTestUtil.createIntegratedRig(2, projectDetail);

    var subseaInfrastructure1 = SubseaInfrastructureTestUtil.createSubseaInfrastructure(1, projectDetail);
    var subseaInfrastructure2 = SubseaInfrastructureTestUtil.createSubseaInfrastructure(2, projectDetail);

    var decommissionedPipeline1 = DecommissionedPipelineTestUtil.createDecommissionedPipeline(1, projectDetail);
    var decommissionedPipeline2 = DecommissionedPipelineTestUtil.createDecommissionedPipeline(2, projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        projectLocation,
        projectLocationBlocks,
        MapUtils.of(
            upcomingTender1, upcomingTenderFileLink,
            upcomingTender2, null
        ),
        List.of(infrastructureAwardedContract1, infrastructureAwardedContract2),
        MapUtils.of(
            infrastructureCollaborationOpportunity1, infrastructureCollaborationOpportunityFileLink,
            infrastructureCollaborationOpportunity2, null
        ),
        campaignInformation,
        List.of(campaignProject1, campaignProject2),
        MapUtils.of(
            commissionedWellSchedule1, List.of(commissionedWell1, commissionedWell2),
            commissionedWellSchedule2, null
        ),
        decommissioningSchedule,
        MapUtils.of(
            plugAbandonmentSchedule1, List.of(plugAbandonmentWell1, plugAbandonmentWell2),
            plugAbandonmentSchedule2, null
        ),
        List.of(platformFpso1, platformFpso2),
        List.of(integratedRig1, integratedRig2),
        List.of(subseaInfrastructure1, subseaInfrastructure2),
        List.of(decommissionedPipeline1, decommissionedPipeline2)
    );

    var expectedInfrastructureProjectJson = new InfrastructureProjectJson(
        projectDetail.getProject().getId(),
        InfrastructureProjectDetailsJson.from(projectOperator, projectInformation),
        ContactJson.from(projectInformation),
        null,
        InfrastructureProjectLocationJson.from(projectLocation, projectLocationBlocks),
        Set.of(
            InfrastructureProjectUpcomingTenderJson.from(upcomingTender1, upcomingTenderFileLink),
            InfrastructureProjectUpcomingTenderJson.from(upcomingTender2, null)
        ),
        Set.of(
            AwardedContractJson.from(infrastructureAwardedContract1),
            AwardedContractJson.from(infrastructureAwardedContract2)
        ),
        Set.of(
            CollaborationOpportunityJson
                .from(infrastructureCollaborationOpportunity1, infrastructureCollaborationOpportunityFileLink),
            CollaborationOpportunityJson.from(infrastructureCollaborationOpportunity2, null)
        ),
        InfrastructureProjectCampaignJson.from(campaignInformation, List.of(campaignProject1, campaignProject2)),
        Set.of(
            InfrastructureProjectWellScheduleJson.from(commissionedWellSchedule1, List.of(commissionedWell1, commissionedWell2)),
            InfrastructureProjectWellScheduleJson.from(commissionedWellSchedule2, null)
        ),
        InfrastructureProjectDecommissioningScheduleJson.from(decommissioningSchedule),
        Set.of(
            InfrastructureProjectWellScheduleJson.from(plugAbandonmentSchedule1, List.of(plugAbandonmentWell1, plugAbandonmentWell2)),
            InfrastructureProjectWellScheduleJson.from(plugAbandonmentSchedule2, null)
        ),
        Set.of(
            InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson.from(platformFpso1),
            InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson.from(platformFpso2)
        ),
        Set.of(
            InfrastructureProjectIntegratedRigToBeDecommissionedJson.from(integratedRig1),
            InfrastructureProjectIntegratedRigToBeDecommissionedJson.from(integratedRig2)
        ),
        Set.of(
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson.from(subseaInfrastructure1),
            InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson.from(subseaInfrastructure2)
        ),
        Set.of(
            InfrastructureProjectPipelineToBeDecommissionedJson.from(decommissionedPipeline1),
            InfrastructureProjectPipelineToBeDecommissionedJson.from(decommissionedPipeline2)
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
        null,
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
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.firstProductionDate())
        .isEqualTo(QuarterYearJson.from(projectInformation.getFirstProductionDateQuarter(), projectInformation.getFirstProductionDateYear()));
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
        null,
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
        null,
        null,
        null,
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
  void from_upcomingTenderToFileLinkIsNull() {
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
        null,
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
  void from_upcomingTenderToFileLinkIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var upcomingTender1 = UpcomingTenderUtil.getUpcomingTender(1, projectDetail);
    var upcomingTender2 = UpcomingTenderUtil.getUpcomingTender(2, projectDetail);

    var upcomingTenderFileLink = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink(1, upcomingTender1);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        MapUtils.of(
            upcomingTender1, upcomingTenderFileLink,
            upcomingTender2, null
        ),
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.upcomingTenders()).containsExactlyInAnyOrder(
        InfrastructureProjectUpcomingTenderJson.from(upcomingTender1, upcomingTenderFileLink),
        InfrastructureProjectUpcomingTenderJson.from(upcomingTender2, null)
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
        null,
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
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.awardedContracts()).containsExactlyInAnyOrder(
        AwardedContractJson.from(infrastructureAwardedContract1),
        AwardedContractJson.from(infrastructureAwardedContract2)
    );
  }

  @Test
  void from_infrastructureCollaborationOpportunityToFileLinkIsNull() {
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
        null,
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
  void from_infrastructureCollaborationOpportunityToFileLinkIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var infrastructureCollaborationOpportunity1 =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(1, projectDetail);
    var infrastructureCollaborationOpportunity2 =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(2, projectDetail);

    var infrastructureCollaborationOpportunityFileLink = InfrastructureCollaborationOpportunityTestUtil
        .createCollaborationOpportunityFileLink(1, infrastructureCollaborationOpportunity1);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        null,
        null,
        MapUtils.of(
            infrastructureCollaborationOpportunity1, infrastructureCollaborationOpportunityFileLink,
            infrastructureCollaborationOpportunity2, null
        ),
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.collaborationOpportunities()).containsExactlyInAnyOrder(
        CollaborationOpportunityJson
            .from(infrastructureCollaborationOpportunity1, infrastructureCollaborationOpportunityFileLink),
        CollaborationOpportunityJson.from(infrastructureCollaborationOpportunity2, null)
    );
  }

  @Test
  void from_campaignInformationIsNull() {
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
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.campaign()).isNull();
  }

  @Test
  void from_campaignInformationIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var campaignInformation = CampaignInformationTestUtil.createCampaignInformation(1, projectDetail);

    var campaignProject1 = CampaignProjectTestUtil.newBuilder().withId(1).withCampaignInformation(campaignInformation).build();
    var campaignProject2 = CampaignProjectTestUtil.newBuilder().withId(2).withCampaignInformation(campaignInformation).build();

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null,
        null,
        null,
        null,
        campaignInformation,
        List.of(campaignProject1, campaignProject2),
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.campaign())
        .isEqualTo(InfrastructureProjectCampaignJson.from(campaignInformation, List.of(campaignProject1, campaignProject2)));
  }

  @Test
  void from_commissionedWellScheduleToWellsIsNull() {
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
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.wellCommissioningSchedules()).isNull();
  }

  @Test
  void from_commissionedWellScheduleToWellsIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var commissionedWellSchedule1 = CommissionedWellTestUtil.getCommissionedWellSchedule(1, projectDetail);
    var commissionedWellSchedule2 = CommissionedWellTestUtil.getCommissionedWellSchedule(2, projectDetail);

    var commissionedWell1 = CommissionedWellTestUtil.getCommissionedWell(1, commissionedWellSchedule1);
    var commissionedWell2 = CommissionedWellTestUtil.getCommissionedWell(2, commissionedWellSchedule1);

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
        null,
        MapUtils.of(
            commissionedWellSchedule1, List.of(commissionedWell1, commissionedWell2),
            commissionedWellSchedule2, null
        ),
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.wellCommissioningSchedules()).containsExactlyInAnyOrder(
        InfrastructureProjectWellScheduleJson.from(commissionedWellSchedule1, List.of(commissionedWell1, commissionedWell2)),
        InfrastructureProjectWellScheduleJson.from(commissionedWellSchedule2, null)
    );
  }

  @Test
  void from_decommissioningScheduleIsNull() {
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
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.decommissioningSchedule()).isNull();
  }

  @Test
  void from_decommissioningScheduleIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var decommissioningSchedule = DecommissioningScheduleTestUtil.createDecommissioningSchedule(projectDetail);

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
        null,
        null,
        decommissioningSchedule,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.decommissioningSchedule())
        .isEqualTo(InfrastructureProjectDecommissioningScheduleJson.from(decommissioningSchedule));
  }

  @Test
  void from_plugAbandonmentScheduleToWellsIsNull() {
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
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.wellDecommissioningSchedules()).isNull();
  }

  @Test
  void from_plugAbandonmentScheduleToWellsIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var plugAbandonmentSchedule1 = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule(1, projectDetail);
    var plugAbandonmentSchedule2 = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule(2, projectDetail);

    var plugAbandonmentWell1 = PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(1, plugAbandonmentSchedule1);
    var plugAbandonmentWell2 = PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(2, plugAbandonmentSchedule1);

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
        null,
        null,
        null,
        MapUtils.of(
            plugAbandonmentSchedule1, List.of(plugAbandonmentWell1, plugAbandonmentWell2),
            plugAbandonmentSchedule2, null
        ),
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.wellDecommissioningSchedules()).containsExactlyInAnyOrder(
        InfrastructureProjectWellScheduleJson.from(plugAbandonmentSchedule1, List.of(plugAbandonmentWell1, plugAbandonmentWell2)),
        InfrastructureProjectWellScheduleJson.from(plugAbandonmentSchedule2, null)
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
        null,
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
        null,
        null,
        null,
        null,
        null,
        List.of(platformFpso1, platformFpso2),
        null,
        null,
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
        null,
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
        null,
        null,
        null,
        null,
        null,
        List.of(integratedRig1, integratedRig2),
        null,
        null
    );

    assertThat(infrastructureProjectJson.integratedRigsToBeDecommissioned()).containsExactlyInAnyOrder(
        InfrastructureProjectIntegratedRigToBeDecommissionedJson.from(integratedRig1),
        InfrastructureProjectIntegratedRigToBeDecommissionedJson.from(integratedRig2)
    );
  }

  @Test
  void from_subseaInfrastructuresIsNull() {
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
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.subseaInfrastructuresToBeDecommissioned()).isNull();
  }

  @Test
  void from_subseaInfrastructuresIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var subseaInfrastructure1 = SubseaInfrastructureTestUtil.createSubseaInfrastructure(1, projectDetail);
    var subseaInfrastructure2 = SubseaInfrastructureTestUtil.createSubseaInfrastructure(2, projectDetail);

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
        null,
        null,
        null,
        null,
        null,
        null,
        List.of(subseaInfrastructure1, subseaInfrastructure2),
        null
    );

    assertThat(infrastructureProjectJson.subseaInfrastructuresToBeDecommissioned()).containsExactlyInAnyOrder(
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson.from(subseaInfrastructure1),
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson.from(subseaInfrastructure2)
    );
  }

  @Test
  void from_decommissionedPipelinesIsNull() {
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
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertThat(infrastructureProjectJson.pipelinesToBeDecommissioned()).isNull();
  }

  @Test
  void from_decommissionedPipelinesIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var decommissionedPipeline1 = DecommissionedPipelineTestUtil.createDecommissionedPipeline(1, projectDetail);
    var decommissionedPipeline2 = DecommissionedPipelineTestUtil.createDecommissionedPipeline(2, projectDetail);

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
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        List.of(decommissionedPipeline1, decommissionedPipeline2)
    );

    assertThat(infrastructureProjectJson.pipelinesToBeDecommissioned()).containsExactlyInAnyOrder(
        InfrastructureProjectPipelineToBeDecommissionedJson.from(decommissionedPipeline1),
        InfrastructureProjectPipelineToBeDecommissionedJson.from(decommissionedPipeline2)
    );
  }
}
