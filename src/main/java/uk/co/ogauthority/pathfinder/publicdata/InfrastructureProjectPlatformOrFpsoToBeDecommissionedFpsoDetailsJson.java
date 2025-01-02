package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;

record InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson(
    String name,
    String type,
    String dimensions
) {

  static InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson from(PlatformFpso platformFpso) {
    var name = platformFpso.getStructure() != null
        ? platformFpso.getStructure().getFacilityName()
        : platformFpso.getManualStructureName();
    var type = platformFpso.getFpsoType();
    var dimensions = platformFpso.getFpsoDimensions();

    return new InfrastructureProjectPlatformOrFpsoToBeDecommissionedFpsoDetailsJson(
        name,
        type,
        dimensions
    );
  }
}
