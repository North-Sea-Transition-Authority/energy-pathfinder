package uk.co.ogauthority.pathfinder.model.email.emailproperties.subscription;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class SubscribedToNewsletterEmailProperties extends EmailProperties {

  private final String manageSubscriptionUrl;

  public SubscribedToNewsletterEmailProperties(String recipientName, String manageSubscriptionUrl) {
    super(NotifyTemplate.SUBSCRIBED_TO_NEWSLETTER, recipientName);
    this.manageSubscriptionUrl = manageSubscriptionUrl;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("MANAGE_SUBSCRIPTION_URL", manageSubscriptionUrl);
    return emailPersonalisation;
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (this == o) {
      return true;
    }
    if (getClass() != o.getClass()) {
      return false;
    }
    SubscribedToNewsletterEmailProperties that = (SubscribedToNewsletterEmailProperties) o;
    return Objects.equals(manageSubscriptionUrl, that.manageSubscriptionUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), manageSubscriptionUrl);
  }
}
