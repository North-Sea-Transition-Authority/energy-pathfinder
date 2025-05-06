package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withPlatformAndSubstructuresRemoved(projectDetail);

    var infrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson =
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson.from(platformFpso);

    var expectedInfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson =
        new InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson(
            platformFpso.getSubstructureRemovalPremise().name(),
            platformFpso.getSubstructureRemovalMass(),
            StartEndYearJson.from(
                platformFpso.getSubStructureRemovalEarliestYear(),
                platformFpso.getSubStructureRemovalLatestYear()
            )
        );

    assertThat(infrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson)
        .isEqualTo(expectedInfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson);
  }
}
