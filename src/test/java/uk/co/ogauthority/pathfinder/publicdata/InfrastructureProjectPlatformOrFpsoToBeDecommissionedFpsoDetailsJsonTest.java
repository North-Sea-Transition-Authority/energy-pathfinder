package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil.FACILITY;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJsonTest {

  @Test
  void from_platformFpsoHasStructure() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withFpso(projectDetail);

    platformFpso.setStructure(FACILITY);
    platformFpso.setManualStructureName(null);

    var infrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson =
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson.from(platformFpso);

    var expectedInfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson =
        new InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson(
            FACILITY.getFacilityName(),
            platformFpso.getFpsoType(),
            platformFpso.getFpsoDimensions()
        );

    assertThat(infrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson)
        .isEqualTo(expectedInfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson);
  }

  @Test
  void from_platformFpsoHasManualStructure() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withFpso(projectDetail);

    platformFpso.setStructure(null);
    platformFpso.setManualStructureName("Test structure name");

    var infrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson =
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson.from(platformFpso);

    var expectedInfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson =
        new InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson(
            "Test structure name",
            platformFpso.getFpsoType(),
            platformFpso.getFpsoDimensions()
        );

    assertThat(infrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson)
        .isEqualTo(expectedInfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson);
  }
}
