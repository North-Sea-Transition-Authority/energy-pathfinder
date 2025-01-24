package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityCommon;

record CollaborationOpportunityJson(
    Integer id,
    String function,
    String manualFunction,
    String descriptionOfWork,
    Boolean urgent,
    ContactJson contact
) {

  static CollaborationOpportunityJson from(
      CollaborationOpportunityCommon collaborationOpportunityCommon
  ) {
    var id = collaborationOpportunityCommon.getId();
    var function = collaborationOpportunityCommon.getFunction() != null
        ? collaborationOpportunityCommon.getFunction().name()
        : null;
    var manualFunction = collaborationOpportunityCommon.getManualFunction();
    var descriptionOfWork = collaborationOpportunityCommon.getDescriptionOfWork();
    var urgent = collaborationOpportunityCommon.getUrgentResponseNeeded();
    var contact = ContactJson.from(collaborationOpportunityCommon);

    return new CollaborationOpportunityJson(
        id,
        function,
        manualFunction,
        descriptionOfWork,
        urgent,
        contact
    );
  }
}
