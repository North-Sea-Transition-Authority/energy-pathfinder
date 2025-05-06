package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;

record InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson(
    String premise,
    Integer estimatedMassMetricTonnes,
    StartEndYearJson period
) {

  static InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson from(PlatformFpso platformFpso) {
    var premise = platformFpso.getSubstructureRemovalPremise().name();
    var estimatedMassMetricTonnes = platformFpso.getSubstructureRemovalMass();
    var period = StartEndYearJson.from(
        platformFpso.getSubStructureRemovalEarliestYear(),
        platformFpso.getSubStructureRemovalLatestYear()
    );

    return new InfrastructureProjectPlatformOrFpsoToBeDecommissionedSubstructureRemovalDetailsJson(
        premise,
        estimatedMassMetricTonnes,
        period
    );
  }
}
