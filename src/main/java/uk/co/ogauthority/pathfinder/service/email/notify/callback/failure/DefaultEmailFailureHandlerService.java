package uk.co.ogauthority.pathfinder.service.email.notify.callback.failure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailDeliveryFailedEmailProps;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.co.ogauthority.pathfinder.service.email.notify.callback.EmailCallback;

@Service
class DefaultEmailFailureHandlerService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEmailFailureHandlerService.class);

  private final EmailService emailService;

  private final String regulatorSharedEmail;

  @Autowired
  DefaultEmailFailureHandlerService(EmailService emailService,
                                    @Value("${regulator.shared.email}") String regulatorSharedEmail) {
    this.emailService = emailService;
    this.regulatorSharedEmail = regulatorSharedEmail;
  }

  void handleEmailFailure(EmailCallback emailCallback) {

    final var recipientEmailAddress = emailCallback.getRecipientEmailAddress();

    LOGGER.info(
        "Could not deliver email with ID {}",
        emailCallback.getNotificationId()
    );

    // if an email failed and the failed email wasn't going to the regulator mailbox, notify the regulator
    if (!recipientEmailAddress.equals(regulatorSharedEmail)) {

      final var failedEmailProperties = new EmailDeliveryFailedEmailProps(
          recipientEmailAddress,
          emailCallback.getSubject(),
          emailCallback.getBody()
      );

      emailService.sendEmail(failedEmailProperties, regulatorSharedEmail);

    } else {
      // we failed to email the regulator mailbox
      LOGGER.error(
          "Could not send email delivery failure notification to the regulator shared mailbox for notification with ID {}",
          emailCallback.getNotificationId()
      );
    }
  }
}
