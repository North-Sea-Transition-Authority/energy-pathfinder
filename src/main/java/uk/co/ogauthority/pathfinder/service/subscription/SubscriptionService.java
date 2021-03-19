package uk.co.ogauthority.pathfinder.service.subscription;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.subscription.SubscriptionController;
import uk.co.ogauthority.pathfinder.exception.SubscriberNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder;
import uk.co.ogauthority.pathfinder.model.form.subscription.SubscribeForm;
import uk.co.ogauthority.pathfinder.model.form.subscription.SubscribeFormValidator;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.subscription.SubscriberRepository;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.email.SubscriberEmailService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class SubscriptionService {

  public static final String SUBSCRIBE_TEMPLATE_PATH = "subscription/subscribe";
  public static final String SUBSCRIBE_CONFIRMATION_TEMPLATE_PATH = "subscription/subscribeConfirmation";
  public static final String UNSUBSCRIBE_TEMPLATE_PATH = "subscription/unsubscribe";
  public static final String UNSUBSCRIBE_CONFIRMATION_TEMPLATE_PATH = "subscription/unsubscribeConfirmation";

  private final SubscriberRepository subscriberRepository;
  private final ValidationService validationService;
  private final SubscriberEmailService subscriberEmailService;
  private final SubscribeFormValidator subscribeFormValidator;

  @Autowired
  public SubscriptionService(
      SubscriberRepository subscriberRepository,
      ValidationService validationService,
      SubscriberEmailService subscriberEmailService,
      SubscribeFormValidator subscribeFormValidator) {
    this.subscriberRepository = subscriberRepository;
    this.validationService = validationService;
    this.subscriberEmailService = subscriberEmailService;
    this.subscribeFormValidator = subscribeFormValidator;
  }

  public boolean isSubscribed(String emailAddress) {
    return subscriberRepository.existsByEmailAddress(emailAddress);
  }

  public UUID verifyIsSubscribed(String subscriberUuid) {
    UUID uuid;
    try {
      uuid = UUID.fromString(subscriberUuid);
    } catch (IllegalArgumentException exception) {
      throw new SubscriberNotFoundException(String.format("Unable to convert %s to UUID", subscriberUuid));
    }
    if (!subscriberRepository.existsByUuid(uuid)) {
      throw new SubscriberNotFoundException(String.format("Unable to find subscriber with UUID %s", subscriberUuid));
    }
    return uuid;
  }

  @Transactional
  public void subscribe(SubscribeForm form) {
    if (isSubscribed(form.getEmailAddress())) {
      return;
    }
    var subscriber = new Subscriber();
    subscriber.setUuid(UUID.randomUUID());
    subscriber.setForename(form.getForename());
    subscriber.setSurname(form.getSurname());
    subscriber.setEmailAddress(form.getEmailAddress());
    subscriber.setRelationToPathfinder(form.getRelationToPathfinder());
    if (RelationToPathfinder.OTHER.equals(form.getRelationToPathfinder())) {
      subscriber.setSubscribeReason(form.getSubscribeReason());
    }
    subscriber.setSubscribedInstant(Instant.now());
    subscriberRepository.save(subscriber);
    subscriberEmailService.sendSubscribedEmail(form.getForename(), form.getEmailAddress(), subscriber.getUuid());

    AuditService.audit(
        AuditEvent.SUBSCRIBER_SIGN_UP_REQUEST,
        String.format(AuditEvent.SUBSCRIBER_SIGN_UP_REQUEST.getMessage(), subscriber.getId())
    );
  }

  @Transactional
  public void unsubscribe(UUID subscriberUuid) {
    subscriberRepository.deleteByUuid(subscriberUuid);
  }

  public BindingResult validate(SubscribeForm form, BindingResult bindingResult) {
    subscribeFormValidator.validate(form, bindingResult);
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  public ModelAndView getSubscribeModelAndView(SubscribeForm form) {
    return new ModelAndView(SUBSCRIBE_TEMPLATE_PATH)
        .addObject("form", form)
        .addObject("supplyChainRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.SUPPLY_CHAIN))
        .addObject("operatorRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.OPERATOR))
        .addObject("otherRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.OTHER))
        .addObject("developerRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.DEVELOPER));
  }

  public ModelAndView getSubscribeConfirmationModelAndView() {
    return new ModelAndView(SUBSCRIBE_CONFIRMATION_TEMPLATE_PATH);
  }

  public ModelAndView getUnsubscribeModelAndView() {
    return new ModelAndView(UNSUBSCRIBE_TEMPLATE_PATH);
  }

  public ModelAndView getUnsubscribeConfirmationModelAndView() {
    return new ModelAndView(UNSUBSCRIBE_CONFIRMATION_TEMPLATE_PATH)
        .addObject("resubscribeUrl", ReverseRouter.route(on(SubscriptionController.class).getSubscribe()));
  }
}
