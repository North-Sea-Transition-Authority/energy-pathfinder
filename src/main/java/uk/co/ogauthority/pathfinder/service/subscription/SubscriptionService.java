package uk.co.ogauthority.pathfinder.service.subscription;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.controller.subscription.SubscriptionController;
import uk.co.ogauthority.pathfinder.exception.SubscriberNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;
import uk.co.ogauthority.pathfinder.model.entity.subscription.SubscriberFieldStage;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder;
import uk.co.ogauthority.pathfinder.model.enums.subscription.SubscriptionManagementOption;
import uk.co.ogauthority.pathfinder.model.form.subscription.ManageSubscriptionForm;
import uk.co.ogauthority.pathfinder.model.form.subscription.ManageSubscriptionFormValidator;
import uk.co.ogauthority.pathfinder.model.form.subscription.SubscribeForm;
import uk.co.ogauthority.pathfinder.model.form.subscription.SubscribeFormValidator;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.subscription.SubscriberFieldStageRepository;
import uk.co.ogauthority.pathfinder.repository.subscription.SubscriberRepository;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.email.SubscriberEmailService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class SubscriptionService {

  static final String SUBSCRIBE_TEMPLATE_PATH = "subscription/subscribe";
  static final String SUBSCRIBE_CONFIRMATION_TEMPLATE_PATH = "subscription/subscribeConfirmation";
  static final String UNSUBSCRIBE_TEMPLATE_PATH = "subscription/unsubscribe";
  static final String UNSUBSCRIBE_CONFIRMATION_TEMPLATE_PATH = "subscription/unsubscribeConfirmation";
  static final String ALREADY_UNSUBSCRIBED_TEMPLATE_PATH = "subscription/alreadyUnsubscribed";
  static final String MANAGE_SUBSCRIPTION_TEMPLATE_PATH = "subscription/manageSubscription";
  static final String SUBSCRIPTION_UPDATED_TEMPLATE_PATH = "subscription/subscriptionUpdatedConfirmation";
  static final String UPDATE_SUBSCRIPTION_PAGE_HEADING_PREFIX = "Update subscription details for";

  private final SubscriberRepository subscriberRepository;
  private final ValidationService validationService;
  private final SubscriberEmailService subscriberEmailService;
  private final SubscribeFormValidator subscribeFormValidator;
  private final SubscriberFieldStageRepository subscriberFieldStageRepository;
  private final ServiceProperties serviceProperties;
  private final ManageSubscriptionFormValidator manageSubscriptionFormValidator;

  @Autowired
  public SubscriptionService(
      SubscriberRepository subscriberRepository,
      ValidationService validationService,
      SubscriberEmailService subscriberEmailService,
      SubscribeFormValidator subscribeFormValidator,
      SubscriberFieldStageRepository subscriberFieldStageRepository,
      ServiceProperties serviceProperties,
      ManageSubscriptionFormValidator manageSubscriptionFormValidator
  ) {
    this.subscriberRepository = subscriberRepository;
    this.validationService = validationService;
    this.subscriberEmailService = subscriberEmailService;
    this.subscribeFormValidator = subscribeFormValidator;
    this.subscriberFieldStageRepository = subscriberFieldStageRepository;
    this.serviceProperties = serviceProperties;
    this.manageSubscriptionFormValidator = manageSubscriptionFormValidator;
  }

  public boolean isSubscribed(String emailAddress) {
    return subscriberRepository.existsByEmailAddress(emailAddress);
  }

  public Optional<Subscriber> verifyIsSubscribed(String subscriberUuid) {
    UUID uuid;
    try {
      uuid = UUID.fromString(subscriberUuid);
    } catch (IllegalArgumentException exception) {
      throw new SubscriberNotFoundException(String.format("Unable to convert %s to UUID", subscriberUuid));
    }
    return subscriberRepository.findByUuid(uuid);
  }

  @Transactional
  public void subscribe(SubscribeForm form) {
    if (isSubscribed(form.getEmailAddress())) {
      return;
    }
    var subscriber = new Subscriber();
    subscriber.setUuid(UUID.randomUUID());
    saveSubscriber(form, subscriber);
    subscriberEmailService.sendSubscribedEmail(form.getForename(), form.getEmailAddress(), subscriber.getUuid());

    AuditService.audit(
        AuditEvent.SUBSCRIBER_SIGN_UP_REQUEST,
        String.format(AuditEvent.SUBSCRIBER_SIGN_UP_REQUEST.getMessage(), subscriber.getId())
    );
  }

  @Transactional
  public void updateSubscriptionPreferences(SubscribeForm form, UUID subscriberUuid) {
    var subscriber = getSubscriber(subscriberUuid);
    saveSubscriber(form, subscriber);
  }

  private void saveSubscriber(SubscribeForm form, Subscriber subscriber) {
    subscriber.setForename(form.getForename());
    subscriber.setSurname(form.getSurname());
    subscriber.setEmailAddress(form.getEmailAddress());
    subscriber.setRelationToPathfinder(form.getRelationToPathfinder());
    if (RelationToPathfinder.OTHER.equals(form.getRelationToPathfinder())) {
      subscriber.setSubscribeReason(form.getSubscribeReason());
    }
    subscriber.setSubscribedInstant(Instant.now());
    subscriberRepository.save(subscriber);
    saveSubscriberFieldStages(form, subscriber);
  }

  private void saveSubscriberFieldStages(SubscribeForm form, Subscriber subscriber) {
    List<FieldStage> fieldStages = null;
    if (Boolean.FALSE.equals(form.getInterestedInAllProjects())) {
      fieldStages = convertStringsToFieldStages(form.getFieldStages());
    }
    if (Objects.isNull(fieldStages)) {
      fieldStages = FieldStage.getAllAsList();
    }

    var subscriberFieldStages = fieldStages.stream()
        .map(fieldStage -> {
              var subscriberFieldStage = new SubscriberFieldStage();
              subscriberFieldStage.setFieldStage(fieldStage);
              subscriberFieldStage.setSubscriberUuid(subscriber.getUuid());
              return subscriberFieldStage;
            }
        )
        .collect(Collectors.toList());

    subscriberFieldStageRepository.deleteAllBySubscriberUuid(subscriber.getUuid());
    subscriberFieldStageRepository.saveAll(subscriberFieldStages);
  }

  @Transactional
  public void unsubscribe(UUID subscriberUuid) {
    subscriberFieldStageRepository.deleteAllBySubscriberUuid(subscriberUuid);
    subscriberRepository.deleteByUuid(subscriberUuid);
  }

  @Transactional
  public void unsubscribe(String emailAddress) {
    var subscribers = subscriberRepository.findAllByEmailAddress(emailAddress);
    var subscriberUuids = subscribers.stream().map(Subscriber::getUuid).collect(Collectors.toList());
    subscriberFieldStageRepository.deleteAllBySubscriberUuidIn(subscriberUuids);
    subscriberRepository.deleteByEmailAddress(emailAddress);
  }

  public BindingResult validateSubscribeForm(SubscribeForm form, BindingResult bindingResult) {
    subscribeFormValidator.validate(form, bindingResult);
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  public BindingResult validateManageSubscriptionForm(ManageSubscriptionForm form, BindingResult bindingResult) {
    manageSubscriptionFormValidator.validate(form, bindingResult);
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  public ModelAndView getSubscribeModelAndView(SubscribeForm form, String pageHeading) {
    return new ModelAndView(SUBSCRIBE_TEMPLATE_PATH)
        .addObject("form", form)
        .addObject("pageHeading", pageHeading)
        .addObject("supplyChainRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.SUPPLY_CHAIN))
        .addObject("operatorRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.OPERATOR))
        .addObject("otherRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.OTHER))
        .addObject("developerRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.DEVELOPER))
        .addObject("fieldStages", FieldStage.getAllAsMap());
  }

  public ModelAndView getSubscribeConfirmationModelAndView() {
    return new ModelAndView(SUBSCRIBE_CONFIRMATION_TEMPLATE_PATH);
  }

  public ModelAndView getSubscriptionUpdatedConfirmationModelAndView(UUID subscriberUuid) {
    return new ModelAndView(SUBSCRIPTION_UPDATED_TEMPLATE_PATH)
        .addObject("backToManageUrl",
            ReverseRouter.route(on(SubscriptionController.class).getManageSubscription(subscriberUuid.toString())));
  }

  public ModelAndView getUnsubscribeModelAndView(String subscriberUuid) {
    return new ModelAndView(UNSUBSCRIBE_TEMPLATE_PATH)
        .addObject("backToManageUrl",
            ReverseRouter.route(on(SubscriptionController.class).getManageSubscription(subscriberUuid)));
  }

  public ModelAndView getUnsubscribeConfirmationModelAndView() {
    return new ModelAndView(UNSUBSCRIBE_CONFIRMATION_TEMPLATE_PATH)
        .addObject("resubscribeUrl", ReverseRouter.route(on(SubscriptionController.class).getSubscribe()));
  }

  public ModelAndView getAlreadyUnsubscribedModelAndView() {
    return new ModelAndView(ALREADY_UNSUBSCRIBED_TEMPLATE_PATH)
      .addObject("resubscribeUrl", ReverseRouter.route(on(SubscriptionController.class).getSubscribe()));
  }

  public ModelAndView getManageSubscriptionModelAndView(UUID subscriberUuid, ManageSubscriptionForm form) {
    var subscriber = getSubscriber(subscriberUuid);
    return new ModelAndView(MANAGE_SUBSCRIPTION_TEMPLATE_PATH)
        .addObject("form", form)
        .addObject("subscriberEmail", subscriber.getEmailAddress())
        .addObject("managementOptions", SubscriptionManagementOption.getAllAsMap());
  }

  public ModelAndView getManagementRouting(UUID subscriberUuid, ManageSubscriptionForm form) {
    if (SubscriptionManagementOption.UNSUBSCRIBE.name().equals(form.getSubscriptionManagementOption())) {
      return ReverseRouter.redirect(on(SubscriptionController.class).getUnsubscribe(subscriberUuid.toString()));
    } else {
      return ReverseRouter.redirect(on(SubscriptionController.class).getUpdateSubscriptionPreferences(subscriberUuid.toString()));
    }
  }

  public ModelAndView getUpdateSubscriptionPreferencesModelAndView(UUID subscriberUuid, SubscribeForm form) {
    var pageHeading = String.format("%s %s", UPDATE_SUBSCRIPTION_PAGE_HEADING_PREFIX, serviceProperties.getServiceName());
    return getSubscribeModelAndView(form, pageHeading)
        .addObject("backToManageUrl",
            ReverseRouter.route(on(SubscriptionController.class).getManageSubscription(subscriberUuid.toString())));
  }

  private Subscriber getSubscriber(UUID subscriberUuid) {
    return subscriberRepository.findByUuid(subscriberUuid)
        .orElseThrow(() -> new SubscriberNotFoundException(String.format("Unable to find subscriber with UUID %s", subscriberUuid)));
  }

  public SubscribeForm getForm(UUID subscriberUuid) {
    var form = new SubscribeForm();
    var subscriber = getSubscriber(subscriberUuid);

    form.setForename(subscriber.getForename());
    form.setSurname(subscriber.getSurname());
    form.setEmailAddress(subscriber.getEmailAddress());
    form.setSubscribeReason(subscriber.getSubscribeReason());
    form.setRelationToPathfinder(subscriber.getRelationToPathfinder());

    var subscriberFieldStages = subscriberFieldStageRepository.findAllBySubscriberUuid(subscriber.getUuid());
    var fieldStages = subscriberFieldStages
        .stream()
        .map(SubscriberFieldStage::getFieldStage)
        .collect(Collectors.toList());

    if (FieldStage.getAllAsList().equals(fieldStages)) {
      form.setInterestedInAllProjects(true);
      form.setFieldStages(Collections.emptyList());
    } else {
      form.setInterestedInAllProjects(false);
      form.setFieldStages(convertFieldStagesToString(fieldStages));
    }
    return form;
  }

  private List<FieldStage> convertStringsToFieldStages(List<String> fieldStageStrings) {
    return fieldStageStrings.stream()
        .map(FieldStage::valueOf)
        .collect(Collectors.toList());
  }

  private List<String> convertFieldStagesToString(List<FieldStage> fieldStages) {
    return fieldStages.stream()
        .map((FieldStage::name))
        .collect(Collectors.toList());
  }
}

