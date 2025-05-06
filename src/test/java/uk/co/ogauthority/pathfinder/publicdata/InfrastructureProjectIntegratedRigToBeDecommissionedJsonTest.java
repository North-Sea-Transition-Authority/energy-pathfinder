package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.IntegratedRigTestUtil;

class InfrastructureProjectIntegratedRigToBeDecommissionedJsonTest {

  @Test
  void from_integratedRigHasDevUkFacility() {
    var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withDevUkFacility();

    var infrastructureProjectIntegratedRigToBeDecommissionedJson =
        InfrastructureProjectIntegratedRigToBeDecommissionedJson.from(integratedRig);

    var expectedInfrastructureProjectIntegratedRigToBeDecommissionedJson =
        new InfrastructureProjectIntegratedRigToBeDecommissionedJson(
            integratedRig.getId(),
            integratedRig.getFacility().getFacilityName(),
            integratedRig.getName(),
            integratedRig.getStatus().name(),
            integratedRig.getIntentionToReactivate().name()
        );

    assertThat(infrastructureProjectIntegratedRigToBeDecommissionedJson)
        .isEqualTo(expectedInfrastructureProjectIntegratedRigToBeDecommissionedJson);
  }

  @Test
  void from_integratedRigHasManualFacility() {
    var integratedRig = IntegratedRigTestUtil.createIntegratedRig_withManualFacility();

    var infrastructureProjectIntegratedRigToBeDecommissionedJson =
        InfrastructureProjectIntegratedRigToBeDecommissionedJson.from(integratedRig);

    var expectedInfrastructureProjectIntegratedRigToBeDecommissionedJson =
        new InfrastructureProjectIntegratedRigToBeDecommissionedJson(
            integratedRig.getId(),
            integratedRig.getManualFacility(),
            integratedRig.getName(),
            integratedRig.getStatus().name(),
            integratedRig.getIntentionToReactivate().name()
        );

    assertThat(infrastructureProjectIntegratedRigToBeDecommissionedJson)
        .isEqualTo(expectedInfrastructureProjectIntegratedRigToBeDecommissionedJson);
  }
}
