package uk.co.ogauthority.pathfinder.energyportal.model.entity;

import com.google.common.annotations.VisibleForTesting;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.annotations.Immutable;


/**
 * A resource Person, from decmgr.resource_people.
 */
@Entity
@Immutable
@Table(name = "people")
public class Person implements Serializable {

  private static final long serialVersionUID = 1;

  @Id
  private Integer id;

  private String forename;
  private String surname;
  private String emailAddress;
  private String telephoneNo;

  public Person() {}

  @VisibleForTesting
  public Person(Integer id, String forename, String surname, String emailAddress, String telephoneNo) {
    this.id = id;
    this.forename = forename;
    this.surname = surname;
    this.telephoneNo = telephoneNo;
    this.emailAddress = emailAddress;
  }

  public PersonId getId() {
    return new PersonId(id);
  }

  public String getForename() {
    return forename;
  }

  public String getSurname() {
    return surname;
  }

  public String getFullName() {
    return forename + " " + surname;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public String getTelephoneNo() {
    return telephoneNo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Person person = (Person) o;
    return id.equals(person.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
