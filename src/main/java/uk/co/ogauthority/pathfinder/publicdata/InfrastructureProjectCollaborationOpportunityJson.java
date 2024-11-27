package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;

record InfrastructureProjectCollaborationOpportunityJson(
    Integer id,
    String function,
    String manualFunction,
    String descriptionOfWork,
    Boolean urgent,
    ContactJson contact
) {

  static InfrastructureProjectCollaborationOpportunityJson from(
      InfrastructureCollaborationOpportunity infrastructureCollaborationOpportunity
  ) {
    var id = infrastructureCollaborationOpportunity.getId();
    var function = infrastructureCollaborationOpportunity.getFunction() != null
        ? infrastructureCollaborationOpportunity.getFunction().name()
        : null;
    var manualFunction = infrastructureCollaborationOpportunity.getManualFunction();
    var descriptionOfWork = infrastructureCollaborationOpportunity.getDescriptionOfWork();
    var urgent = infrastructureCollaborationOpportunity.getUrgentResponseNeeded();
    var contact = ContactJson.from(infrastructureCollaborationOpportunity);

    return new InfrastructureProjectCollaborationOpportunityJson(
        id,
        function,
        manualFunction,
        descriptionOfWork,
        urgent,
        contact
    );
  }
}
