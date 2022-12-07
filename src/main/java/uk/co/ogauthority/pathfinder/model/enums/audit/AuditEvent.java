package uk.co.ogauthority.pathfinder.model.enums.audit;

public enum AuditEvent {
  //Subscribe & Unsubscribe
  SUBSCRIBER_SIGN_UP_REQUEST(AuditLevel.INFO, "Subscriber signed up with id: %d"),
  UNSUBSCRIBE_GET_REQUEST(AuditLevel.INFO, "Subscriber unsubscribe request made with uuid: %s"),
  UNSUBSCRIBE_POST_REQUEST(AuditLevel.INFO, "Subscriber unsubscribe request made with uuid: %s"),
  //Form page POSTS
  PROJECT_OPERATOR_UPDATED(AuditLevel.INFO, "Project operator with id: %d updated for projectDetail with id: %d"),
  PROJECT_INFORMATION_UPDATED(AuditLevel.INFO, "Project information updated for projectDetail with id: %d"),
  LOCATION_INFORMATION_UPDATED(AuditLevel.INFO, "Location information updated for projectDetail with id: %d"),
  PROJECT_SETUP_UPDATED(AuditLevel.INFO, "Project setup information updated for projectDetail with id: %d"),
  UPCOMING_TENDER_UPDATED(AuditLevel.INFO, "Upcoming tender with id: %d updated for projectDetail with id: %d"),
  UPCOMING_TENDER_REMOVED(AuditLevel.INFO, "Upcoming tender with id: %d removed for projectDetail with id: %d"),
  COLLABORATION_OPPORTUNITY_UPDATED(AuditLevel.INFO, "Collaboration opportunity with id: %d updated for projectDetail with id: %d"),
  COLLABORATION_OPPORTUNITY_REMOVED(AuditLevel.INFO, "Collaboration opportunity with id: %d removed for projectDetail with id: %d"),
  AWARDED_CONTRACT_UPDATED(AuditLevel.INFO, "Awarded contract with id: %d updated for projectDetail with id: %d"),
  AWARDED_CONTRACT_REMOVED(AuditLevel.INFO, "Awarded contract with id: %d removed for projectDetail with id: %d"),
  P_AND_A_SCHEDULE_UPDATED(AuditLevel.INFO, "Decommissioning schedule with id: %d updated for projectDetail with id: %d"),
  P_AND_A_SCHEDULE_REMOVED(AuditLevel.INFO, "Decommissioning schedule with id: %d removed for projectDetail with id: %d"),
  PLATFORM_FPSO_UPDATED(AuditLevel.INFO, "Platform or FPSO with id: %d updated for projectDetail with id: %d"),
  PLATFORM_FPSO_REMOVED(AuditLevel.INFO, "Platform or FPSO with id: %d removed for projectDetail with id: %d"),
  INTEGRATED_RIG_UPDATED(AuditLevel.INFO, "Integrated rig with id: %d updated for projectDetail with id: %d"),
  INTEGRATED_RIG_REMOVED(AuditLevel.INFO, "Integrated rig with id: %d removed for projectDetail with id: %d"),
  PIPELINE_UPDATED(AuditLevel.INFO, "Pipeline with id: %d updated for projectDetail with id: %d"),
  PIPELINE_REMOVED(AuditLevel.INFO, "Pipeline with id: %d removed for projectDetail with id: %d"),
  SUBSEA_INFRASTRUCTURE_UPDATED(AuditLevel.INFO, "Subsea infrastructure with id: %d updated for projectDetail with id: %d"),
  SUBSEA_INFRASTRUCTURE_REMOVED(AuditLevel.INFO, "Subsea infrastructure with id: %d removed for projectDetail with id: %d"),
  WORK_PLAN_UPCOMING_TENDER_UPDATED(AuditLevel.INFO, "Work plan upcoming tender with id: %d updated for projectDetail with id: %d"),
  WORK_PLAN_UPCOMING_TENDER_REMOVED(AuditLevel.INFO, "Work plan upcoming tender with id: %d removed for projectDetail with id: %d"),
  WORK_PLAN_COLLABORATION_UPDATED(AuditLevel.INFO, "Work plan collaboration opportunity with id: %d updated for projectDetail with id: %d"),
  WORK_PLAN_COLLABORATION_REMOVED(AuditLevel.INFO, "Work plan collaboration opportunity with id: %d removed for projectDetail with id: %d"),
  CAMPAIGN_INFORMATION_UPDATED(AuditLevel.INFO, "Campaign information with id: %d updated for projectDetail with id: %d"),
  COMMISSIONED_WELL_SCHEDULE_UPDATED(AuditLevel.INFO, "Commissioned well schedule with id: %d updated for projectDetail with id: %d"),
  COMMISSIONED_WELL_SCHEDULE_REMOVED(AuditLevel.INFO, "Commissioned well schedule with id: %d removed for projectDetail with id: %d");

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
