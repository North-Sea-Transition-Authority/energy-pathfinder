package uk.co.ogauthority.pathfinder.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.feedback.FeedbackFailedToSendEmailProperties;
import uk.co.ogauthority.pathfinder.service.email.EmailService;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackEmailServiceTest {

  @Mock
  private EmailService emailService;

  private FeedbackEmailService feedbackEmailService;

  @Before
  public void setup() {
    feedbackEmailService = new FeedbackEmailService(emailService);
  }

  @Test
  public void sendFeedbackFailedToSendEmail(){
    var feedbackContent = "testContent";
    var emailAddress = "test@test.com";
    var recipientName = "testRecipient";

    feedbackEmailService.sendFeedbackFailedToSendEmail(feedbackContent, emailAddress, recipientName);

    ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
    verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(emailAddress));
    FeedbackFailedToSendEmailProperties emailProperties = (FeedbackFailedToSendEmailProperties) emailCaptor.getValue();

    final var expectedEmailProperties = new HashMap<String, Object>();
    expectedEmailProperties.put("FEEDBACK_CONTENT", feedbackContent);
    expectedEmailProperties.put("RECIPIENT_IDENTIFIER", recipientName);
    assertThat(emailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
  }

}