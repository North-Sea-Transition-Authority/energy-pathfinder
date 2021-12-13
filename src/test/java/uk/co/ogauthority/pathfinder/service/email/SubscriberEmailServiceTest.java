package uk.co.ogauthority.pathfinder.service.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.subscription.SubscribedToNewsletterEmailProperties;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;

@RunWith(MockitoJUnitRunner.class)
public class SubscriberEmailServiceTest {

  @Mock
  private EmailService emailService;

  @Mock
  private LinkService linkService;

  private SubscriberEmailService subscriberEmailService;

  @Before
  public void setup() {
    subscriberEmailService = new SubscriberEmailService(emailService, linkService);
  }

  @Test
  public void sendSubscribedEmail() {
    var forename = "Test forename";
    var emailAddress = "test@test.com";
    var subscriberUuid = UUID.randomUUID();
    var unsubscribeUrl = "testUrl";

    when(linkService.getUnsubscribeUrl(subscriberUuid.toString())).thenReturn(unsubscribeUrl);

    subscriberEmailService.sendSubscribedEmail(forename, emailAddress, subscriberUuid);

    ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
    verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(emailAddress));
    SubscribedToNewsletterEmailProperties emailProperties = (SubscribedToNewsletterEmailProperties) emailCaptor.getValue();

    final var expectedEmailProperties = new HashMap<String, Object>();
    expectedEmailProperties.put(CommonEmailMergeField.RECIPIENT_IDENTIFIER, forename);
    expectedEmailProperties.put("UNSUBSCRIBE_URL", unsubscribeUrl);
    assertThat(emailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
  }
}
