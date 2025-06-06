package uk.co.ogauthority.pathfinder.controller.email;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.exception.NotifyCallbackAccessDeniedException;
import uk.co.ogauthority.pathfinder.model.email.NotifyCallback;
import uk.co.ogauthority.pathfinder.service.email.notify.callback.NotifyCallbackService;

/**
 * Rest controller to handle GOV.UK Notify callback requests. A callback message is formatted in JSON.
 * For more information about callbacks visit https://docs.notifications.service.gov.uk/java.html#callbacks
 */
@RestController
public class NotifyCallbackController {

  private final NotifyCallbackService notifyCallbackService;

  public NotifyCallbackController(NotifyCallbackService notifyCallbackService) {
    this.notifyCallbackService = notifyCallbackService;
  }

  @PostMapping("/notify/callback")
  public ResponseEntity notifyCallback(@RequestBody NotifyCallback callbackRequest,
                                       @RequestHeader("Authorization") String bearerToken) {

    // verify authorization token
    boolean isValid = notifyCallbackService.isTokenValid(bearerToken);

    if (isValid) {
      notifyCallbackService.handleCallback(callbackRequest);
      return ResponseEntity.ok().build();
    } else {
      throw new NotifyCallbackAccessDeniedException("Authorization token to process NotifyCallback request cannot be verified");
    }

  }
}