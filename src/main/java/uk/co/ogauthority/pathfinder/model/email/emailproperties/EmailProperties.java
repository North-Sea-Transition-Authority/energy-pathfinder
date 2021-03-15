package uk.co.ogauthority.pathfinder.model.email.emailproperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

/**
 * Simple class to hold the default email properties for a GOV.UK notify template.
 */
public class EmailProperties {

  private static final String SERVICE_NAME = "Energy Pathfinder";
  private static final String CUSTOMER_MNEMONIC = "OGA";

  public static final String DEFAULT_RECIPIENT_IDENTIFIER = String.format("%s user", SERVICE_NAME);
  public static final String DEFAULT_SIGN_OFF_IDENTIFIER = String.format("%s %s team", CUSTOMER_MNEMONIC, SERVICE_NAME);
  public static final String DEFAULT_GREETING_TEXT = "Dear";
  public static final String DEFAULT_SIGN_OFF_TEXT = "Kind regards";
  public static final String DEFAULT_SERVICE_LOGIN_TEXT = "To see more details please log in to the service";

  private final NotifyTemplate template;
  private final String recipientIdentifier;
  private final String signOffIdentifier;

  /**
   * Construct email properties using a template that the email will be based from.
   */
  public EmailProperties(NotifyTemplate template) {
    this.template = template;
    this.recipientIdentifier = DEFAULT_RECIPIENT_IDENTIFIER;
    this.signOffIdentifier = DEFAULT_SIGN_OFF_IDENTIFIER;
  }

  public EmailProperties(NotifyTemplate template, String recipientIdentifier) {
    this.template = template;
    this.recipientIdentifier = recipientIdentifier;
    this.signOffIdentifier = DEFAULT_SIGN_OFF_IDENTIFIER;
  }

  public EmailProperties(NotifyTemplate template, String recipientIdentifier, String signOffIdentifier) {
    this.template = template;
    this.recipientIdentifier = recipientIdentifier;
    this.signOffIdentifier = signOffIdentifier;
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
  public Map<String, String> getEmailPersonalisation() {

    Map<String, String> emailPersonalisation = new HashMap<>();

    // TEST_EMAIL set to "no" by default and only set to "yes" in the TestNotifyServiceImpl
    emailPersonalisation.put("TEST_EMAIL", "no");
    emailPersonalisation.put("GREETING_TEXT", EmailProperties.DEFAULT_GREETING_TEXT);
    emailPersonalisation.put("RECIPIENT_IDENTIFIER", recipientIdentifier);
    emailPersonalisation.put("SIGN_OFF_TEXT", EmailProperties.DEFAULT_SIGN_OFF_TEXT);
    emailPersonalisation.put("SIGN_OFF_IDENTIFIER", signOffIdentifier);

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
        && Objects.equals(recipientIdentifier, that.recipientIdentifier)
        && Objects.equals(signOffIdentifier, that.signOffIdentifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(template, recipientIdentifier, signOffIdentifier);
  }
}