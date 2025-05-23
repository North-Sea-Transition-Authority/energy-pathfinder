package uk.co.ogauthority.pathfinder.util;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;

public class SecurityUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtil.class);

  private SecurityUtil() {
    throw new IllegalStateException("SecurityUtil is a utility class and should not be instantiated");
  }

  public static Optional<AuthenticatedUserAccount> getAuthenticatedUserFromSecurityContext() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      LOGGER.debug("SecurityContext contained no Authentication object");
      return Optional.empty();
    } else if (authentication.getPrincipal() == null) {
      LOGGER.debug("Principal was null when trying to resolve controller argument");
      return Optional.empty();
    } else if (!(authentication.getPrincipal() instanceof AuthenticatedUserAccount)) {
      LOGGER.debug("Principal was not a AuthenticatedUserAccount when trying to resolve controller argument (was a {})",
          authentication.getPrincipal().getClass());
      return Optional.empty();
    } else {
      return Optional.of((AuthenticatedUserAccount) authentication.getPrincipal());
    }
  }

}
