package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class InfrastructureProjectJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var projectLocationBlocks = List.<ProjectLocationBlock>of();

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        projectLocation,
        projectLocationBlocks
    );

    var expectedInfrastructureProjectJson = new InfrastructureProjectJson(
        projectDetail.getProject().getId(),
        InfrastructureProjectDetailsJson.from(projectOperator, projectInformation),
        ContactJson.from(projectInformation),
        null,
        projectInformation.getFirstProductionDateYear(),
        InfrastructureProjectLocationJson.from(projectLocation, projectLocationBlocks),
        LocalDateTime.ofInstant(projectDetail.getSubmittedInstant(), ZoneId.systemDefault())
    );

    assertThat(infrastructureProjectJson).isEqualTo(expectedInfrastructureProjectJson);
  }

  @Test
  void from_firstProductionDateQuarterIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    projectInformation.setFirstProductionDateQuarter(null);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null
    );

    assertThat(infrastructureProjectJson.firstProductionDateQuarter()).isNull();
  }

  @Test
  void from_firstProductionDateQuarterIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    projectInformation.setFirstProductionDateQuarter(Quarter.Q1);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null
    );

    assertThat(infrastructureProjectJson.firstProductionDateQuarter()).isEqualTo(Quarter.Q1.name());
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

    var projectLocationBlocks = List.<ProjectLocationBlock>of();

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        projectLocation,
        projectLocationBlocks
    );

    assertThat(infrastructureProjectJson.location())
        .isEqualTo(InfrastructureProjectLocationJson.from(projectLocation, projectLocationBlocks));
  }
}
