package uk.co.ogauthority.pathfinder.service.newsletters;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.newsletters.MonthlyNewsletterEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.newsletters.MonthlyNewsletter;
import uk.co.ogauthority.pathfinder.model.enums.NewsletterSendingResult;
import uk.co.ogauthority.pathfinder.repository.newsletters.MonthlyNewsletterRepository;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.co.ogauthority.pathfinder.service.subscription.SubscriberAccessor;

@Service
public class NewsletterService {
  private final SubscriberAccessor subscriberAccessor;
  private final EmailLinkService emailLinkService;
  private final EmailService emailService;
  private final MonthlyNewsletterRepository monthlyNewsletterRepository;

  @Autowired
  public NewsletterService(SubscriberAccessor subscriberAccessor,
                           EmailLinkService emailLinkService,
                           EmailService emailService,
                           MonthlyNewsletterRepository monthlyNewsletterRepository) {
    this.subscriberAccessor = subscriberAccessor;
    this.emailLinkService = emailLinkService;
    this.emailService = emailService;
    this.monthlyNewsletterRepository = monthlyNewsletterRepository;
  }

  @Transactional
  public void sendNewsletterToSubscribers() {
    var newsletter = new MonthlyNewsletter(Instant.now());

    try {
      var subscribers = subscriberAccessor.getAllSubscribers();
      subscribers.forEach(subscriber -> {
        var emailProperties = new MonthlyNewsletterEmailProperties(
            subscriber.getForename(),
            emailLinkService.getUnsubscribeUrl(subscriber.getUuid().toString())
        );
        emailService.sendEmail(emailProperties, subscriber.getEmailAddress());
      });
      setResultAndSave(newsletter, NewsletterSendingResult.SUCCESS);

    } catch (Exception e) {
      setResultAndSave(newsletter, NewsletterSendingResult.FAILURE);
    }
  }

  private void setResultAndSave(MonthlyNewsletter newsletter, NewsletterSendingResult result) {
    newsletter.setResult(result);
    newsletter.setResultDateTime(Instant.now());
    monthlyNewsletterRepository.save(newsletter);
  }
}
