package uk.co.ogauthority.pathfinder.service.communication;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.communication.CommunicationEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.model.entity.communication.CommunicationRecipient;
import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;
import uk.co.ogauthority.pathfinder.service.email.EmailService;

@Service
class CommunicationEmailService {

  private final EmailService emailService;
  private final CommunicationRecipientService communicationRecipientService;

  @Autowired
  CommunicationEmailService(EmailService emailService,
                            CommunicationRecipientService communicationRecipientService) {
    this.emailService = emailService;
    this.communicationRecipientService = communicationRecipientService;
  }

  void sendCommunicationEmail(Communication communication,
                              List<Recipient> recipients) {

    var communicationRecipients = new ArrayList<CommunicationRecipient>();

    recipients.forEach(recipient -> {

      final var recipientEmailAddress = recipient.getEmailAddress();

      var communicationRecipient = new CommunicationRecipient();
      communicationRecipient.setCommunication(communication);
      communicationRecipient.setSentToEmailAddress(recipientEmailAddress);

      final var communicationEmailProperties = new CommunicationEmailProperties(
          recipient.getForename(),
          communication.getEmailSubject(),
          communication.getEmailBody()
      );
      emailService.sendEmail(communicationEmailProperties, recipientEmailAddress);
      communicationRecipient.setSentInstant(Instant.now());
      communicationRecipients.add(communicationRecipient);
    });

    // For GDPR reasons don't log the subscriber details to the communication recipients
    // table. If they unsubscribe we would have to remove their email address from this table as
    // we don't have a good reason to store the email address.
    if (!communication.getRecipientType().equals(RecipientType.SUBSCRIBERS)) {
      communicationRecipientService.saveCommunicationRecipients(communicationRecipients);
    }
  }
}
