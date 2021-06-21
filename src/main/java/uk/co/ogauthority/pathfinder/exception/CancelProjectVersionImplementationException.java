package uk.co.ogauthority.pathfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    value = HttpStatus.INTERNAL_SERVER_ERROR,
    reason = "An error occurred trying locate a supported CancelDraftProjectVersionService implementation"
)
public class CancelProjectVersionImplementationException extends RuntimeException {

  public CancelProjectVersionImplementationException(String message) {
    super(message);
  }

  public CancelProjectVersionImplementationException(String message, Throwable cause) {
    super(message, cause);
  }
}
