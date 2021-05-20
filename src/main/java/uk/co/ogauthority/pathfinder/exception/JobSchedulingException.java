package uk.co.ogauthority.pathfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "An error occurred trying to schedule a job")
public class JobSchedulingException extends RuntimeException {

  public JobSchedulingException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
