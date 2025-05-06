package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.subseainfrastructure.SubseaInfrastructure;

record InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson(
    String type,
    Integer totalEstimatedMassMetricTonnes
) {

  static InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson from(
      SubseaInfrastructure subseaInfrastructure
  ) {
    var type = subseaInfrastructure.getOtherInfrastructureType();
    var totalEstimatedMassMetricTonnes = subseaInfrastructure.getTotalEstimatedOtherMass();

    return new InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson(
        type,
        totalEstimatedMassMetricTonnes
    );
  }
}
