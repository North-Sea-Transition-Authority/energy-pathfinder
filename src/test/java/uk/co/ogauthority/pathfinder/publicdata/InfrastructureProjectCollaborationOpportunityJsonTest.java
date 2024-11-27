package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.testutil.InfrastructureCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class InfrastructureProjectCollaborationOpportunityJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var infrastructureCollaborationOpportunity =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    var infrastructureProjectCollaborationOpportunityJson =
        InfrastructureProjectCollaborationOpportunityJson.from(infrastructureCollaborationOpportunity);

    var expectedInfrastructureProjectCollaborationOpportunityJson = new InfrastructureProjectCollaborationOpportunityJson(
        infrastructureCollaborationOpportunity.getId(),
        infrastructureCollaborationOpportunity.getFunction().name(),
        infrastructureCollaborationOpportunity.getManualFunction(),
        infrastructureCollaborationOpportunity.getDescriptionOfWork(),
        infrastructureCollaborationOpportunity.getUrgentResponseNeeded(),
        ContactJson.from(infrastructureCollaborationOpportunity)
    );

    assertThat(infrastructureProjectCollaborationOpportunityJson)
        .isEqualTo(expectedInfrastructureProjectCollaborationOpportunityJson);
  }

  @Test
  void from_functionIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var infrastructureCollaborationOpportunity =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    infrastructureCollaborationOpportunity.setFunction(Function.LOGISTICS);

    var infrastructureProjectCollaborationOpportunityJson =
        InfrastructureProjectCollaborationOpportunityJson.from(infrastructureCollaborationOpportunity);

    assertThat(infrastructureProjectCollaborationOpportunityJson.function()).isEqualTo(Function.LOGISTICS.name());
  }

  @Test
  void from_functionIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var infrastructureCollaborationOpportunity =
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    infrastructureCollaborationOpportunity.setFunction(null);

    var infrastructureProjectCollaborationOpportunityJson =
        InfrastructureProjectCollaborationOpportunityJson.from(infrastructureCollaborationOpportunity);

    assertThat(infrastructureProjectCollaborationOpportunityJson.function()).isNull();
  }
}
