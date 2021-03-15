package uk.co.ogauthority.pathfinder.model.enums.audit;

public enum AuditEvent {
  SUBSCRIBER_SIGN_UP_REQUEST(AuditLevel.INFO, "Subscriber signed up with id: %d"),
  UNSUBSCRIBE_GET_REQUEST(AuditLevel.INFO, "Subscriber unsubscribe request made with uuid: %s"),
  UNSUBSCRIBE_POST_REQUEST(AuditLevel.INFO, "Subscriber unsubscribe request made with uuid: %s");

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
