package uk.co.ogauthority.pathfinder.model.email.emailproperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;

/**
 * Simple class to hold the default email properties for a GOV.UK notify template.
 */
public class EmailProperties {

  private final NotifyTemplate template;
  private final String recipientIdentifier;

  /**
   * Construct email properties using a template that the email will be based from.
   */
  public EmailProperties(NotifyTemplate template) {
    this.template = template;
    this.recipientIdentifier = ""; // inherit the default identifier unless overwritten
  }

  public EmailProperties(NotifyTemplate template, String recipientIdentifier) {
    this.template = template;
    this.recipientIdentifier = recipientIdentifier;
  }

  /**
   * Retrieve the name of the email template.
   */
  public String getTemplateName() {
    return template.getTemplateName();
  }

  public NotifyTemplate getTemplate() {
    return template;
  }

  public String getRecipientIdentifier() {
    return recipientIdentifier;
  }

  /**
   * Get the default email personalisation for all templates.
   */
  public Map<String, Object> getEmailPersonalisation() {

    var emailPersonalisation = new HashMap<String, Object>();

    if (!StringUtils.isBlank(recipientIdentifier)) {
      emailPersonalisation.put(CommonEmailMergeField.RECIPIENT_IDENTIFIER, recipientIdentifier);
    }

    return emailPersonalisation;

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    EmailProperties that = (EmailProperties) o;
    return Objects.equals(template, that.template)
        && Objects.equals(recipientIdentifier, that.recipientIdentifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(template, recipientIdentifier);
  }
}