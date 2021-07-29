package uk.co.ogauthority.pathfinder.model.email.emailproperties.newsletter;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public abstract class ProjectNewsletterEmailProperties extends EmailProperties {

  private static final String DEFAULT_UNSUBSCRIBE_TEXT =
      "You can unsubscribe from these emails at any time using the following link.";

  private final String unsubscribeUrl;
  private final String introductionText;
  private final String serviceName;
  private final String customerMnemonic;

  public ProjectNewsletterEmailProperties(NotifyTemplate notifyTemplate,
                                          String recipientIdentifier,
                                          String unsubscribeUrl,
                                          String introductionText,
                                          String serviceName,
                                          String customerMnemonic) {
    super(notifyTemplate, recipientIdentifier);
    this.unsubscribeUrl = unsubscribeUrl;
    this.introductionText = introductionText;
    this.serviceName = serviceName;
    this.customerMnemonic = customerMnemonic;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("UNSUBSCRIBE_URL", unsubscribeUrl);
    emailPersonalisation.put("INTRODUCTION_TEXT", introductionText);
    emailPersonalisation.put("UNSUBSCRIBE_TEXT", DEFAULT_UNSUBSCRIBE_TEXT);
    emailPersonalisation.put("SUPPLY_CHAIN_TEXT", getDefaultSupplyChainText());
    return emailPersonalisation;
  }

  private String getDefaultSupplyChainText() {
    return String.format(
        "For details about all %s projects, visit the %s %s supply chain interface using the following link.",
        serviceName,
        customerMnemonic,
        serviceName
    );
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
        && Objects.equals(introductionText, that.introductionText)
        && Objects.equals(serviceName, that.serviceName)
        && Objects.equals(customerMnemonic, that.customerMnemonic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        unsubscribeUrl,
        introductionText,
        serviceName,
        customerMnemonic
    );
  }
}
