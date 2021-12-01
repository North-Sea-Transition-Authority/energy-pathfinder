package uk.co.ogauthority.pathfinder.service.email.notify.callback.failure;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.service.email.notify.callback.EmailCallback;

@Service
public class PathfinderEmailFailureService {

  private final List<EmailFailureHandler> emailFailureHandlers;

  private final DefaultEmailFailureHandlerService defaultEmailFailureHandlerService;

  @Autowired
  PathfinderEmailFailureService(List<EmailFailureHandler> emailFailureHandlers,
                                DefaultEmailFailureHandlerService defaultEmailFailureHandlerService) {
    this.emailFailureHandlers = emailFailureHandlers;
    this.defaultEmailFailureHandlerService = defaultEmailFailureHandlerService;
  }

  public void processNotifyEmailDeliveryFailure(EmailCallback emailCallback) {

    final var supportedEmailFailureHandler = emailFailureHandlers
        .stream()
        .filter(emailFailureHandler -> emailFailureHandler.getSupportedTemplates().contains(emailCallback.getTemplate()))
        .findFirst();

    if (supportedEmailFailureHandler.isPresent()) {
      supportedEmailFailureHandler.get().handleEmailFailure(emailCallback);
    } else {
      defaultEmailFailureHandlerService.handleEmailFailure(emailCallback);
    }
  }
}
