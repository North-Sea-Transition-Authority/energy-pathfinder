package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.SubseaInfrastructureTestUtil;

class InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJsonTest {

  @Test
  void from() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withSubseaStructure();

    var infrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson =
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson.from(subseaInfrastructure);

    var expectedInfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson =
        new InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson(
            subseaInfrastructure.getTotalEstimatedSubseaMass().name()
        );

    assertThat(infrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson)
        .isEqualTo(expectedInfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson);
  }
}
