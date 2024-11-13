package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class InfrastructureProjectFirstProductionDateJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    projectInformation.setFirstProductionDateQuarter(Quarter.Q1);
    projectInformation.setFirstProductionDateYear(2025);

    var infrastructureProjectFirstProductionDateJson = InfrastructureProjectFirstProductionDateJson.from(projectInformation);

    var expectedInfrastructureProjectFirstProductionDateJson = new InfrastructureProjectFirstProductionDateJson(
        projectInformation.getFirstProductionDateQuarter().name(),
        projectInformation.getFirstProductionDateYear()
    );

    assertThat(infrastructureProjectFirstProductionDateJson).isEqualTo(expectedInfrastructureProjectFirstProductionDateJson);
  }
}
