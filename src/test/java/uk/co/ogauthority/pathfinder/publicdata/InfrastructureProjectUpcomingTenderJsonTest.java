package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;

class InfrastructureProjectUpcomingTenderJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);

    var infrastructureProjectUpcomingTenderJson = InfrastructureProjectUpcomingTenderJson.from(upcomingTender);

    var expectedInfrastructureProjectUpcomingTenderJson = new InfrastructureProjectUpcomingTenderJson(
        upcomingTender.getId(),
        upcomingTender.getTenderFunction().name(),
        upcomingTender.getManualTenderFunction(),
        upcomingTender.getDescriptionOfWork(),
        upcomingTender.getEstimatedTenderDate(),
        upcomingTender.getContractBand().name(),
        ContactJson.from(upcomingTender)
    );

    assertThat(infrastructureProjectUpcomingTenderJson).isEqualTo(expectedInfrastructureProjectUpcomingTenderJson);
  }

  @Test
  void from_tenderFunctionIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);

    upcomingTender.setTenderFunction(Function.DRILLING);

    var infrastructureProjectUpcomingTenderJson = InfrastructureProjectUpcomingTenderJson.from(upcomingTender);

    assertThat(infrastructureProjectUpcomingTenderJson.function()).isEqualTo(Function.DRILLING.name());
  }

  @Test
  void from_tenderFunctionIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);

    upcomingTender.setTenderFunction(null);

    var infrastructureProjectUpcomingTenderJson = InfrastructureProjectUpcomingTenderJson.from(upcomingTender);

    assertThat(infrastructureProjectUpcomingTenderJson.function()).isNull();
  }
}
