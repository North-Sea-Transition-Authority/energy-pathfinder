package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.SubseaInfrastructureTestUtil;

class InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJsonTest {

  @Test
  void from() {
    var subseaInfrastructure = SubseaInfrastructureTestUtil.createSubseaInfrastructure_withConcreteMattresses();

    var infrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson =
        InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson.from(subseaInfrastructure);

    var expectedInfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson =
        new InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson(
            subseaInfrastructure.getNumberOfMattresses(),
            subseaInfrastructure.getTotalEstimatedMattressMass()
        );

    assertThat(infrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson)
        .isEqualTo(expectedInfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson);
  }
}
