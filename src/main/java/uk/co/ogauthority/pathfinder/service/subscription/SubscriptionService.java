package uk.co.ogauthority.pathfinder.service.subscription;

import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder;
import uk.co.ogauthority.pathfinder.model.form.subscription.SubscribeForm;
import uk.co.ogauthority.pathfinder.model.form.subscription.SubscribeFormValidator;
import uk.co.ogauthority.pathfinder.repository.subscription.SubscriberRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class SubscriptionService {

  public static final String SUBSCRIBE_TEMPLATE_PATH = "subscription/subscribe";
  public static final String SUBSCRIBE_SUCCESS_TEMPLATE_PATH = "subscription/subscribeSuccess";

  private final SubscriberRepository subscriberRepository;
  private final ValidationService validationService;
  private final SubscribeFormValidator subscribeFormValidator;

  @Autowired
  public SubscriptionService(
      SubscriberRepository subscriberRepository,
      ValidationService validationService,
      SubscribeFormValidator subscribeFormValidator) {
    this.subscriberRepository = subscriberRepository;
    this.validationService = validationService;
    this.subscribeFormValidator = subscribeFormValidator;
  }

  public boolean isSubscribed(String emailAddress) {
    return subscriberRepository.existsByEmailAddress(emailAddress);
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
        .addObject("otherRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.OTHER));
  }

  public ModelAndView getSubscribeSuccessModelAndView() {
    return new ModelAndView(SUBSCRIBE_SUCCESS_TEMPLATE_PATH);
  }
}
