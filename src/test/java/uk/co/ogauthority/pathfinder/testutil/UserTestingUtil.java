package uk.co.ogauthority.pathfinder.testutil;

import java.util.List;
import java.util.Set;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;

public class UserTestingUtil {

  public static Person getPerson() {
    return getPerson(1, "Test", "Person", "someone@example.com", "0");
  }

  public static Person getPerson(Integer id,
                                 String forename,
                                 String surname,
                                 String emailAddress,
                                 String telephoneNo) {
    return new Person(id, forename, surname, emailAddress, telephoneNo);
  }

  public static Person getPerson(AuthenticatedUserAccount authenticatedUserAccount) {
    return new Person(
            authenticatedUserAccount.getWuaId(),
            authenticatedUserAccount.getForename(),
            authenticatedUserAccount.getSurname(),
            authenticatedUserAccount.getEmailAddress(),
            "1"
        );
  }

  public static WebUserAccount getWebUserAccount() {
    return getWebUserAccount(1, getPerson());
  }

  public static WebUserAccount getWebUserAccount(Integer id, Person person) {
    return new WebUserAccount(id, person);
  }

  public static AuthenticatedUserAccount getAuthenticatedUserAccount() {
    return getAuthenticatedUserAccount(getWebUserAccount(), List.of());
  }

  public static AuthenticatedUserAccount getAuthenticatedUserAccount(List<UserPrivilege> privileges) {
    return getAuthenticatedUserAccount(getWebUserAccount(), privileges);
  }

  public static AuthenticatedUserAccount getAuthenticatedUserAccount(Set<UserPrivilege> privileges){
    return new AuthenticatedUserAccount(getWebUserAccount(), privileges);
  }

  public static AuthenticatedUserAccount getAuthenticatedUserAccount(WebUserAccount webUserAccount,
                                                                     List<UserPrivilege> privileges) {
    return new AuthenticatedUserAccount(webUserAccount, privileges);
  }
}
