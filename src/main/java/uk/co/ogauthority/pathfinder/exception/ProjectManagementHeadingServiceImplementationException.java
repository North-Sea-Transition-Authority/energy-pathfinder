package uk.co.ogauthority.pathfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    value = HttpStatus.INTERNAL_SERVER_ERROR,
    reason = "An error occurred trying locate a supported ProjectManagementHeadingService implementation"
)
public class ProjectManagementHeadingServiceImplementationException extends RuntimeException {

  public ProjectManagementHeadingServiceImplementationException(String message) {
    super(message);
  }

  public ProjectManagementHeadingServiceImplementationException(String message, Throwable cause) {
    super(message, cause);
  }
}
