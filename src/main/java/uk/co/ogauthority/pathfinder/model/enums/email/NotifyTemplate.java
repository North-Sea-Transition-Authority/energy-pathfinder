package uk.co.ogauthority.pathfinder.model.enums.email;

/**
 * Enumeration of templates stored in GOV.UK Notify.
 */
public enum NotifyTemplate {

  PROJECT_UPDATE_SUBMITTED("PROJECT_UPDATE_SUBMITTED_V3"),
  PROJECT_UPDATE_REQUESTED("PROJECT_UPDATE_REQUESTED_V2"),
  NO_UPDATE_NOTIFICATION("NO_UPDATE_NOTIFICATION_V3"),
  OUTGOING_OPERATOR_PROJECT_TRANSFER("OUTGOING_OPERATOR_PROJECT_TRANSFER_V2"),
  INCOMING_OPERATOR_PROJECT_TRANSFER("INCOMING_OPERATOR_PROJECT_TRANSFER_V2"),
  ADDED_TO_TEAM("ADDED_TO_TEAM_V2"),
  TEAM_ROLES_UPDATED("TEAM_ROLES_UPDATED_V2"),
  REMOVED_FROM_TEAM("REMOVED_FROM_TEAM_V2"),
  EMAIL_DELIVERY_FAILED("EMAIL_DELIVERY_FAILED_V1"),
  CUSTOM_COMMUNICATION("CUSTOM_COMMUNICATION_V1"),
  SUBSCRIBED_TO_NEWSLETTER("SUBSCRIBED_TO_NEWSLETTER_V2"),
  NEWSLETTER_WITH_PROJECTS_UPDATED("NEWSLETTER_WITH_PROJECTS_UPDATED_V2"),
  NEWSLETTER_NO_PROJECTS_UPDATED("NEWSLETTER_NO_PROJECTS_UPDATED_V2"),
  INITIAL_QUARTERLY_PROJECT_UPDATE_REMINDER("INITIAL_QUARTERLY_PROJECT_UPDATE_REMINDER_V2"),
  FINAL_QUARTERLY_PROJECT_UPDATE_REMINDER("FINAL_QUARTERLY_PROJECT_UPDATE_REMINDER_V2"),
  WEEK_BEFORE_DEADLINE_REGULATOR_UPDATE_REMINDER("WEEK_BEFORE_DEADLINE_REGULATOR_UPDATE_REMINDER_V1"),
  DAY_AFTER_DEADLINE_REGULATOR_UPDATE_REMINDER("DAY_AFTER_DEADLINE_REGULATOR_UPDATE_REMINDER_V1"),
  FEEDBACK_FAILED_TO_SEND("FEEDBACK_FAILED_TO_SEND_V1"),
  REMOVED_PROJECT_CONTRIBUTOR("REMOVED_PROJECT_CONTRIBUTOR_V1"),
  ADDED_PROJECT_CONTRIBUTOR("ADDED_PROJECT_CONTRIBUTOR_V1");

  private final String templateName;

  NotifyTemplate(String templateName) {
    this.templateName = templateName;
  }

  public String getTemplateName() {
    return this.templateName;
  }
}
