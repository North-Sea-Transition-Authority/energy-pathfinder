package uk.co.ogauthority.pathfinder.model.email;

import java.util.Objects;

public class EmailRecipient {

  private final EmailAddress emailAddress;

  private final String recipientIdentifier;

  public EmailRecipient(EmailAddress emailAddress, String recipientIdentifier) {
    this.emailAddress = emailAddress;
    this.recipientIdentifier = recipientIdentifier;
  }

  public EmailRecipient(EmailAddress emailAddress) {
    this.emailAddress = emailAddress;
    this.recipientIdentifier = "";
  }

  public EmailAddress getEmailAddress() {
    return emailAddress;
  }

  public String getRecipientIdentifier() {
    return recipientIdentifier;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }

    if (!(o instanceof EmailRecipient)) {
      return false;
    }

    EmailRecipient that = (EmailRecipient) o;
    return Objects.equals(emailAddress, that.emailAddress)
        && Objects.equals(recipientIdentifier, that.recipientIdentifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        emailAddress,
        recipientIdentifier
    );
  }

  @Override
  public String toString() {
    return "EmailRecipient{" +
        "emailAddress=" + emailAddress +
        ", recipientIdentifier='" + recipientIdentifier + '\'' +
        '}';
  }
}
