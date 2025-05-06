package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.SubseaInfrastructureTestUtil;

class InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJsonTest {

  @Test
  void from() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withOtherInfrastructure();

    var infrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson =
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson.from(subseaInfrastructure);

    var expectedInfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson =
        new InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson(
            subseaInfrastructure.getOtherInfrastructureType(),
            subseaInfrastructure.getTotalEstimatedOtherMass()
        );

    assertThat(infrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson)
        .isEqualTo(expectedInfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson);
  }
}
