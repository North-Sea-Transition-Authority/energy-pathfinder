package uk.co.ogauthority.pathfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    value = HttpStatus.INTERNAL_SERVER_ERROR,
    reason = "An error occurred trying locate a supported ProjectManagementDetailService implementation"
)
public class ProjectTypeDetailServiceImplementationException extends RuntimeException {

  public ProjectTypeDetailServiceImplementationException(String message) {
    super(message);
  }

  public ProjectTypeDetailServiceImplementationException(String message, Throwable cause) {
    super(message, cause);
  }
}
