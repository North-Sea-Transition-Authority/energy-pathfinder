package uk.co.ogauthority.pathfinder.development.controller;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.email.NotifyCallback;
import uk.co.ogauthority.pathfinder.service.email.notify.callback.NotifyCallbackService;

@RestController
@Profile("callback-test")
@RequestMapping("/notify-test")
public class DevelopmentTestNotifyCallbackController {

  private final NotifyCallbackService notifyCallbackService;

  @Autowired
  public DevelopmentTestNotifyCallbackController(NotifyCallbackService notifyCallbackService) {
    this.notifyCallbackService = notifyCallbackService;
  }

  @GetMapping("/trigger-callback")
  @ResponseBody
  public ResponseEntity<String> triggerNotifyCallback(@RequestParam("notificationId") String notificationId) {

    final var notifyCallback = new NotifyCallback(
        notificationId,
        345235,
        NotifyCallback.NotifyCallbackStatus.PERMANENT_FAILURE,
        "test@test.email.co.uk",
        NotifyCallback.NotifyNotificationType.EMAIL,
        Instant.now(),
        Instant.now(),
        Instant.now()
    );

    notifyCallbackService.handleCallback(notifyCallback);

    return new ResponseEntity<>("notify callback triggered", HttpStatus.OK);
  }
}
