package uk.co.ogauthority.pathfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    value = HttpStatus.INTERNAL_SERVER_ERROR,
    reason = "An error occurred trying locate a supported ProjectSubmissionSummaryService implementation"
)
public class UnsupportedProjectSubmissionSummaryServiceException extends RuntimeException {

  public UnsupportedProjectSubmissionSummaryServiceException(String message) {
    super(message);
  }

  public UnsupportedProjectSubmissionSummaryServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
