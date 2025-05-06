package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.subseainfrastructure.SubseaInfrastructure;

record InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson(
    String totalEstimatedMass
) {

  static InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson from(
      SubseaInfrastructure subseaInfrastructure
  ) {
    var totalEstimatedMass = subseaInfrastructure.getTotalEstimatedSubseaMass().name();

    return new InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson(
        totalEstimatedMass
    );
  }
}
