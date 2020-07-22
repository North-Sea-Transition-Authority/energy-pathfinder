package uk.co.ogauthority.pathfinder.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;

public class AuthTestingUtil {

  /**
   * Check that the given testFunction returns true only when at least one of the requiredPrivs is passed to it, otherwise it should return false.
   */
  public static void testPrivilegeBasedAuthenticationFunction(Set<UserPrivilege> requiredPrivs, Function<AuthenticatedUserAccount, Boolean> testFunction) {

    // No privileges
    assertThat(testFunction.apply(new AuthenticatedUserAccount(new WebUserAccount(1), List.of())))
        .isEqualTo(false);

    // Single privilege
    for (UserPrivilege privilege : UserPrivilege.values()) {
      boolean expectedResult = requiredPrivs.contains(privilege);

      try {
        assertThat(testFunction.apply(new AuthenticatedUserAccount(new WebUserAccount(1), List.of(privilege))))
            .isEqualTo(expectedResult);
      } catch (AssertionError e) {
        throw new AssertionError(String.format("Privilege check function returned %s with privilege %s. Expected %s", !expectedResult, privilege, expectedResult), e);
      }
    }

    // List of privileges
    for (UserPrivilege privilege1 : UserPrivilege.values()) {
      for (UserPrivilege privilege2 : UserPrivilege.values()) {
        boolean expectedResult = requiredPrivs.contains(privilege1) || requiredPrivs.contains(privilege2);
        try {
          assertThat(testFunction.apply(new AuthenticatedUserAccount(new WebUserAccount(1), List.of(privilege1, privilege2))))
              .isEqualTo(expectedResult);
        } catch (AssertionError e) {
          throw new AssertionError(String.format("Privilege check function returned %s with privileges %s. Expected %s", !expectedResult, List.of(privilege1, privilege2), expectedResult), e);
        }
      }
    }
  }

}
