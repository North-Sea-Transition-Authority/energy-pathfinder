package uk.co.ogauthority.pathfinder.service.newsletters;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
import uk.co.ogauthority.pathfinder.model.entity.subscription.SubscriberFieldStage;
import uk.co.ogauthority.pathfinder.model.enums.NewsletterSendingResult;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.repository.newsletters.MonthlyNewsletterRepository;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.co.ogauthority.pathfinder.service.email.notify.DefaultEmailPersonalisationService;
import uk.co.ogauthority.pathfinder.service.subscription.SubscriberAccessor;

@Service
public class NewsletterService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterService.class);

  private final SubscriberAccessor subscriberAccessor;
  private final LinkService linkService;
  private final EmailService emailService;
  private final MonthlyNewsletterRepository monthlyNewsletterRepository;
  private final NewsletterProjectService newsletterProjectService;
  private final DefaultEmailPersonalisationService defaultEmailPersonalisationService;

  @Autowired
  public NewsletterService(SubscriberAccessor subscriberAccessor,
                           LinkService linkService,
                           EmailService emailService,
                           MonthlyNewsletterRepository monthlyNewsletterRepository,
                           NewsletterProjectService newsletterProjectService,
                           DefaultEmailPersonalisationService defaultEmailPersonalisationService) {
    this.subscriberAccessor = subscriberAccessor;
    this.linkService = linkService;
    this.emailService = emailService;
    this.monthlyNewsletterRepository = monthlyNewsletterRepository;
    this.newsletterProjectService = newsletterProjectService;
    this.defaultEmailPersonalisationService = defaultEmailPersonalisationService;
  }

  @Transactional
  public void sendNewsletterToSubscribers() {
    var newsletter = new MonthlyNewsletter(Instant.now());

    try {

      var projectsUpdatedInTheLastMonth = newsletterProjectService.getProjectsUpdatedInTheLastMonth();
      var subscribers = subscriberAccessor.getAllSubscribers();
      var allSubscriberFieldStages = subscriberAccessor.getAllSubscriberFieldStages(subscribers);

      subscribers.forEach(subscriber -> {
        var fieldStages = getFieldStagesForSubscriber(allSubscriberFieldStages, subscriber.getUuid());
        var projects = projectsUpdatedInTheLastMonth.stream()
            .filter(project -> fieldStages.contains(project.getFieldStage()))
            .map(NewsletterProjectView::getProject)
            .collect(Collectors.toList());
        var emailProperties = getEmailProperties(subscriber, projects);
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

  private List<FieldStage> getFieldStagesForSubscriber(List<SubscriberFieldStage> allSubscriberFieldStages,
                                                       UUID subscriberUuid) {
    return allSubscriberFieldStages.stream()
        .filter(subscriberFieldStage -> subscriberUuid.equals(subscriberFieldStage.getSubscriberUuid()))
        .map(SubscriberFieldStage::getFieldStage)
        .collect(Collectors.toList());
  }

  private ProjectNewsletterEmailProperties getEmailProperties(Subscriber subscriber,
                                                              List<String> projectsUpdated) {

    final var serviceName = defaultEmailPersonalisationService.getServiceName();
    final var customerMnemonic = defaultEmailPersonalisationService.getCustomerMnemonic();

    if (projectsUpdated.isEmpty()) {
      return new NoProjectsUpdatedNewsletterEmailProperties(
          subscriber.getForename(),
          linkService.getManageSubscriptionUrl(subscriber.getUuid().toString()),
          serviceName,
          customerMnemonic
      );
    } else {
      return new ProjectsUpdatedNewsletterEmailProperties(
          subscriber.getForename(),
          linkService.getManageSubscriptionUrl(subscriber.getUuid().toString()),
          projectsUpdated,
          serviceName,
          customerMnemonic
      );
    }
  }
}
