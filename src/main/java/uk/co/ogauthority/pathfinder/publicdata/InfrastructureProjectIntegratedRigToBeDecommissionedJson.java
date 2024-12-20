package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.integratedrig.IntegratedRig;

record InfrastructureProjectIntegratedRigToBeDecommissionedJson(
    Integer id,
    String structureName,
    String name,
    String status,
    String intentionToReactivate
) {

  static InfrastructureProjectIntegratedRigToBeDecommissionedJson from(IntegratedRig integratedRig) {
    var id = integratedRig.getId();
    var structureName = integratedRig.getFacility() != null
        ? integratedRig.getFacility().getFacilityName()
        : integratedRig.getManualFacility();
    var name = integratedRig.getName();
    var status = integratedRig.getStatus().name();
    var intentionToReactivate = integratedRig.getIntentionToReactivate().name();

    return new InfrastructureProjectIntegratedRigToBeDecommissionedJson(
        id,
        structureName,
        name,
        status,
        intentionToReactivate
    );
  }
}
