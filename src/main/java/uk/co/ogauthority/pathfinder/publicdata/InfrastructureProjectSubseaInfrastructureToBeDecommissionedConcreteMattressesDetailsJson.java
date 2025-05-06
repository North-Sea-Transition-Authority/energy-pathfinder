package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.subseainfrastructure.SubseaInfrastructure;

record InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson(
    Integer numberToDecommission,
    Integer totalEstimatedMassMetricTonnes
) {

  static InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson from(
      SubseaInfrastructure subseaInfrastructure
  ) {
    var numberToDecommission = subseaInfrastructure.getNumberOfMattresses();
    var totalEstimatedMassMetricTonnes = subseaInfrastructure.getTotalEstimatedMattressMass();

    return new InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson(
        numberToDecommission,
        totalEstimatedMassMetricTonnes
    );
  }
}
