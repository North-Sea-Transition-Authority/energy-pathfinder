package uk.co.ogauthority.pathfinder.model.enums.audit;

public enum AuditEvent {
  //Subscribe & Unsubscribe
  SUBSCRIBER_SIGN_UP_REQUEST(AuditLevel.INFO, "Subscriber signed up with id: %d"),
  UNSUBSCRIBE_GET_REQUEST(AuditLevel.INFO, "Subscriber unsubscribe request made with uuid: %s"),
  UNSUBSCRIBE_POST_REQUEST(AuditLevel.INFO, "Subscriber unsubscribe request made with uuid: %s"),
  //Form page POSTS
  PROJECT_OPERATOR_UPDATED(AuditLevel.INFO, "Project operator with orgGroup id: %d set for projectDetail with id: %d"),
  PROJECT_INFORMATION_UPDATED(AuditLevel.INFO, "Project information updated for projectDetail with id: %d"),
  LOCATION_INFORMATION_UPDATED(AuditLevel.INFO, "Location information updated for projectDetail with id: %d"),
  PROJECT_SETUP_UPDATED(AuditLevel.INFO, "Project setup information updated for projectDetail with id: %d"),
  UPCOMING_TENDER_UPDATED(AuditLevel.INFO, "Upcoming tender with id: %d updated for projectDetail with id: %d"),
  COLLABORATION_OPPORTUNITY_UPDATED(AuditLevel.INFO, "Collaboration opportunity with id: %d updated for projectDetail with id: %d"),
  AWARDED_CONTRACT_UPDATED(AuditLevel.INFO, "Awarded contract with id: %d updated for projectDetail with id: %d"),
  DECOMMISSIONED_WELL_UPDATED(AuditLevel.INFO, "Decommissioning schedule with id: %d updated for projectDetail with id: %d"),
  PLATFORM_FPSO_UPDATED(AuditLevel.INFO, "Platform or FPSO with id: %d updated for projectDetail with id: %d"),
  INTEGRATED_RIG_UPDATED(AuditLevel.INFO, "Integrated rig with id: %d updated for projectDetail with id: %d"),
  PIPELINE_UPDATED(AuditLevel.INFO, "Pipeline with id: %d updated for projectDetail with id: %d"),
  SUBSEA_INFRASTRUCTURE_UPDATED(AuditLevel.INFO, "Subsea infrastructure with id: %d updated for projectDetail with id: %d");

  private final AuditLevel auditLevel;
  private final String message;

  AuditEvent(AuditLevel auditLevel, String message) {
    this.auditLevel = auditLevel;
    this.message = message;
  }

  public AuditLevel getAuditLevel() {
    return auditLevel;
  }

  public String getMessage() {
    return message;
  }
}
