package uk.co.ogauthority.pathfinder.service.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.subscription.SubscribedToNewsletterEmailProperties;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;

@ExtendWith(MockitoExtension.class)
class SubscriberEmailServiceTest {

  @Mock
  private EmailService emailService;

  @Mock
  private LinkService linkService;

  @InjectMocks
  private SubscriberEmailService subscriberEmailService;

  @Test
  void sendSubscribedEmail() {
    var forename = "Test forename";
    var emailAddress = "test@test.com";
    var subscriberUuid = UUID.randomUUID();
    var manageSubscriptionUrl = "testUrl";

    when(linkService.getManageSubscriptionUrl(subscriberUuid.toString())).thenReturn(manageSubscriptionUrl);

    subscriberEmailService.sendSubscribedEmail(forename, emailAddress, subscriberUuid);

    ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
    verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(emailAddress));
    SubscribedToNewsletterEmailProperties emailProperties = (SubscribedToNewsletterEmailProperties) emailCaptor.getValue();

    final var expectedEmailProperties = new HashMap<String, Object>();
    expectedEmailProperties.put(CommonEmailMergeField.RECIPIENT_IDENTIFIER, forename);
    expectedEmailProperties.put("MANAGE_SUBSCRIPTION_URL", manageSubscriptionUrl);
    assertThat(emailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
  }
}
