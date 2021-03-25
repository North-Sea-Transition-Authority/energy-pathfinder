package uk.co.ogauthority.pathfinder.service.email;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.subscription.SubscribedToNewsletterEmailProperties;

@Service
public class SubscriberEmailService {

  private final EmailService emailService;
  private final EmailLinkService emailLinkService;

  @Autowired
  public SubscriberEmailService(EmailService emailService,
                                EmailLinkService emailLinkService) {
    this.emailService = emailService;
    this.emailLinkService = emailLinkService;
  }

  public void sendSubscribedEmail(String forename, String emailAddress, UUID subscriberUuid) {
    var unsubscribeUrl = emailLinkService.getUnsubscribeUrl(subscriberUuid.toString());

    var emailProperties = new SubscribedToNewsletterEmailProperties(
        forename,
        unsubscribeUrl
    );

    emailService.sendEmail(emailProperties, emailAddress);
  }
}
