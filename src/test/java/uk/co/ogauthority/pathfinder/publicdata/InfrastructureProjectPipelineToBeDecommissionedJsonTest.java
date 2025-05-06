package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.DecommissionedPipelineTestUtil;

class InfrastructureProjectPipelineToBeDecommissionedJsonTest {

  @Test
  void from() {
    var decommissionedPipeline = DecommissionedPipelineTestUtil.createDecommissionedPipeline();

    var infrastructureProjectPipelineToBeDecommissionedJson =
        InfrastructureProjectPipelineToBeDecommissionedJson.from(decommissionedPipeline);

    var expectedInfrastructureProjectPipelineToBeDecommissionedJson = new InfrastructureProjectPipelineToBeDecommissionedJson(
        decommissionedPipeline.getId(),
        decommissionedPipeline.getPipeline().getName(),
        decommissionedPipeline.getStatus().name(),
        StartEndYearJson.from(
            decommissionedPipeline.getEarliestRemovalYear(),
            decommissionedPipeline.getLatestRemovalYear()
        ),
        decommissionedPipeline.getRemovalPremise().name()
    );

    assertThat(infrastructureProjectPipelineToBeDecommissionedJson)
        .isEqualTo(expectedInfrastructureProjectPipelineToBeDecommissionedJson);
  }
}
