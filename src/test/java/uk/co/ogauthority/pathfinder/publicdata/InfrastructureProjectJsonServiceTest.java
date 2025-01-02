package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorRepository;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.infrastructure.InfrastructureAwardedContractRepository;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesRepository;
import uk.co.ogauthority.pathfinder.repository.project.integratedrig.IntegratedRigRepository;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationBlockRepository;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationRepository;
import uk.co.ogauthority.pathfinder.repository.project.platformsfpsos.PlatformFpsoRepository;
import uk.co.ogauthority.pathfinder.repository.project.projectinformation.ProjectInformationRepository;
import uk.co.ogauthority.pathfinder.repository.project.subseainfrastructure.SubseaInfrastructureRepository;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.InfrastructureCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.IntegratedRigTestUtil;
import uk.co.ogauthority.pathfinder.testutil.LicenceBlockTestUtil;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.SubseaInfrastructureTestUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
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
  private InfrastructureAwardedContractRepository infrastructureAwardedContractRepository;

  @Mock
  private InfrastructureCollaborationOpportunitiesRepository infrastructureCollaborationOpportunitiesRepository;

  @Mock
  private PlatformFpsoRepository platformFpsoRepository;

  @Mock
  private IntegratedRigRepository integratedRigRepository;

  @Mock
  private SubseaInfrastructureRepository subseaInfrastructureRepository;

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

    var infrastructureAwardedContract1 = AwardedContractTestUtil.createInfrastructureAwardedContract(1, projectDetail1);
    var infrastructureAwardedContract2 = AwardedContractTestUtil.createInfrastructureAwardedContract(2, projectDetail1);
    var infrastructureAwardedContract3 = AwardedContractTestUtil.createInfrastructureAwardedContract(3, projectDetail2);

    var infrastructureCollaborationOpportunity1 =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(1, projectDetail1);
    var infrastructureCollaborationOpportunity2 =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(2, projectDetail1);
    var infrastructureCollaborationOpportunity3 =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(3, projectDetail2);

    var platformFpso1 = PlatformFpsoTestUtil.getPlatformFpso(1, projectDetail1);
    var platformFpso2 = PlatformFpsoTestUtil.getPlatformFpso(2, projectDetail1);
    var platformFpso3 = PlatformFpsoTestUtil.getPlatformFpso(3, projectDetail2);

    var integratedRig1 = IntegratedRigTestUtil.createIntegratedRig(1, projectDetail1);
    var integratedRig2 = IntegratedRigTestUtil.createIntegratedRig(2, projectDetail1);
    var integratedRig3 = IntegratedRigTestUtil.createIntegratedRig(3, projectDetail2);

    var subseaInfrastructure1 = SubseaInfrastructureTestUtil.createSubseaInfrastructure(1, projectDetail1);
    var subseaInfrastructure2 = SubseaInfrastructureTestUtil.createSubseaInfrastructure(2, projectDetail1);
    var subseaInfrastructure3 = SubseaInfrastructureTestUtil.createSubseaInfrastructure(3, projectDetail2);

    when(projectDetailsRepository.getAllPublishedProjectDetailsByProjectType(ProjectType.INFRASTRUCTURE))
        .thenReturn(List.of(projectDetail1, projectDetail2, projectDetail3));

    when(projectOperatorRepository.findAll()).thenReturn(List.of(projectOperator1, projectOperator2, projectOperator3));

    when(projectInformationRepository.findAll())
        .thenReturn(List.of(projectInformation1, projectInformation2, projectInformation3));

    when(projectLocationRepository.findAll()).thenReturn(List.of(projectLocation1, projectLocation2, projectLocation3));

    when(projectLocationBlockRepository.findAll())
        .thenReturn(List.of(projectLocationBlock1, projectLocationBlock2, projectLocationBlock3, projectLocationBlock4));

    when(upcomingTenderRepository.findAll()).thenReturn(List.of(upcomingTender1, upcomingTender2, upcomingTender3));

    when(infrastructureAwardedContractRepository.findAll())
        .thenReturn(List.of(infrastructureAwardedContract1, infrastructureAwardedContract2, infrastructureAwardedContract3));

    when(infrastructureCollaborationOpportunitiesRepository.findAll()).thenReturn(
        List.of(
            infrastructureCollaborationOpportunity1,
            infrastructureCollaborationOpportunity2,
            infrastructureCollaborationOpportunity3
        )
    );

    when(platformFpsoRepository.findAll()).thenReturn(List.of(platformFpso1, platformFpso2, platformFpso3));

    when(integratedRigRepository.findAll()).thenReturn(List.of(integratedRig1, integratedRig2, integratedRig3));

    when(subseaInfrastructureRepository.findAll())
        .thenReturn(List.of(subseaInfrastructure1, subseaInfrastructure2, subseaInfrastructure3));

    var infrastructureProjectJsons = infrastructureProjectJsonService.getPublishedInfrastructureProjects();

    assertThat(infrastructureProjectJsons).containsExactlyInAnyOrder(
        InfrastructureProjectJson.from(
            projectDetail1,
            projectOperator1,
            projectInformation1,
            projectLocation1,
            List.of(projectLocationBlock1, projectLocationBlock2),
            List.of(upcomingTender1, upcomingTender2),
            List.of(infrastructureAwardedContract1, infrastructureAwardedContract2),
            List.of(infrastructureCollaborationOpportunity1, infrastructureCollaborationOpportunity2),
            List.of(platformFpso1, platformFpso2),
            List.of(integratedRig1, integratedRig2),
            List.of(subseaInfrastructure1, subseaInfrastructure2)
        ),
        InfrastructureProjectJson.from(
            projectDetail2,
            projectOperator2,
            projectInformation2,
            projectLocation2,
            List.of(projectLocationBlock3, projectLocationBlock4),
            List.of(upcomingTender3),
            List.of(infrastructureAwardedContract3),
            List.of(infrastructureCollaborationOpportunity3),
            List.of(platformFpso3),
            List.of(integratedRig3),
            List.of(subseaInfrastructure3)
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
            null,
            null,
            null
        )
    );
  }
}
