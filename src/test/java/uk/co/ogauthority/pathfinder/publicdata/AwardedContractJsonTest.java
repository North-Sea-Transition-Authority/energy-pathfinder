package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class AwardedContractJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var infrastructureAwardedContract = AwardedContractTestUtil.createInfrastructureAwardedContract(projectDetail);

    var infrastructureProjectAwardedContractJson = AwardedContractJson.from(infrastructureAwardedContract);

    var expectedInfrastructureProjectAwardedContractJson = new AwardedContractJson(
        infrastructureAwardedContract.getId(),
        infrastructureAwardedContract.getContractorName(),
        infrastructureAwardedContract.getContractFunction().name(),
        infrastructureAwardedContract.getManualContractFunction(),
        infrastructureAwardedContract.getDescriptionOfWork(),
        infrastructureAwardedContract.getDateAwarded(),
        infrastructureAwardedContract.getContractBand().name(),
        ContactJson.from(infrastructureAwardedContract)
    );

    assertThat(infrastructureProjectAwardedContractJson).isEqualTo(expectedInfrastructureProjectAwardedContractJson);
  }

  @Test
  void from_contractFunctionIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var infrastructureAwardedContract = AwardedContractTestUtil.createInfrastructureAwardedContract(projectDetail);

    infrastructureAwardedContract.setContractFunction(Function.FABRICATION);

    var infrastructureProjectAwardedContractJson = AwardedContractJson.from(infrastructureAwardedContract);

    assertThat(infrastructureProjectAwardedContractJson.function()).isEqualTo(Function.FABRICATION.name());
  }

  @Test
  void from_contractFunctionIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var infrastructureAwardedContract = AwardedContractTestUtil.createInfrastructureAwardedContract(projectDetail);

    infrastructureAwardedContract.setContractFunction(null);

    var infrastructureProjectAwardedContractJson = AwardedContractJson.from(infrastructureAwardedContract);

    assertThat(infrastructureProjectAwardedContractJson.function()).isNull();
  }
}
