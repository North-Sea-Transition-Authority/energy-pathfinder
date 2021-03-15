package uk.co.ogauthority.pathfinder.service.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.util.SecurityUtil;

public class AuditService {
  public static final String UNAUTHENTICATED_USER = "Unauthenticated user";
  private static final Logger LOGGER = LoggerFactory.getLogger(AuditService.class);

  private AuditService() {
    throw new IllegalStateException("AuditService is a static utility class and should not be instantiated");
  }

  public static void audit(AuditEvent auditEvent, String message) {
    var userIdentifier = SecurityUtil.getAuthenticatedUserFromSecurityContext()
        .map(wua -> String.format("%s - %d", wua.getFullName(), wua.getWuaId()))
        .orElse(UNAUTHENTICATED_USER);

    var auditMessage = String.format("%s [%s] - %s", auditEvent.name(), userIdentifier, message);

    switch (auditEvent.getAuditLevel()) {
      case INFO:
        LOGGER.info(auditMessage);
        break;
      case WARNING:
        LOGGER.warn(auditMessage);
        break;
      case ERROR:
        LOGGER.error(auditMessage);
        break;
      default:
        throw new IllegalStateException("Unexpected AuditLevel value: " + auditEvent.getAuditLevel());
    }
  }

}
