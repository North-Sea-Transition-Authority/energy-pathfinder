package uk.co.ogauthority.pathfinder.testutil;

import org.springframework.security.core.context.SecurityContextHolder;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserToken;

public class SecurityHelperUtil {

  private SecurityHelperUtil() {
    throw new IllegalStateException("SecurityHelperUtil is an util class and should not be instantiated");
  }

  public static void setAuthentication(AuthenticatedUserAccount userAccount) {
    var userToken = AuthenticatedUserToken.create("1", userAccount);
    SecurityContextHolder.getContext().setAuthentication(userToken);
  }
}
