package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.utils.MapUtils;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorRepository;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.infrastructure.InfrastructureAwardedContractRepository;
import uk.co.ogauthority.pathfinder.repository.project.campaigninformation.CampaignInformationRepository;
import uk.co.ogauthority.pathfinder.repository.project.campaigninformation.CampaignProjectRepository;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesRepository;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFileLinkRepository;
import uk.co.ogauthority.pathfinder.repository.project.commissionedwell.CommissionedWellRepository;
import uk.co.ogauthority.pathfinder.repository.project.commissionedwell.CommissionedWellScheduleRepository;
import uk.co.ogauthority.pathfinder.repository.project.decommissionedpipeline.DecommissionedPipelineRepository;
import uk.co.ogauthority.pathfinder.repository.project.decommissioningschedule.DecommissioningScheduleRepository;
import uk.co.ogauthority.pathfinder.repository.project.integratedrig.IntegratedRigRepository;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationBlockRepository;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationRepository;
import uk.co.ogauthority.pathfinder.repository.project.platformsfpsos.PlatformFpsoRepository;
import uk.co.ogauthority.pathfinder.repository.project.plugabandonmentschedule.PlugAbandonmentScheduleRepository;
import uk.co.ogauthority.pathfinder.repository.project.plugabandonmentschedule.PlugAbandonmentWellRepository;
import uk.co.ogauthority.pathfinder.repository.project.projectinformation.ProjectInformationRepository;
import uk.co.ogauthority.pathfinder.repository.project.subseainfrastructure.SubseaInfrastructureRepository;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderFileLinkRepository;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderRepository;
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
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderFileLinkUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;

@ExtendWith(MockitoExtension.class)
class InfrastructureProjectJsonServiceTest {

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  @Mock
  private ProjectOperatorRepository projectOperatorRepository;

  @Mock
  private ProjectInformationRepository projectInformationRepository;

  @Mock
  private ProjectLocationRepository projectLocationRepository;

  @Mock
  private ProjectLocationBlockRepository projectLocationBlockRepository;

  @Mock
  private UpcomingTenderRepository upcomingTenderRepository;

  @Mock
  private UpcomingTenderFileLinkRepository upcomingTenderFileLinkRepository;

  @Mock
  private InfrastructureAwardedContractRepository infrastructureAwardedContractRepository;

  @Mock
  private InfrastructureCollaborationOpportunitiesRepository infrastructureCollaborationOpportunitiesRepository;

  @Mock
  private InfrastructureCollaborationOpportunityFileLinkRepository infrastructureCollaborationOpportunityFileLinkRepository;

  @Mock
  private CampaignInformationRepository campaignInformationRepository;

  @Mock
  private CampaignProjectRepository campaignProjectRepository;

  @Mock
  private CommissionedWellScheduleRepository commissionedWellScheduleRepository;

  @Mock
  private CommissionedWellRepository commissionedWellRepository;

  @Mock
  private DecommissioningScheduleRepository decommissioningScheduleRepository;

  @Mock
  private PlugAbandonmentScheduleRepository plugAbandonmentScheduleRepository;

  @Mock
  private PlugAbandonmentWellRepository plugAbandonmentWellRepository;

  @Mock
  private PlatformFpsoRepository platformFpsoRepository;

  @Mock
  private IntegratedRigRepository integratedRigRepository;

  @Mock
  private SubseaInfrastructureRepository subseaInfrastructureRepository;

  @Mock
  private DecommissionedPipelineRepository decommissionedPipelineRepository;

  @InjectMocks
  private InfrastructureProjectJsonService infrastructureProjectJsonService;

  @Test
  void getPublishedInfrastructureProjects() {
    var projectDetail1 = ProjectUtil.getPublishedProjectDetails();
    projectDetail1.setId(1);
    projectDetail1.getProject().setId(2);

    var projectDetail2 = ProjectUtil.getPublishedProjectDetails();
    projectDetail2.setId(3);
    projectDetail2.getProject().setId(4);

    var projectDetail3 = ProjectUtil.getPublishedProjectDetails();
    projectDetail2.setId(5);
    projectDetail2.getProject().setId(6);

    var projectOperator1 = ProjectOperatorTestUtil.getOperator(projectDetail1);
    projectOperator1.setOrganisationGroup(
        TeamTestingUtil.generateOrganisationGroup(1, "A Org Grp", "AOrgGrp"));

    var projectOperator2 = ProjectOperatorTestUtil.getOperator(projectDetail2);
    projectOperator2.setOrganisationGroup(
        TeamTestingUtil.generateOrganisationGroup(2, "b Org Grp", "brgGrp"));

    var projectOperator3 = ProjectOperatorTestUtil.getOperator(projectDetail3);
    projectOperator3.setOrganisationGroup(
        TeamTestingUtil.generateOrganisationGroup(3, "C Org Grp", "COrgGrp"));

    var projectInformation1 = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail1);
    projectInformation1.setProjectTitle("A project");

    var projectInformation2 = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail2);
    projectInformation2.setProjectTitle("b project");

    var projectInformation3 = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail3);
    projectInformation3.setProjectTitle("C project");

    var projectLocation1 = ProjectLocationTestUtil.getProjectLocation(projectDetail1);
    projectLocation1.setFieldType(FieldType.OIL);

    var projectLocation2 = ProjectLocationTestUtil.getProjectLocation(projectDetail2);
    projectLocation2.setFieldType(FieldType.GAS);

    var projectLocation3 = ProjectLocationTestUtil.getProjectLocation(projectDetail3);
    projectLocation3.setFieldType(FieldType.CARBON_STORAGE);

    var projectLocationBlock1 = LicenceBlockTestUtil.getProjectLocationBlock(projectLocation1, "12/34");
    var projectLocationBlock2 = LicenceBlockTestUtil.getProjectLocationBlock(projectLocation1, "12/56");
    var projectLocationBlock3 = LicenceBlockTestUtil.getProjectLocationBlock(projectLocation2, "14/82");
    var projectLocationBlock4 = LicenceBlockTestUtil.getProjectLocationBlock(projectLocation2, "18/93");

    var upcomingTender1 = UpcomingTenderUtil.getUpcomingTender(1, projectDetail1);
    var upcomingTender2 = UpcomingTenderUtil.getUpcomingTender(2, projectDetail1);
    var upcomingTender3 = UpcomingTenderUtil.getUpcomingTender(3, projectDetail2);

    var upcomingTenderFileLink1 = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink(1, upcomingTender1);
    var upcomingTenderFileLink2 = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink(2, upcomingTender2);

    var infrastructureAwardedContract1 = AwardedContractTestUtil.createInfrastructureAwardedContract(1, projectDetail1);
    var infrastructureAwardedContract2 = AwardedContractTestUtil.createInfrastructureAwardedContract(2, projectDetail1);
    var infrastructureAwardedContract3 = AwardedContractTestUtil.createInfrastructureAwardedContract(3, projectDetail2);

    var infrastructureCollaborationOpportunity1 =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(1, projectDetail1);
    var infrastructureCollaborationOpportunity2 =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(2, projectDetail1);
    var infrastructureCollaborationOpportunity3 =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(3, projectDetail2);

    var infrastructureCollaborationOpportunityFileLink1 = InfrastructureCollaborationOpportunityTestUtil
        .createCollaborationOpportunityFileLink(1, infrastructureCollaborationOpportunity1);
    var infrastructureCollaborationOpportunityFileLink2 = InfrastructureCollaborationOpportunityTestUtil
        .createCollaborationOpportunityFileLink(2, infrastructureCollaborationOpportunity2);

    var campaignInformation1 = CampaignInformationTestUtil.createCampaignInformation(1, projectDetail1);
    var campaignInformation2 = CampaignInformationTestUtil.createCampaignInformation(2, projectDetail2);
    var campaignInformation3 = CampaignInformationTestUtil.createCampaignInformation(3, projectDetail3);

    var campaignProject1 = CampaignProjectTestUtil.newBuilder().withId(1).withCampaignInformation(campaignInformation1).build();
    var campaignProject2 = CampaignProjectTestUtil.newBuilder().withId(2).withCampaignInformation(campaignInformation1).build();
    var campaignProject3 = CampaignProjectTestUtil.newBuilder().withId(3).withCampaignInformation(campaignInformation2).build();

    var commissionedWellSchedule1 = CommissionedWellTestUtil.getCommissionedWellSchedule(1, projectDetail1);
    var commissionedWellSchedule2 = CommissionedWellTestUtil.getCommissionedWellSchedule(2, projectDetail1);
    var commissionedWellSchedule3 = CommissionedWellTestUtil.getCommissionedWellSchedule(3, projectDetail2);

    var commissionedWell1 = CommissionedWellTestUtil.getCommissionedWell(1, commissionedWellSchedule1);
    var commissionedWell2 = CommissionedWellTestUtil.getCommissionedWell(2, commissionedWellSchedule1);
    var commissionedWell3 = CommissionedWellTestUtil.getCommissionedWell(3, commissionedWellSchedule2);

    var decommissioningSchedule1 = DecommissioningScheduleTestUtil.createDecommissioningSchedule(1, projectDetail1);
    var decommissioningSchedule2 = DecommissioningScheduleTestUtil.createDecommissioningSchedule(2, projectDetail2);

    var plugAbandonmentSchedule1 = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule(1, projectDetail1);
    var plugAbandonmentSchedule2 = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule(2, projectDetail1);
    var plugAbandonmentSchedule3 = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule(3, projectDetail2);

    var plugAbandonmentWell1 = PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(1, plugAbandonmentSchedule1);
    var plugAbandonmentWell2 = PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(2, plugAbandonmentSchedule1);
    var plugAbandonmentWell3 = PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(3, plugAbandonmentSchedule2);

    var platformFpso1 = PlatformFpsoTestUtil.getPlatformFpso(1, projectDetail1);
    var platformFpso2 = PlatformFpsoTestUtil.getPlatformFpso(2, projectDetail1);
    var platformFpso3 = PlatformFpsoTestUtil.getPlatformFpso(3, projectDetail2);

    var integratedRig1 = IntegratedRigTestUtil.createIntegratedRig(1, projectDetail1);
    var integratedRig2 = IntegratedRigTestUtil.createIntegratedRig(2, projectDetail1);
    var integratedRig3 = IntegratedRigTestUtil.createIntegratedRig(3, projectDetail2);

    var subseaInfrastructure1 = SubseaInfrastructureTestUtil.createSubseaInfrastructure(1, projectDetail1);
    var subseaInfrastructure2 = SubseaInfrastructureTestUtil.createSubseaInfrastructure(2, projectDetail1);
    var subseaInfrastructure3 = SubseaInfrastructureTestUtil.createSubseaInfrastructure(3, projectDetail2);

    var decommissionedPipeline1 = DecommissionedPipelineTestUtil.createDecommissionedPipeline(1, projectDetail1);
    var decommissionedPipeline2 = DecommissionedPipelineTestUtil.createDecommissionedPipeline(2, projectDetail1);
    var decommissionedPipeline3 = DecommissionedPipelineTestUtil.createDecommissionedPipeline(3, projectDetail2);

    when(projectDetailsRepository.getAllPublishedProjectDetailsByProjectTypes(EnumSet.of(ProjectType.INFRASTRUCTURE)))
        .thenReturn(List.of(projectDetail1, projectDetail2, projectDetail3));

    when(projectOperatorRepository.findAll()).thenReturn(List.of(projectOperator1, projectOperator2, projectOperator3));

    when(projectInformationRepository.findAll())
        .thenReturn(List.of(projectInformation1, projectInformation2, projectInformation3));

    when(projectLocationRepository.findAll()).thenReturn(List.of(projectLocation1, projectLocation2, projectLocation3));

    when(projectLocationBlockRepository.findAll())
        .thenReturn(List.of(projectLocationBlock1, projectLocationBlock2, projectLocationBlock3, projectLocationBlock4));

    when(upcomingTenderRepository.findAll()).thenReturn(List.of(upcomingTender1, upcomingTender2, upcomingTender3));

    when(upcomingTenderFileLinkRepository.findAll()).thenReturn(List.of(upcomingTenderFileLink1, upcomingTenderFileLink2));

    when(infrastructureAwardedContractRepository.findAll())
        .thenReturn(List.of(infrastructureAwardedContract1, infrastructureAwardedContract2, infrastructureAwardedContract3));

    when(infrastructureCollaborationOpportunitiesRepository.findAll()).thenReturn(
        List.of(
            infrastructureCollaborationOpportunity1,
            infrastructureCollaborationOpportunity2,
            infrastructureCollaborationOpportunity3
        )
    );

    when(infrastructureCollaborationOpportunityFileLinkRepository.findAll())
        .thenReturn(List.of(infrastructureCollaborationOpportunityFileLink1, infrastructureCollaborationOpportunityFileLink2));

    when(campaignInformationRepository.findAll())
        .thenReturn(List.of(campaignInformation1, campaignInformation2, campaignInformation3));

    when(campaignProjectRepository.findAll()).thenReturn(List.of(campaignProject1, campaignProject2, campaignProject3));

    when(commissionedWellRepository.findAll()).thenReturn(List.of(commissionedWell1, commissionedWell2, commissionedWell3));

    when(commissionedWellScheduleRepository.findAll())
        .thenReturn(List.of(commissionedWellSchedule1, commissionedWellSchedule2, commissionedWellSchedule3));

    when(decommissioningScheduleRepository.findAll()).thenReturn(List.of(decommissioningSchedule1, decommissioningSchedule2));

    when(plugAbandonmentWellRepository.findAll())
        .thenReturn(List.of(plugAbandonmentWell1, plugAbandonmentWell2, plugAbandonmentWell3));

    when(plugAbandonmentScheduleRepository.findAll())
        .thenReturn(List.of(plugAbandonmentSchedule1, plugAbandonmentSchedule2, plugAbandonmentSchedule3));

    when(platformFpsoRepository.findAll()).thenReturn(List.of(platformFpso1, platformFpso2, platformFpso3));

    when(integratedRigRepository.findAll()).thenReturn(List.of(integratedRig1, integratedRig2, integratedRig3));

    when(subseaInfrastructureRepository.findAll())
        .thenReturn(List.of(subseaInfrastructure1, subseaInfrastructure2, subseaInfrastructure3));

    when(decommissionedPipelineRepository.findAll())
        .thenReturn(List.of(decommissionedPipeline1, decommissionedPipeline2, decommissionedPipeline3));

    var infrastructureProjectJsons = infrastructureProjectJsonService.getPublishedInfrastructureProjects();

    assertThat(infrastructureProjectJsons).containsExactlyInAnyOrder(
        InfrastructureProjectJson.from(
            projectDetail1,
            projectOperator1,
            projectInformation1,
            projectLocation1,
            List.of(projectLocationBlock1, projectLocationBlock2),
            Map.of(
                upcomingTender1, upcomingTenderFileLink1,
                upcomingTender2, upcomingTenderFileLink2
            ),
            List.of(infrastructureAwardedContract1, infrastructureAwardedContract2),
            Map.of(
                infrastructureCollaborationOpportunity1, infrastructureCollaborationOpportunityFileLink1,
                infrastructureCollaborationOpportunity2, infrastructureCollaborationOpportunityFileLink2
            ),
            campaignInformation1,
            List.of(campaignProject1, campaignProject2),
            Map.of(
                commissionedWellSchedule1, List.of(commissionedWell1, commissionedWell2),
                commissionedWellSchedule2, List.of(commissionedWell3)
            ),
            decommissioningSchedule1,
            Map.of(
                plugAbandonmentSchedule1, List.of(plugAbandonmentWell1, plugAbandonmentWell2),
                plugAbandonmentSchedule2, List.of(plugAbandonmentWell3)
            ),
            List.of(platformFpso1, platformFpso2),
            List.of(integratedRig1, integratedRig2),
            List.of(subseaInfrastructure1, subseaInfrastructure2),
            List.of(decommissionedPipeline1, decommissionedPipeline2)
        ),
        InfrastructureProjectJson.from(
            projectDetail2,
            projectOperator2,
            projectInformation2,
            projectLocation2,
            List.of(projectLocationBlock3, projectLocationBlock4),
            MapUtils.of(
                upcomingTender3, null
            ),
            List.of(infrastructureAwardedContract3),
            MapUtils.of(
                infrastructureCollaborationOpportunity3, null
            ),
            campaignInformation2,
            List.of(campaignProject3),
            MapUtils.of(
                commissionedWellSchedule3, null
            ),
            decommissioningSchedule2,
            MapUtils.of(
                plugAbandonmentSchedule3, null
            ),
            List.of(platformFpso3),
            List.of(integratedRig3),
            List.of(subseaInfrastructure3),
            List.of(decommissionedPipeline3)
        ),
        InfrastructureProjectJson.from(
            projectDetail3,
            projectOperator3,
            projectInformation3,
            projectLocation3,
            null,
            null,
            null,
            null,
            campaignInformation3,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    );
  }
}
