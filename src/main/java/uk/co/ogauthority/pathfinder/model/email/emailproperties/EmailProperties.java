package uk.co.ogauthority.pathfinder.model.email.emailproperties;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

/**
 * Simple class to hold the default email properties for a GOV.UK notify template.
 */
public class EmailProperties {

  public static final String DEFAULT_RECIPIENT_IDENTIFIER = "Pathfinder user";
  public static final String DEFAULT_SIGN_OFF_IDENTIFIER = "%s admin team";

  @Value("${service.customer.mnemonic}")
  private String customerMnemonic;

  private final NotifyTemplate template;
  private final String recipientIdentifier;
  private final String signOffIdentifier;

  /**
   * Construct email properties using a template that the email will be based from.
   */
  public EmailProperties(NotifyTemplate template) {
    this.template = template;
    this.recipientIdentifier = DEFAULT_RECIPIENT_IDENTIFIER;
    this.signOffIdentifier = String.format(DEFAULT_SIGN_OFF_IDENTIFIER, customerMnemonic);
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
    emailPersonalisation.put("RECIPIENT_IDENTIFIER", recipientIdentifier);
    emailPersonalisation.put("SIGN_OFF_IDENTIFIER", signOffIdentifier);

    return emailPersonalisation;

  }
}