package uk.co.ogauthority.pathfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error occurred while processing object differences")
public class DifferenceProcessingException extends RuntimeException {

  public DifferenceProcessingException(String message) {
    super(message);
  }
}
