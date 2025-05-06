package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class InfrastructureProjectPlatformOrFpsoToBeDecommissionedJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var platformFpso = PlatformFpsoTestUtil.getPlatformFpso(1, projectDetail);

    var infrastructureProjectPlatformOrFpsoToBeDecommissionedJson =
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson.from(platformFpso);

    var expectedInfrastructureProjectPlatformOrFpsoToBeDecommissionedJson =
        new InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson(
            platformFpso.getId(),
            platformFpso.getInfrastructureType().name(),
            null,
            InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson.from(platformFpso),
            platformFpso.getTopsideFpsoMass(),
            StartEndYearJson.from(platformFpso.getEarliestRemovalYear(), platformFpso.getLatestRemovalYear()),
            platformFpso.getSubstructuresExpectedToBeRemoved(),
            InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson.from(platformFpso),
            platformFpso.getFuturePlans().name()
        );

    assertThat(infrastructureProjectPlatformOrFpsoToBeDecommissionedJson)
        .isEqualTo(expectedInfrastructureProjectPlatformOrFpsoToBeDecommissionedJson);
  }

  @Test
  void from_platformFpsoInfrastructureTypeIsPlatform() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withPlatform(projectDetail);

    var infrastructureProjectPlatformOrFpsoToBeDecommissionedJson =
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson.from(platformFpso);

    assertThat(infrastructureProjectPlatformOrFpsoToBeDecommissionedJson)
        .extracting(
            InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson::platformDetails,
            InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson::fpsoDetails
        )
        .containsExactly(
            InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson.from(platformFpso),
            null
        );
  }

  @Test
  void from_platformFpsoInfrastructureTypeIsFpso() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withFpso(projectDetail);

    var infrastructureProjectPlatformOrFpsoToBeDecommissionedJson =
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson.from(platformFpso);

    assertThat(infrastructureProjectPlatformOrFpsoToBeDecommissionedJson)
        .extracting(
            InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson::platformDetails,
            InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson::fpsoDetails
        )
        .containsExactly(
            null,
            InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson.from(platformFpso)
        );
  }

  @Test
  void from_platformFpsoSubstructuresExpectedToBeRemoved() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withPlatformAndSubstructuresRemoved(projectDetail);

    var infrastructureProjectPlatformOrFpsoToBeDecommissionedJson =
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson.from(platformFpso);

    assertThat(infrastructureProjectPlatformOrFpsoToBeDecommissionedJson.substructureRemovalDetails())
        .isEqualTo(InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson.from(platformFpso));
  }

  @Test
  void from_platformFpsoSubstructuresNotExpectedToBeRemoved() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var platformFpso = PlatformFpsoTestUtil.getPlatformFpso_withPlatformAndSubstructuresNotRemoved(projectDetail);

    var infrastructureProjectPlatformOrFpsoToBeDecommissionedJson =
        InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson.from(platformFpso);

    assertThat(infrastructureProjectPlatformOrFpsoToBeDecommissionedJson.substructureRemovalDetails()).isNull();
  }
}
