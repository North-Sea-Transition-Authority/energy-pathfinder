package uk.co.ogauthority.pathfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    value = HttpStatus.INTERNAL_SERVER_ERROR,
    reason = "Project version is not able to be cancelled"
)
public class CancelDraftProjectException extends RuntimeException {

  public CancelDraftProjectException(String message) {
    super(message);
  }

  public CancelDraftProjectException(String message, Throwable cause) {
    super(message, cause);
  }
}