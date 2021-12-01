package uk.co.ogauthority.pathfinder.service.email.notify.callback.failure;

import java.util.Set;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.service.email.notify.callback.EmailCallback;

/**
 * Implement this interface to change the default behaviour for handling email failure callbacks
 * for a specific set of NotifyTemplates.
 */
public interface EmailFailureHandler {

  /**
   * The set of notify templates that are supported by this implementation.
   * @return a set of supported notify templates
   */
  Set<NotifyTemplate> getSupportedTemplates();

  /**
   * Method to execute when an email callback failure is found for a supported template.
   * @param emailCallback the email callback that has been received
   */
  void handleEmailFailure(EmailCallback emailCallback);
}
