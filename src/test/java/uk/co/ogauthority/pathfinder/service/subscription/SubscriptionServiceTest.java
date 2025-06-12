package uk.co.ogauthority.pathfinder.service.subscription;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.controller.subscription.SubscriptionController;
import uk.co.ogauthority.pathfinder.exception.SubscriberNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;
import uk.co.ogauthority.pathfinder.model.entity.subscription.SubscriberFieldStage;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
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
import uk.co.ogauthority.pathfinder.service.email.SubscriberEmailService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.SubscriptionTestUtil;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

  @Mock
  private SubscriberRepository subscriberRepository;

  @Mock
  private ValidationService validationService;

  @Mock
  private SubscriberEmailService subscriberEmailService;

  @Mock
  private SubscribeFormValidator subscribeFormValidator;

  @Mock
  private SubscriberFieldStageRepository fieldStageRepository;

  @Mock
  private ServiceProperties serviceProperties;

  @Mock
  private ManageSubscriptionFormValidator manageSubscriptionFormValidator;

  @InjectMocks
  private SubscriptionService subscriptionService;

  private static final String SERVICE_NAME = "Service name";

  @Test
  void isSubscribed_whenNotExists_thenFalse() {
    var emailAddress = "test@test.com";

    when(subscriberRepository.existsByEmailAddress(emailAddress)).thenReturn(false);

    assertThat(subscriptionService.isSubscribed(emailAddress)).isFalse();
  }

  @Test
  void isSubscribed_whenExists_thenTrue() {
    var emailAddress = "test@test.com";

    when(subscriberRepository.existsByEmailAddress(emailAddress)).thenReturn(true);

    assertThat(subscriptionService.isSubscribed(emailAddress)).isTrue();
  }

  @Test
  void verifyIsSubscribed_whenInvalidUuid_thenError() {
    var subscriberUuid = "invaliduuid";

    assertThrows(SubscriberNotFoundException.class,
        () -> subscriptionService.verifyIsSubscribed(subscriberUuid));

    verify(subscriberRepository, never()).findByUuid(any());
  }

  @Test
  void verifyIsSubscribed_whenNotExists_thenError() {
    var subscriberUuid = UUID.randomUUID();
    var subscriberUuidString = subscriberUuid.toString();

    when(subscriberRepository.findByUuid(subscriberUuid)).thenReturn(Optional.empty());

    var result = subscriptionService.verifyIsSubscribed(subscriberUuidString);
    assertThat(result).isEmpty();
  }

  @Test
  void verifyIsSubscribed_whenExists_thenReturnUuid() {
    var subscriberUuid = UUID.randomUUID();
    var subscriber = SubscriptionTestUtil.createSubscriber();

    when(subscriberRepository.findByUuid(subscriberUuid)).thenReturn(Optional.of(subscriber));

    var result = subscriptionService.verifyIsSubscribed(subscriberUuid.toString());
    assertThat(result).isEqualTo(Optional.of(subscriber));
  }

  @Test
  void subscribe_whenNotSubscribedAndOtherRelationToPathfinder_thenSubscribe() {
    when(subscriberRepository.save(any(Subscriber.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    var form = SubscriptionTestUtil.createSubscribeForm();
    form.setRelationToPathfinder(RelationToPathfinder.OTHER);
    form.setSubscribeReason("Subscribe reason");

    when(subscriberRepository.existsByEmailAddress(form.getEmailAddress())).thenReturn(false);

    subscriptionService.subscribe(form);

    ArgumentCaptor<Subscriber> subscriberCaptor = ArgumentCaptor.forClass(Subscriber.class);
    verify(subscriberRepository, times(1)).save(subscriberCaptor.capture());
    var subscriber = subscriberCaptor.getValue();

    assertThat(subscriber.getUuid()).isNotNull();
    assertThat(subscriber.getForename()).isEqualTo(form.getForename());
    assertThat(subscriber.getSurname()).isEqualTo(form.getSurname());
    assertThat(subscriber.getEmailAddress()).isEqualTo(form.getEmailAddress());
    assertThat(subscriber.getRelationToPathfinder()).isEqualTo(form.getRelationToPathfinder());
    assertThat(subscriber.getSubscribeReason()).isEqualTo(form.getSubscribeReason());
    assertThat(subscriber.getSubscribedInstant()).isNotNull();

    ArgumentCaptor<List<SubscriberFieldStage>> fieldStagesCaptor = ArgumentCaptor.forClass(List.class);
    verify(fieldStageRepository).deleteAllBySubscriberUuid(subscriber.getUuid());
    verify(fieldStageRepository).saveAll(fieldStagesCaptor.capture());
    var subscriberFieldStages = fieldStagesCaptor.getValue();

    assertThat(subscriberFieldStages).hasSize(2);
    assertThat(subscriberFieldStages.get(0).getFieldStage()).isEqualTo(FieldStage.OIL_AND_GAS);
    assertThat(subscriberFieldStages.get(0).getSubscriberUuid()).isEqualTo(subscriber.getUuid());
    assertThat(subscriberFieldStages.get(1).getFieldStage()).isEqualTo(FieldStage.WIND_ENERGY);
    assertThat(subscriberFieldStages.get(1).getSubscriberUuid()).isEqualTo(subscriber.getUuid());

    verify(subscriberEmailService, times(1)).sendSubscribedEmail(form.getForename(), form.getEmailAddress(), subscriber.getUuid());
  }

  @Test
  void subscribe_whenNotSubscribedAndNotOtherRelationToPathfinderAndInterestedInAllProjects_thenSubscribe() {
    when(subscriberRepository.save(any(Subscriber.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    var form = SubscriptionTestUtil.createSubscribeForm();
    form.setRelationToPathfinder(RelationToPathfinder.SUPPLY_CHAIN);
    form.setSubscribeReason("Subscribe reason");
    form.setInterestedInAllProjects(true);
    form.setFieldStages(Collections.emptyList());

    when(subscriberRepository.existsByEmailAddress(form.getEmailAddress())).thenReturn(false);

    subscriptionService.subscribe(form);

    ArgumentCaptor<Subscriber> subscriberCaptor = ArgumentCaptor.forClass(Subscriber.class);
    verify(subscriberRepository, times(1)).save(subscriberCaptor.capture());
    var subscriber = subscriberCaptor.getValue();

    assertThat(subscriber.getUuid()).isNotNull();
    assertThat(subscriber.getForename()).isEqualTo(form.getForename());
    assertThat(subscriber.getSurname()).isEqualTo(form.getSurname());
    assertThat(subscriber.getEmailAddress()).isEqualTo(form.getEmailAddress());
    assertThat(subscriber.getRelationToPathfinder()).isEqualTo(form.getRelationToPathfinder());
    assertThat(subscriber.getSubscribeReason()).isNull();
    assertThat(subscriber.getSubscribedInstant()).isNotNull();

    ArgumentCaptor<List<SubscriberFieldStage>> fieldStagesCaptor = ArgumentCaptor.forClass(List.class);
    verify(fieldStageRepository).deleteAllBySubscriberUuid(subscriber.getUuid());
    verify(fieldStageRepository).saveAll(fieldStagesCaptor.capture());
    var subscriberFieldStages = fieldStagesCaptor.getValue();
    assertThat(subscriberFieldStages).hasSize(FieldStage.getAllAsMap().size());

    verify(subscriberEmailService, times(1)).sendSubscribedEmail(form.getForename(), form.getEmailAddress(), subscriber.getUuid());
  }

  @Test
  void unsubscribe_uuidVariant_verifyInteractions() {
    var subscriberUuid = UUID.randomUUID();

    subscriptionService.unsubscribe(subscriberUuid);

    verify(subscriberRepository, times(1)).deleteByUuid(subscriberUuid);
    verify(fieldStageRepository).deleteAllBySubscriberUuid(subscriberUuid);
  }

  @Test
  void unsubscribe_emailAddressVariant_verifyInteractions() {
    final var subscriberEmailAddress = "someone@example.com";
    var subscriber = SubscriptionTestUtil.createSubscriber(subscriberEmailAddress);
    var subscriberUuids = List.of(subscriber.getUuid());

    when(subscriberRepository.findAllByEmailAddress(subscriberEmailAddress)).thenReturn(List.of(subscriber));
    subscriptionService.unsubscribe(subscriberEmailAddress);

    verify(fieldStageRepository).deleteAllBySubscriberUuidIn(subscriberUuids);
    verify(subscriberRepository).deleteByEmailAddress(subscriberEmailAddress);
  }

  @Test
  void subscribe_whenSubscribed_thenNoSubscribe() {
    var form = SubscriptionTestUtil.createSubscribeForm();

    when(subscriberRepository.existsByEmailAddress(form.getEmailAddress())).thenReturn(true);

    subscriptionService.subscribe(form);

    verify(subscriberRepository, times(0)).save(any());
  }

  @Test
  void updateSubscriptionPreferences() {
    when(subscriberRepository.save(any(Subscriber.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

    var subscriber = SubscriptionTestUtil.createSubscriber();
    var subscriberUuid = subscriber.getUuid();
    when(subscriberRepository.findByUuid(subscriberUuid)).thenReturn(Optional.of(subscriber));

    var form = SubscriptionTestUtil.createSubscribeForm();
    form.setForename("new forename");
    form.setFieldStages(List.of("HYDROGEN"));

    subscriptionService.updateSubscriptionPreferences(form, subscriberUuid);

    ArgumentCaptor<Subscriber> subscriberCaptor = ArgumentCaptor.forClass(Subscriber.class);
    verify(subscriberRepository, times(1)).save(subscriberCaptor.capture());
    var updatedSubscriber = subscriberCaptor.getValue();

    assertThat(updatedSubscriber.getUuid()).isNotNull();
    assertThat(updatedSubscriber.getForename()).isEqualTo(form.getForename());
    assertThat(updatedSubscriber.getSurname()).isEqualTo(form.getSurname());
    assertThat(updatedSubscriber.getEmailAddress()).isEqualTo(form.getEmailAddress());
    assertThat(updatedSubscriber.getRelationToPathfinder()).isEqualTo(form.getRelationToPathfinder());
    assertThat(updatedSubscriber.getSubscribeReason()).isEqualTo(form.getSubscribeReason());
    assertThat(updatedSubscriber.getSubscribedInstant()).isNotNull();

    ArgumentCaptor<List<SubscriberFieldStage>> fieldStagesCaptor = ArgumentCaptor.forClass(List.class);
    verify(fieldStageRepository).deleteAllBySubscriberUuid(subscriberUuid);
    verify(fieldStageRepository).saveAll(fieldStagesCaptor.capture());

    var subscriberFieldStages = fieldStagesCaptor.getValue();
    assertThat(subscriberFieldStages).hasSize(1);
    assertThat(subscriberFieldStages.getFirst().getFieldStage()).isEqualTo(FieldStage.HYDROGEN);
    assertThat(subscriberFieldStages.getFirst().getSubscriberUuid()).isEqualTo(subscriberUuid);
  }

  @Test
  void validateSubscribeForm() {
    var form = new SubscribeForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    subscriptionService.validateSubscribeForm(form, bindingResult);

    verify(subscribeFormValidator, times(1)).validate(form, bindingResult);
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void validateManageSubscriptionForm() {
    var form = new ManageSubscriptionForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    subscriptionService.validateManageSubscriptionForm(form, bindingResult);
    verify(manageSubscriptionFormValidator).validate(form, bindingResult);
    verify(validationService).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void getSubscribeModelAndView() {
    var form = new SubscribeForm();
    var pageHeading = "Subscribe to page heading";

    var modelAndView = subscriptionService.getSubscribeModelAndView(form, pageHeading);

    assertThat(modelAndView.getViewName()).isEqualTo(SubscriptionService.SUBSCRIBE_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("form", form),
        entry("pageHeading", pageHeading),
        entry("supplyChainRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.SUPPLY_CHAIN)),
        entry("operatorRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.OPERATOR)),
        entry("otherRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.OTHER)),
        entry("developerRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.DEVELOPER)),
        entry("fieldStages", FieldStage.getAllAsMap())
    );
  }

  @Test
  void getSubscribeConfirmationModelAndView() {
    var modelAndView = subscriptionService.getSubscribeConfirmationModelAndView();

    assertThat(modelAndView.getViewName()).isEqualTo(SubscriptionService.SUBSCRIBE_CONFIRMATION_TEMPLATE_PATH);
  }

  @Test
  void getSubscriptionUpdatedConfirmationModelAndView() {
    var subscriberUuid = UUID.randomUUID();
    var modelAndView = subscriptionService.getSubscriptionUpdatedConfirmationModelAndView(subscriberUuid);

    assertThat(modelAndView.getViewName()).isEqualTo(SubscriptionService.SUBSCRIPTION_UPDATED_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("backToManageUrl", ReverseRouter.route(on(SubscriptionController.class)
            .getManageSubscription(subscriberUuid.toString())))
    );
  }

  @Test
  void getUnsubscribeModelAndView() {
    var subscriberUuid = UUID.randomUUID();
    var modelAndView = subscriptionService.getUnsubscribeModelAndView(subscriberUuid.toString());

    assertThat(modelAndView.getViewName()).isEqualTo(SubscriptionService.UNSUBSCRIBE_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("backToManageUrl", ReverseRouter.route(on(SubscriptionController.class)
            .getManageSubscription(subscriberUuid.toString())))
    );
  }

  @Test
  void getUnsubscribeConfirmationModelAndView() {
    var modelAndView = subscriptionService.getUnsubscribeConfirmationModelAndView();

    assertThat(modelAndView.getViewName()).isEqualTo(SubscriptionService.UNSUBSCRIBE_CONFIRMATION_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("resubscribeUrl", ReverseRouter.route(on(SubscriptionController.class).getSubscribe()))
    );
  }

  @Test
  void getAlreadyUnsubscribedModelAndView() {
    var modelAndView = subscriptionService.getAlreadyUnsubscribedModelAndView();

    assertThat(modelAndView.getViewName()).isEqualTo(SubscriptionService.ALREADY_UNSUBSCRIBED_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
      entry("resubscribeUrl", ReverseRouter.route(on(SubscriptionController.class).getSubscribe()))
    );
  }

  @Test
  void getManageSubscriptionModelAndView() {
    var subscriberUuid = UUID.randomUUID();
    var form = new ManageSubscriptionForm();
    var email = "test@email.com";
    var subscriber = SubscriptionTestUtil.createSubscriber(email);
    subscriber.setUuid(subscriberUuid);
    when(subscriberRepository.findByUuid(subscriberUuid)).thenReturn(Optional.of(subscriber));

    var modelAndView = subscriptionService.getManageSubscriptionModelAndView(subscriberUuid, form);

    assertThat(modelAndView.getViewName()).isEqualTo(SubscriptionService.MANAGE_SUBSCRIPTION_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("form", form),
        entry("subscriberEmail", email),
        entry("managementOptions", SubscriptionManagementOption.getAllAsMap())
    );
  }

  @Test
  void getManagementRouting_unsubscribe() {
    var form = new ManageSubscriptionForm();
    form.setSubscriptionManagementOption(SubscriptionManagementOption.UNSUBSCRIBE.name());
    var subscriberUuid = UUID.randomUUID();

    var modelAndView = subscriptionService.getManagementRouting(subscriberUuid, form);
    var expectedViewName = ReverseRouter.redirect(on(SubscriptionController.class)
            .getUnsubscribe(subscriberUuid.toString())).getViewName();
    assertThat(modelAndView.getViewName()).isEqualTo(expectedViewName);
  }

  @Test
  void getManagementRouting_updateSubscriptionPreferences() {
    var form = new ManageSubscriptionForm();
    form.setSubscriptionManagementOption(SubscriptionManagementOption.UPDATE_SUBSCRIPTION.name());
    var subscriberUuid = UUID.randomUUID();

    var modelAndView = subscriptionService.getManagementRouting(subscriberUuid, form);
    var expectedViewName = ReverseRouter.redirect(on(SubscriptionController.class)
        .getUpdateSubscriptionPreferences(subscriberUuid.toString())).getViewName();
    assertThat(modelAndView.getViewName()).isEqualTo(expectedViewName);
  }

  @Test
  void getUpdateSubscriptionPreferencesModelAndView() {
    when(serviceProperties.getServiceName()).thenReturn(SERVICE_NAME);
    var subscriberUuid = UUID.randomUUID();
    var form = SubscriptionTestUtil.createSubscribeForm();
    var expectedPageHeading = String.format("%s %s",
        SubscriptionService.UPDATE_SUBSCRIPTION_PAGE_HEADING_PREFIX,
        SERVICE_NAME
    );
    var modelAndView = subscriptionService.getUpdateSubscriptionPreferencesModelAndView(subscriberUuid, form);

    assertThat(modelAndView.getViewName()).isEqualTo(SubscriptionService.SUBSCRIBE_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("form", form),
        entry("pageHeading", expectedPageHeading),
        entry("supplyChainRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.SUPPLY_CHAIN)),
        entry("operatorRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.OPERATOR)),
        entry("otherRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.OTHER)),
        entry("developerRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.DEVELOPER)),
        entry("fieldStages", FieldStage.getAllAsMap()),
        entry("backToManageUrl",
            ReverseRouter.route(on(SubscriptionController.class).getManageSubscription(subscriberUuid.toString())))
    );
  }

  @Test
  void getForm_interestedInAllProjects() {
    var subscriber = SubscriptionTestUtil.createSubscriber();
    var subscriberUuid = subscriber.getUuid();
    when(subscriberRepository.findByUuid(subscriberUuid)).thenReturn(Optional.of(subscriber));

    var subscriberFieldStages = SubscriptionTestUtil.createSubscriberFieldStages(
        FieldStage.getAllAsList(), subscriberUuid
    );
    when(fieldStageRepository.findAllBySubscriberUuid(subscriberUuid)).thenReturn(subscriberFieldStages);

    var form = subscriptionService.getForm(subscriberUuid);
    assertThat(form.getForename()).isEqualTo(subscriber.getForename());
    assertThat(form.getSurname()).isEqualTo(subscriber.getSurname());
    assertThat(form.getEmailAddress()).isEqualTo(subscriber.getEmailAddress());
    assertThat(form.getRelationToPathfinder()).isEqualTo(subscriber.getRelationToPathfinder());
    assertThat(form.getSubscribeReason()).isEqualTo(subscriber.getSubscribeReason());
    assertThat(form.getInterestedInAllProjects()).isTrue();
    assertThat(form.getFieldStages()).isEmpty();
  }

  @Test
  void getForm_notInterestedInAllProjects() {
    var subscriber = SubscriptionTestUtil.createSubscriber();
    var subscriberUuid = subscriber.getUuid();
    when(subscriberRepository.findByUuid(subscriberUuid)).thenReturn(Optional.of(subscriber));

    var fieldStages = List.of(FieldStage.OIL_AND_GAS, FieldStage.HYDROGEN);
    var expectedFieldStages = List.of("OIL_AND_GAS", "HYDROGEN");
    var subscriberFieldStages = SubscriptionTestUtil.createSubscriberFieldStages(
        fieldStages, subscriberUuid
    );
    when(fieldStageRepository.findAllBySubscriberUuid(subscriberUuid)).thenReturn(subscriberFieldStages);

    var form = subscriptionService.getForm(subscriberUuid);
    assertThat(form.getForename()).isEqualTo(subscriber.getForename());
    assertThat(form.getSurname()).isEqualTo(subscriber.getSurname());
    assertThat(form.getEmailAddress()).isEqualTo(subscriber.getEmailAddress());
    assertThat(form.getRelationToPathfinder()).isEqualTo(subscriber.getRelationToPathfinder());
    assertThat(form.getSubscribeReason()).isEqualTo(subscriber.getSubscribeReason());
    assertThat(form.getInterestedInAllProjects()).isFalse();
    assertThat(form.getFieldStages()).isEqualTo(expectedFieldStages);
  }
}
