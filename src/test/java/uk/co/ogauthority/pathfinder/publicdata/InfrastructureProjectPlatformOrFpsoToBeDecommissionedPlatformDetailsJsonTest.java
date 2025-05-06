package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil.FACILITY;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJsonTest {

  @Test
  void from_platformFpsoHasStructure() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withPlatform(projectDetail);

    platformFpso.setStructure(FACILITY);
    platformFpso.setManualStructureName(null);

    var infrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson =
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson.from(platformFpso);

    var expectedInfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson =
        new InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson(
            FACILITY.getFacilityName()
        );

    assertThat(infrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson)
        .isEqualTo(expectedInfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson);
  }

  @Test
  void from_platformFpsoHasManualStructure() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withPlatform(projectDetail);

    platformFpso.setStructure(null);
    platformFpso.setManualStructureName("Test structure name");

    var infrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson =
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson.from(platformFpso);

    var expectedInfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson =
        new InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson("Test structure name");

    assertThat(infrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson)
        .isEqualTo(expectedInfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson);
  }
}
