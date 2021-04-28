package uk.co.ogauthority.pathfinder.model.email.emailproperties.newsletter;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public abstract class ProjectNewsletterEmailProperties extends EmailProperties {

  private static final String DEFAULT_UNSUBSCRIBE_TEXT =
      "You can unsubscribe from these emails at any time using the following link.";

  private static final String DEFAULT_SUPPLY_CHAIN_TEXT = String.format(
      "For details about all %s projects, visit the %s %s supply chain interface using the following link.",
      EmailProperties.SERVICE_NAME,
      EmailProperties.CUSTOMER_MNEMONIC,
      EmailProperties.SERVICE_NAME
  );

  private final String unsubscribeUrl;
  private final String introductionText;

  public ProjectNewsletterEmailProperties(NotifyTemplate notifyTemplate,
                                          String recipientIdentifier,
                                          String unsubscribeUrl,
                                          String introductionText) {
    super(notifyTemplate, recipientIdentifier);
    this.unsubscribeUrl = unsubscribeUrl;
    this.introductionText = introductionText;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("UNSUBSCRIBE_URL", unsubscribeUrl);
    emailPersonalisation.put("INTRODUCTION_TEXT", introductionText);
    emailPersonalisation.put("UNSUBSCRIBE_TEXT", DEFAULT_UNSUBSCRIBE_TEXT);
    emailPersonalisation.put("SUPPLY_CHAIN_TEXT", DEFAULT_SUPPLY_CHAIN_TEXT);
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

    ProjectNewsletterEmailProperties that = (ProjectNewsletterEmailProperties) o;
    return Objects.equals(unsubscribeUrl, that.unsubscribeUrl)
        && Objects.equals(introductionText, that.introductionText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(unsubscribeUrl, introductionText);
  }
}
