package uk.co.ogauthority.pathfinder.model.email.emailproperties.communication;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public final class CommunicationEmailProperties extends EmailProperties {

  private final String subject;

  private final String body;

  public CommunicationEmailProperties(String recipientIdentifier,
                                      String subject,
                                      String body) {
    super(NotifyTemplate.CUSTOM_COMMUNICATION, recipientIdentifier);
    this.subject = subject;
    this.body = body;
  }

  public String getSubject() {
    return subject;
  }

  public String getBody() {
    return body;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("SUBJECT", subject);
    emailPersonalisation.put("BODY", body);
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
    CommunicationEmailProperties that = (CommunicationEmailProperties) o;
    return Objects.equals(subject, that.subject)
        && Objects.equals(body, that.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), subject, body);
  }

  @Override
  public String toString() {
    return String.format(
        "subject %s, body %s, template %s, recipient name %s",
        subject,
        body,
        getTemplate(),
        getRecipientIdentifier()
    );
  }
}
