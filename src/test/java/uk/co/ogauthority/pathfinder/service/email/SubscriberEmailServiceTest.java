package uk.co.ogauthority.pathfinder.service.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.subscription.SubscribedToNewsletterEmailProperties;
import uk.co.ogauthority.pathfinder.testutil.EmailPropertyTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class SubscriberEmailServiceTest {

  @Mock
  private EmailService emailService;

  @Mock
  private EmailLinkService emailLinkService;

  private SubscriberEmailService subscriberEmailService;

  @Before
  public void setup() {
    subscriberEmailService = new SubscriberEmailService(emailService, emailLinkService);
  }

  @Test
  public void sendSubscribedEmail() {
    var forename = "Test forename";
    var emailAddress = "test@test.com";
    var subscriberUuid = UUID.randomUUID();
    var unsubscribeUrl = "testUrl";

    when(emailLinkService.getUnsubscribeUrl(subscriberUuid.toString())).thenReturn(unsubscribeUrl);

    subscriberEmailService.sendSubscribedEmail(forename, emailAddress, subscriberUuid);

    ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
    verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(emailAddress));
    SubscribedToNewsletterEmailProperties emailProperties = (SubscribedToNewsletterEmailProperties) emailCaptor.getValue();

    var expectedEmailProperties = EmailPropertyTestUtil.getDefaultEmailPersonalisation(
        forename,
        EmailProperties.DEFAULT_SIGN_OFF_IDENTIFIER
    );
    expectedEmailProperties.put("UNSUBSCRIBE_URL", unsubscribeUrl);
    assertThat(emailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
  }
}
