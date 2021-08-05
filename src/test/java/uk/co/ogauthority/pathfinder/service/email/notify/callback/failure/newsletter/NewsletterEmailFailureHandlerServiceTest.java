package uk.co.ogauthority.pathfinder.service.email.notify.callback.failure.newsletter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.service.email.notify.callback.failure.EmailCallbackTestUtil;
import uk.co.ogauthority.pathfinder.service.subscription.SubscriptionService;

@RunWith(MockitoJUnitRunner.class)
public class NewsletterEmailFailureHandlerServiceTest {

  @Mock
  private SubscriptionService subscriptionService;

  private NewsletterEmailFailureHandlerService newsletterEmailFailureHandlerService;

  @Before
  public void setup() {
    newsletterEmailFailureHandlerService = new NewsletterEmailFailureHandlerService(
        subscriptionService
    );
  }

  @Test
  public void getSupportedTemplates_verifyNewsletterTemplates() {
    final var supportedTemplates = newsletterEmailFailureHandlerService.getSupportedTemplates();
    assertThat(supportedTemplates).containsExactlyInAnyOrder(
        NotifyTemplate.NEWSLETTER_NO_PROJECTS_UPDATED,
        NotifyTemplate.NEWSLETTER_WITH_PROJECTS_UPDATED
    );
  }

  @Test
  public void handleEmailFailure_whenSubscriberFound_thenVerifyUnsubscribeInteraction() {

    final var failedEmailAddress = "someone@example.com";

    final var emailCallback = EmailCallbackTestUtil.constructEmailCallback(failedEmailAddress);

    when(subscriptionService.isSubscribed(emailCallback.getRecipientEmailAddress())).thenReturn(true);

    newsletterEmailFailureHandlerService.handleEmailFailure(emailCallback);

    verify(subscriptionService, times(1)).unsubscribe(emailCallback.getRecipientEmailAddress());
  }

  @Test
  public void handleEmailFailure_whenSubscriberNotFound_thenVerifyNoUnsubscribeInteraction() {

    final var failedEmailAddress = "someone@example.com";

    final var emailCallback = EmailCallbackTestUtil.constructEmailCallback(failedEmailAddress);

    when(subscriptionService.isSubscribed(emailCallback.getRecipientEmailAddress())).thenReturn(false);

    newsletterEmailFailureHandlerService.handleEmailFailure(emailCallback);

    verify(subscriptionService, never()).unsubscribe(emailCallback.getRecipientEmailAddress());
  }
}