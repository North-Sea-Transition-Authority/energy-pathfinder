package uk.co.ogauthority.pathfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Upcoming tender is not valid for this action")
public class InvalidUpcomingTenderException extends RuntimeException {

  public InvalidUpcomingTenderException(String message) {
    super(message);
  }
}
