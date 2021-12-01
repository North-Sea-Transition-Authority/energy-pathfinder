package uk.co.ogauthority.pathfinder.service.email.notify.callback.failure;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.service.email.notify.callback.EmailCallback;

@Service
public class TestEmailFailureHandlerService implements EmailFailureHandler {

  @Override
  public Set<NotifyTemplate> getSupportedTemplates() {
    return null;
  }

  @Override
  public void handleEmailFailure(EmailCallback emailCallback) {}
}
