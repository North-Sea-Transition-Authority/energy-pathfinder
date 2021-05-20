package uk.co.ogauthority.pathfinder.service.newsletters;

import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.newsletter.NoProjectsUpdatedNewsletterEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.newsletter.ProjectNewsletterEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.newsletter.ProjectsUpdatedNewsletterEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.newsletters.MonthlyNewsletter;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;
import uk.co.ogauthority.pathfinder.model.enums.NewsletterSendingResult;
import uk.co.ogauthority.pathfinder.repository.newsletters.MonthlyNewsletterRepository;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.co.ogauthority.pathfinder.service.subscription.SubscriberAccessor;

@Service
public class NewsletterService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterService.class);

  private final SubscriberAccessor subscriberAccessor;
  private final EmailLinkService emailLinkService;
  private final EmailService emailService;
  private final MonthlyNewsletterRepository monthlyNewsletterRepository;
  private final NewsletterProjectService newsletterProjectService;

  @Autowired
  public NewsletterService(SubscriberAccessor subscriberAccessor,
                           EmailLinkService emailLinkService,
                           EmailService emailService,
                           MonthlyNewsletterRepository monthlyNewsletterRepository,
                           NewsletterProjectService newsletterProjectService) {
    this.subscriberAccessor = subscriberAccessor;
    this.emailLinkService = emailLinkService;
    this.emailService = emailService;
    this.monthlyNewsletterRepository = monthlyNewsletterRepository;
    this.newsletterProjectService = newsletterProjectService;
  }

  @Transactional
  public void sendNewsletterToSubscribers() {
    var newsletter = new MonthlyNewsletter(Instant.now());

    try {

      final var projectsUpdatedInTheLastMonth = newsletterProjectService.getProjectsUpdatedInTheLastMonth();

      subscriberAccessor.getAllSubscribers().forEach(subscriber -> {
        var emailProperties = getEmailProperties(subscriber, projectsUpdatedInTheLastMonth);
        emailService.sendEmail(emailProperties, subscriber.getEmailAddress());
      });

      setResultAndSave(newsletter, NewsletterSendingResult.SUCCESS);

    } catch (Exception e) {
      LOGGER.error("Failed to send subscriber newsletter", e);
      setResultAndSave(newsletter, NewsletterSendingResult.FAILURE);
    }
  }

  private void setResultAndSave(MonthlyNewsletter newsletter, NewsletterSendingResult result) {
    newsletter.setResult(result);
    newsletter.setResultDateTime(Instant.now());
    monthlyNewsletterRepository.save(newsletter);
  }

  private ProjectNewsletterEmailProperties getEmailProperties(Subscriber subscriber,
                                                              List<String> projectsUpdated) {
    if (projectsUpdated.isEmpty()) {
      return new NoProjectsUpdatedNewsletterEmailProperties(
          subscriber.getForename(),
          emailLinkService.getUnsubscribeUrl(subscriber.getUuid().toString())
      );
    } else {
      return new ProjectsUpdatedNewsletterEmailProperties(
          subscriber.getForename(),
          emailLinkService.getUnsubscribeUrl(subscriber.getUuid().toString()),
          projectsUpdated
      );
    }
  }
}
