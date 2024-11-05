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
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationBlockRepository;
import uk.co.ogauthority.pathfinder.repository.project.location.ProjectLocationRepository;
import uk.co.ogauthority.pathfinder.repository.project.projectinformation.ProjectInformationRepository;
import uk.co.ogauthority.pathfinder.testutil.LicenceBlockTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

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

    var projectDetail4 = ProjectUtil.getPublishedProjectDetails();
    projectDetail4.setId(7);
    projectDetail4.getProject().setId(8);

    var projectDetail5 = ProjectUtil.getPublishedProjectDetails();
    projectDetail5.setId(9);
    projectDetail5.getProject().setId(10);

    var projectOperator1 = ProjectOperatorTestUtil.getOperator(projectDetail1);
    projectOperator1.setOrganisationGroup(
        TeamTestingUtil.generateOrganisationGroup(11, "A Org Grp", "AOrgGrp"));

    var projectOperator2 = ProjectOperatorTestUtil.getOperator(projectDetail2);
    projectOperator2.setOrganisationGroup(
        TeamTestingUtil.generateOrganisationGroup(12, "b Org Grp", "brgGrp"));

    var projectOperator3 = ProjectOperatorTestUtil.getOperator(projectDetail3);
    projectOperator3.setOrganisationGroup(
        TeamTestingUtil.generateOrganisationGroup(13, "C Org Grp", "COrgGrp"));

    var projectOperator4 = ProjectOperatorTestUtil.getOperator(projectDetail4);
    projectOperator4.setOrganisationGroup(
        TeamTestingUtil.generateOrganisationGroup(14, "C Org Grp", "COrgGrp"));

    var projectOperator5 = ProjectOperatorTestUtil.getOperator(projectDetail5);
    projectOperator5.setOrganisationGroup(
        TeamTestingUtil.generateOrganisationGroup(15, "C Org Grp", "COrgGrp"));

    var projectInformation1 = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail1);
    projectInformation1.setProjectTitle("d project");

    var projectInformation2 = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail2);
    projectInformation2.setProjectTitle("E project");

    var projectInformation3 = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail3);
    projectInformation3.setProjectTitle("A project");

    var projectInformation4 = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail4);
    projectInformation4.setProjectTitle("b project");

    var projectInformation5 = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail5);
    projectInformation5.setProjectTitle("C project");

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

    when(projectDetailsRepository.getAllPublishedProjectDetailsByProjectType(ProjectType.INFRASTRUCTURE))
        .thenReturn(List.of(projectDetail2, projectDetail4, projectDetail3, projectDetail1, projectDetail5));

    when(projectOperatorRepository.findAll())
        .thenReturn(List.of(projectOperator1, projectOperator2, projectOperator3, projectOperator4, projectOperator5));

    when(projectInformationRepository.findAll()).thenReturn(
        List.of(
            projectInformation1,
            projectInformation2,
            projectInformation3,
            projectInformation4,
            projectInformation5
        )
    );

    when(projectLocationRepository.findAll()).thenReturn(List.of(projectLocation1, projectLocation2, projectLocation3));

    when(projectLocationBlockRepository.findAll())
        .thenReturn(List.of(projectLocationBlock1, projectLocationBlock2, projectLocationBlock3, projectLocationBlock4));

    var expectedInfrastructureProjectJsons = List.of(
        InfrastructureProjectJson.from(
            projectDetail1,
            projectOperator1,
            projectInformation1,
            projectLocation1,
            List.of(projectLocationBlock1, projectLocationBlock2)
        ),
        InfrastructureProjectJson.from(
            projectDetail2,
            projectOperator2,
            projectInformation2,
            projectLocation2,
            List.of(projectLocationBlock3, projectLocationBlock4)
        ),
        InfrastructureProjectJson.from(
            projectDetail3,
            projectOperator3,
            projectInformation3,
            projectLocation3,
            null
        ),
        InfrastructureProjectJson.from(
            projectDetail4,
            projectOperator4,
            projectInformation4,
            null,
            null
        ),
        InfrastructureProjectJson.from(
            projectDetail5,
            projectOperator5,
            projectInformation5,
            null,
            null
        )
    );

    assertThat(infrastructureProjectJsonService.getPublishedInfrastructureProjects())
        .isEqualTo(expectedInfrastructureProjectJsons);
  }
}
