package uk.co.ogauthority.pathfinder.service.team;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Failed to construct a Team from PortalTeam")
class TeamFactoryException extends RuntimeException {

  public TeamFactoryException(String message) {
    super(message);
  }

  public TeamFactoryException(String message, Throwable cause) {
    super(message, cause);
  }
}