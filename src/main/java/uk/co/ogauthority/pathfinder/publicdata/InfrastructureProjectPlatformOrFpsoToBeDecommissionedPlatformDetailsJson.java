package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;

record InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson(
    String name
) {

  static InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson from(PlatformFpso platformFpso) {
    var name = platformFpso.getStructure() != null
        ? platformFpso.getStructure().getFacilityName()
        : platformFpso.getManualStructureName();

    return new InfrastructureProjectPlatformOrFpsoToBeDecommissionedPlatformDetailsJson(name);
  }
}
