package uk.co.ogauthority.pathfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    value = HttpStatus.INTERNAL_SERVER_ERROR,
    reason = "Could not find implementation of DashboardItemService to support project type"
)
public class UnsupportedDashboardItemServiceException extends RuntimeException {

  public UnsupportedDashboardItemServiceException(String message) {
    super(message);
  }

  public UnsupportedDashboardItemServiceException(String message, Throwable cause) {
    super(message, cause);
  }

}
