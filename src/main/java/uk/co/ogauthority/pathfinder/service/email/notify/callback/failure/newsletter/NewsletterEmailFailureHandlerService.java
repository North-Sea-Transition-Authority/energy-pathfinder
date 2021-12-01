package uk.co.ogauthority.pathfinder.service.email.notify.callback.failure.newsletter;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.service.email.notify.callback.EmailCallback;
import uk.co.ogauthority.pathfinder.service.email.notify.callback.failure.EmailFailureHandler;
import uk.co.ogauthority.pathfinder.service.subscription.SubscriptionService;

@Service
public class NewsletterEmailFailureHandlerService implements EmailFailureHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterEmailFailureHandlerService.class);

  private final SubscriptionService subscriptionService;

  public NewsletterEmailFailureHandlerService(SubscriptionService subscriptionService) {
    this.subscriptionService = subscriptionService;
  }

  @Override
  public Set<NotifyTemplate> getSupportedTemplates() {
    return Set.of(
        NotifyTemplate.NEWSLETTER_NO_PROJECTS_UPDATED,
        NotifyTemplate.NEWSLETTER_WITH_PROJECTS_UPDATED
    );
  }

  @Override
  public void handleEmailFailure(EmailCallback emailCallback) {

    final var failedSubscriberEmailAddress = emailCallback.getRecipientEmailAddress();

    final var isSubscriber = subscriptionService.isSubscribed(failedSubscriberEmailAddress);

    if (isSubscriber) {
      subscriptionService.unsubscribe(failedSubscriberEmailAddress);
    } else {
      LOGGER.warn(
          "Could not find subscriber to unsubscribe after failed newsletter delivery notification with ID {}",
          emailCallback.getNotificationId()
      );
    }
  }
}
