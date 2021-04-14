package uk.co.ogauthority.pathfinder.model.email.emailproperties.newsletters;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class MonthlyNewsletterEmailProperties extends EmailProperties {

  private final String unsubscribeUrl;

  public MonthlyNewsletterEmailProperties(String recipientIdentifier,
                                          String unsubscribeUrl) {
    super(NotifyTemplate.MONTHLY_NEWSLETTER, recipientIdentifier);
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
    MonthlyNewsletterEmailProperties that = (MonthlyNewsletterEmailProperties) o;
    return Objects.equals(unsubscribeUrl, that.unsubscribeUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), unsubscribeUrl);
  }
}
