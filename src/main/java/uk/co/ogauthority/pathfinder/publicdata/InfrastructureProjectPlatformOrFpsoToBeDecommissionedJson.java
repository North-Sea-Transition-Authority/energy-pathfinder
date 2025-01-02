package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.PlatformFpsoInfrastructureType;

record InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson(
    Integer id,
    String type,
    InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson platformDetails,
    InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson fpsoDetails,
    Integer topsidesOrFloatingUnitMassMetricTonnes,
    StartEndYearJson removalPeriod,
    Boolean substructureRemovalInScope,
    InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson substructureRemovalDetails,
    String futurePlans
) {

  static InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson from(PlatformFpso platformFpso) {
    var id = platformFpso.getId();
    var type = platformFpso.getInfrastructureType().name();
    var platformDetails = platformFpso.getInfrastructureType() == PlatformFpsoInfrastructureType.PLATFORM
        ? InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson.from(platformFpso)
        : null;
    var fpsoDetails = platformFpso.getInfrastructureType() == PlatformFpsoInfrastructureType.FPSO
        ? InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson.from(platformFpso)
        : null;
    var topsidesOrFloatingUnitMassMetricTonnes = platformFpso.getTopsideFpsoMass();
    var removalPeriod = StartEndYearJson.from(platformFpso.getEarliestRemovalYear(), platformFpso.getLatestRemovalYear());
    var substructureRemovalInScope = platformFpso.getSubstructuresExpectedToBeRemoved();
    var substructureRemovalDetails = Boolean.TRUE.equals(substructureRemovalInScope)
        ? InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson.from(platformFpso)
        : null;
    var futurePlans = platformFpso.getFuturePlans().name();

    return new InfrastructureProjectPlatformOrFpsoToBeDecommissionedJson(
        id,
        type,
        platformDetails,
        fpsoDetails,
        topsidesOrFloatingUnitMassMetricTonnes,
        removalPeriod,
        substructureRemovalInScope,
        substructureRemovalDetails,
        futurePlans
    );
  }
}
