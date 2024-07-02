package uk.co.ogauthority.pathfinder.energyportal.model.entity;

import com.google.common.annotations.VisibleForTesting;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import org.hibernate.annotations.Immutable;
import uk.co.ogauthority.pathfinder.energyportal.model.WebUserAccountStatus;

/**
 * A portal WebUserAccount from securemgr.
 * A WebUserAccount is linked to a Person. A Person may have many associated WebUserAccounts.
 */
@Entity
@Immutable
@Table(name = "user_accounts")
public class WebUserAccount implements Serializable {

  private static final long serialVersionUID = 1;

  @Id
  protected int wuaId;

  protected String title;
  protected String forename;
  protected String surname;
  protected String emailAddress;
  protected String loginId;

  @Enumerated(EnumType.STRING)
  protected WebUserAccountStatus accountStatus;

  @ManyToOne
  @JoinColumn(name = "person_id", referencedColumnName = "id", updatable = false, insertable = false)
  protected Person person;

  public WebUserAccount() {}

  @VisibleForTesting
  public WebUserAccount(int wuaId) {
    this.wuaId = wuaId;
  }

  @VisibleForTesting
  public WebUserAccount(int wuaId, Person person) {
    this.wuaId = wuaId;
    this.person = person;
  }

  @VisibleForTesting
  public WebUserAccount(int wuaId,
                        String title,
                        String forename,
                        String surname,
                        String emailAddress,
                        String loginId,
                        WebUserAccountStatus accountStatus,
                        Person person
  ) {
    this.wuaId = wuaId;
    this.title = title;
    this.forename = forename;
    this.surname = surname;
    this.emailAddress = emailAddress;
    this.loginId = loginId;
    this.accountStatus = accountStatus;
    this.person = person;
  }

  public int getWuaId() {
    return wuaId;
  }

  public String getTitle() {
    return title;
  }

  public String getForename() {
    return forename;
  }

  public String getSurname() {
    return surname;
  }

  public String getFullName() {
    return String.format("%s %s", forename, surname);
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public String getLoginId() {
    return loginId;
  }

  public WebUserAccountStatus getAccountStatus() {
    return accountStatus;
  }

  public Person getLinkedPerson() {
    return person;
  }
}
