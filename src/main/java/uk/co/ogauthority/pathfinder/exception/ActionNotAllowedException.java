package uk.co.ogauthority.pathfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to use when a conditional requirement of a method has not been met.
 * Example use case - a certain validation hint should be expected as a param to the validators
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "A conditional requirement has not been met")
public class ActionNotAllowedException extends RuntimeException {

  public ActionNotAllowedException(String message) {
    super(message);
  }

  public ActionNotAllowedException(String message, Throwable cause) {
    super(message, cause);
  }

}
