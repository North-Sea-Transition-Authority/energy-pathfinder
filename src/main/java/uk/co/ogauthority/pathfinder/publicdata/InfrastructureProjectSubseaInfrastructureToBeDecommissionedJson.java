package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.subseainfrastructure.SubseaInfrastructure;
import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaInfrastructureType;

record InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson(
    Integer id,
    String surfaceInfrastructureHostName,
    String description,
    String status,
    String type,
    InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson concreteMattressesDetails,
    InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson subseaStructureDetails,
    InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson otherDetails,
    StartEndYearJson decommissioningPeriod
) {

  static InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson from(SubseaInfrastructure subseaInfrastructure) {
    var id = subseaInfrastructure.getId();
    var surfaceInfrastructureHostName = subseaInfrastructure.getFacility() != null
        ? subseaInfrastructure.getFacility().getFacilityName()
        : subseaInfrastructure.getManualFacility();
    var description = subseaInfrastructure.getDescription();
    var status = subseaInfrastructure.getStatus().name();
    var type = subseaInfrastructure.getInfrastructureType().name();
    var concreteMattressesDetails = subseaInfrastructure.getInfrastructureType() == SubseaInfrastructureType.CONCRETE_MATTRESSES
        ? InfrastructureProjectSubseaInfrastructureToBeDecommissionedConcreteMattressesDetailsJson.from(subseaInfrastructure)
        : null;
    var subseaStructureDetails = subseaInfrastructure.getInfrastructureType() == SubseaInfrastructureType.SUBSEA_STRUCTURE
        ? InfrastructureProjectSubseaInfrastructureToBeDecommissionedSubseaStructureDetailsJson.from(subseaInfrastructure)
        : null;
    var otherDetails = subseaInfrastructure.getInfrastructureType() == SubseaInfrastructureType.OTHER
        ? InfrastructureProjectSubseaInfrastructureToBeDecommissionedOtherDetailsJson.from(subseaInfrastructure)
        : null;
    var decommissioningPeriod = new StartEndYearJson(
        subseaInfrastructure.getEarliestDecommissioningStartYear(),
        subseaInfrastructure.getLatestDecommissioningCompletionYear()
    );

    return new InfrastructureProjectSubseaInfrastructureToBeDecommissionedJson(
        id,
        surfaceInfrastructureHostName,
        description,
        status,
        type,
        concreteMattressesDetails,
        subseaStructureDetails,
        otherDetails,
        decommissioningPeriod
    );
  }
}
