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

import java.util.UUID;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.controller.subscription.SubscriptionController;
import uk.co.ogauthority.pathfinder.exception.SubscriberNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder;
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

  @InjectMocks
  private SubscriptionService subscriptionService;

  @Before
  public void setup() {

    when(subscriberRepository.save(any(Subscriber.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

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

    verify(subscriberRepository, never()).existsByUuid(any());
  }

  @Test
  void verifyIsSubscribed_whenNotExists_thenError() {
    var subscriberUuid = UUID.randomUUID();

    when(subscriberRepository.existsByUuid(subscriberUuid)).thenReturn(false);

    assertThrows(SubscriberNotFoundException.class,
        () ->subscriptionService.verifyIsSubscribed(subscriberUuid.toString()));
  }

  @Test
  void verifyIsSubscribed_whenExists_thenReturnUuid() {
    var subscriberUuid = UUID.randomUUID();

    when(subscriberRepository.existsByUuid(subscriberUuid)).thenReturn(true);

    var result = subscriptionService.verifyIsSubscribed(subscriberUuid.toString());
    assertThat(result).isEqualTo(subscriberUuid);
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

    verify(subscriberEmailService, times(1)).sendSubscribedEmail(form.getForename(), form.getEmailAddress(), subscriber.getUuid());
  }

  @Test
  void subscribe_whenNotSubscribedAndNotOtherRelationToPathfinder_thenSubscribe() {
    when(subscriberRepository.save(any(Subscriber.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    var form = SubscriptionTestUtil.createSubscribeForm();
    form.setRelationToPathfinder(RelationToPathfinder.SUPPLY_CHAIN);
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
    assertThat(subscriber.getSubscribeReason()).isNull();
    assertThat(subscriber.getSubscribedInstant()).isNotNull();

    verify(subscriberEmailService, times(1)).sendSubscribedEmail(form.getForename(), form.getEmailAddress(), subscriber.getUuid());
  }

  @Test
  void unsubscribe_uuidVariant_verifyInteractions() {
    var subscriberUuid = UUID.randomUUID();

    subscriptionService.unsubscribe(subscriberUuid);

    verify(subscriberRepository, times(1)).deleteByUuid(subscriberUuid);
  }

  @Test
  void unsubscribe_emailAddressVariant_verifyInteractions() {
    final var subscriberEmailAddress = "someone@example.com";

    subscriptionService.unsubscribe(subscriberEmailAddress);

    verify(subscriberRepository, times(1)).deleteByEmailAddress(subscriberEmailAddress);
  }

  @Test
  void subscribe_whenSubscribed_thenNoSubscribe() {
    var form = SubscriptionTestUtil.createSubscribeForm();

    when(subscriberRepository.existsByEmailAddress(form.getEmailAddress())).thenReturn(true);

    subscriptionService.subscribe(form);

    verify(subscriberRepository, times(0)).save(any());
  }

  @Test
  void validate() {
    var form = new SubscribeForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    subscriptionService.validateSubscribeForm(form, bindingResult);

    verify(subscribeFormValidator, times(1)).validate(form, bindingResult);
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void getSubscribeModelAndView() {
    var form = new SubscribeForm();

    var modelAndView = subscriptionService.getSubscribeModelAndView(form);

    assertThat(modelAndView.getViewName()).isEqualTo(SubscriptionService.SUBSCRIBE_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("form", form),
        entry("supplyChainRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.SUPPLY_CHAIN)),
        entry("operatorRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.OPERATOR)),
        entry("otherRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.OTHER)),
        entry("developerRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.DEVELOPER)),
        entry("fieldStages", FieldStage.getAllAsMapOrdered())
    );
  }

  @Test
  void getSubscribeConfirmationModelAndView() {
    var modelAndView = subscriptionService.getSubscribeConfirmationModelAndView();

    assertThat(modelAndView.getViewName()).isEqualTo(SubscriptionService.SUBSCRIBE_CONFIRMATION_TEMPLATE_PATH);
  }

  @Test
  void getUnsubscribeModelAndView() {
    var modelAndView = subscriptionService.getUnsubscribeModelAndView(UUID.randomUUID().toString());

    assertThat(modelAndView.getViewName()).isEqualTo(SubscriptionService.UNSUBSCRIBE_TEMPLATE_PATH);
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
}
