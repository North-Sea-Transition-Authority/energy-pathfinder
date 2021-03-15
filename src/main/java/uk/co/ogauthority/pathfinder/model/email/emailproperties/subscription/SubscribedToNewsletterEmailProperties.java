package uk.co.ogauthority.pathfinder.model.email.emailproperties.subscription;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class SubscribedToNewsletterEmailProperties extends EmailProperties {

  private final String unsubscribeUrl;

  public SubscribedToNewsletterEmailProperties(String recipientName, String unsubscribeUrl) {
    super(NotifyTemplate.SUBSCRIBED_TO_NEWSLETTER, recipientName);
    this.unsubscribeUrl = unsubscribeUrl;
  }

  @Override
  public Map<String, String> getEmailPersonalisation() {
    Map<String, String> emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("UNSUBSCRIBE_URL", unsubscribeUrl);
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
    return Objects.equals(unsubscribeUrl, that.unsubscribeUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), unsubscribeUrl);
  }
}
