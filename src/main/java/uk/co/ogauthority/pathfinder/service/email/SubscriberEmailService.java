package uk.co.ogauthority.pathfinder.service.email;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.subscription.SubscribedToNewsletterEmailProperties;
import uk.co.ogauthority.pathfinder.service.LinkService;

@Service
public class SubscriberEmailService {

  private final EmailService emailService;
  private final LinkService linkService;

  @Autowired
  public SubscriberEmailService(EmailService emailService,
                                LinkService linkService) {
    this.emailService = emailService;
    this.linkService = linkService;
  }

  public void sendSubscribedEmail(String forename, String emailAddress, UUID subscriberUuid) {
    var manageSubscriptionUrl = linkService.getManageSubscriptionUrl(subscriberUuid.toString());

    var emailProperties = new SubscribedToNewsletterEmailProperties(
        forename,
        manageSubscriptionUrl
    );

    emailService.sendEmail(emailProperties, emailAddress);
  }
}
