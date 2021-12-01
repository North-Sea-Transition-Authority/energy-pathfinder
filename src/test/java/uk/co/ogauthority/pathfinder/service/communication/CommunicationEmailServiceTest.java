package uk.co.ogauthority.pathfinder.service.communication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.entity.communication.CommunicationRecipient;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;
import uk.co.ogauthority.pathfinder.testutil.CommunicationTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class CommunicationEmailServiceTest {

  @Mock
  private EmailService emailService;

  @Mock
  private CommunicationRecipientService communicationRecipientService;

  private CommunicationEmailService communicationEmailService;

  @Before
  public void setup() {
    communicationEmailService = new CommunicationEmailService(emailService, communicationRecipientService);
  }

  @Test
  public void sendCommunicationEmail_whenOperatorRecipients_verifyInteractions() {
    final var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setRecipientType(RecipientType.OPERATORS);

    final var recipientList = getTestRecipientList();

    sendCommunicationEmail_verifyCommonInteractionsAndAssertions(communication, recipientList);

    ArgumentCaptor<List<CommunicationRecipient>> captor = ArgumentCaptor.forClass(List.class);
    verify(communicationRecipientService, times(1)).saveCommunicationRecipients(captor.capture());

    var communicationRecipientList = captor.getValue();
    assertThat(recipientList.size()).isEqualTo(communicationRecipientList.size());
    assertCommunicationRecipientProperties(communicationRecipientList.get(0), recipientList.get(0), communication);
    assertCommunicationRecipientProperties(communicationRecipientList.get(1), recipientList.get(1), communication);
  }

  @Test
  public void sendCommunicationEmail_whenSubscriberRecipients_verifyInteractions() {
    final var communication = CommunicationTestUtil.getCompleteCommunication();
    communication.setRecipientType(RecipientType.SUBSCRIBERS);

    final var recipientList = getTestRecipientList();

    sendCommunicationEmail_verifyCommonInteractionsAndAssertions(communication, recipientList);

    verify(communicationRecipientService, never()).saveCommunicationRecipients(any());
  }

  public void sendCommunicationEmail_verifyCommonInteractionsAndAssertions(Communication communication,
                                                                           List<Recipient> recipientList) {

    communicationEmailService.sendCommunicationEmail(communication, recipientList);

    recipientList.forEach(recipient -> {

      ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
      verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(recipient.getEmailAddress()));

      assertThat(emailCaptor.getValue().getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(
          Map.of(
              "SUBJECT", communication.getEmailSubject(),
              "BODY", communication.getEmailBody(),
              CommonEmailMergeField.RECIPIENT_IDENTIFIER, recipient.getForename()
          )
      );
    });
  }

  private void assertCommunicationRecipientProperties(CommunicationRecipient communicationRecipient,
                                                      Recipient recipient,
                                                      Communication communication) {
    assertThat(communicationRecipient.getCommunication()).isEqualTo(communication);
    assertThat(communicationRecipient.getSentToEmailAddress()).isEqualTo(recipient.getEmailAddress());
    assertThat(communicationRecipient.getSentInstant()).isNotNull();
  }

  private List<Recipient> getTestRecipientList() {
    return List.of(
        new Recipient("someone@example.com", "Someone1", "Example"),
        new Recipient("someone.else@example.com", "Someone2", "Else")
    );
  }
}