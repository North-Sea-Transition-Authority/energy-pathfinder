package uk.co.ogauthority.pathfinder.model.enums.email;

/**
 * Enumeration of templates stored in GOV.UK Notify.
 */
public enum NotifyTemplate {

  PROJECT_UPDATE_SUBMITTED("PROJECT_UPDATE_SUBMITTED_V2"),
  NO_UPDATE_NOTIFICATION("NO_UPDATE_NOTIFICATION_V2"),
  ADDED_TO_TEAM("ADDED_TO_TEAM_V2"),
  TEAM_ROLES_UPDATED("TEAM_ROLES_UPDATED_V2"),
  REMOVED_FROM_TEAM("REMOVED_FROM_TEAM_V2"),
  EMAIL_DELIVERY_FAILED("EMAIL_DELIVERY_FAILED_V1"),
  CUSTOM_COMMUNICATION("CUSTOM_COMMUNICATION_V1");

  private final String templateName;

  NotifyTemplate(String templateName) {
    this.templateName = templateName;
  }

  public String getTemplateName() {
    return this.templateName;
  }
}