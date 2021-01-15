package uk.co.ogauthority.pathfinder.model.enums.email;

/**
 * Enumeration of templates stored in GOV.UK Notify.
 */
public enum NotifyTemplate {

  PROJECT_UPDATE_SUBMITTED("PROJECT_UPDATE_SUBMITTED_V1"),
  EMAIL_DELIVERY_FAILED("EMAIL_DELIVERY_FAILED_V1");

  private final String templateName;

  NotifyTemplate(String templateName) {
    this.templateName = templateName;
  }

  public String getTemplateName() {
    return this.templateName;
  }
}