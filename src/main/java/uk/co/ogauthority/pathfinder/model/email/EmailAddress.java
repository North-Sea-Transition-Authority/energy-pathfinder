package uk.co.ogauthority.pathfinder.model.email;

import java.util.Objects;

public class EmailAddress {

  private final String emailAddressValue;

  public EmailAddress(String emailAddressValue) {
    this.emailAddressValue = emailAddressValue;
  }

  public String getEmailAddressValue() {
    return emailAddressValue;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof EmailAddress)) {
      return false;
    }
    EmailAddress that = (EmailAddress) o;
    return Objects.equals(emailAddressValue, that.emailAddressValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(emailAddressValue);
  }

  @Override
  public String toString() {
    return "EmailAddress{" +
        "emailAddressValue='" + emailAddressValue + '\'' +
        '}';
  }
}
