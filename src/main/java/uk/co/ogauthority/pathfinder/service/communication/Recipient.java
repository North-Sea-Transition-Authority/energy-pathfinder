package uk.co.ogauthority.pathfinder.service.communication;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;

final class Recipient {

  private final String emailAddress;

  private final String forename;

  private final String surname;

  Recipient(Person person) {
    this.emailAddress = person.getEmailAddress();
    this.forename = person.getForename();
    this.surname = person.getSurname();
  }

  Recipient(String emailAddress,
                   String forename,
                   String surname) {
    this.emailAddress = emailAddress;
    this.forename = forename;
    this.surname = surname;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public String getForename() {
    return forename;
  }

  public String getSurname() {
    return surname;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Recipient that = (Recipient) o;
    return Objects.equals(emailAddress, that.emailAddress)
        && Objects.equals(forename, that.forename)
        && Objects.equals(surname, that.surname);
  }

  @Override
  public int hashCode() {
    return Objects.hash(emailAddress, forename, surname);
  }
}
