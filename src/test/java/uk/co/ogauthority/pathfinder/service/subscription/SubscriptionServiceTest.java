package uk.co.ogauthority.pathfinder.service.subscription;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder;
import uk.co.ogauthority.pathfinder.model.form.subscription.SubscribeForm;
import uk.co.ogauthority.pathfinder.model.form.subscription.SubscribeFormValidator;
import uk.co.ogauthority.pathfinder.repository.subscription.SubscriberRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.SubscriptionTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionServiceTest {

  @Mock
  private SubscriberRepository subscriberRepository;

  @Mock
  private ValidationService validationService;

  @Mock
  private SubscribeFormValidator subscribeFormValidator;

  private SubscriptionService subscriptionService;

  @Before
  public void setup() {
    subscriptionService = new SubscriptionService(
        subscriberRepository,
        validationService,
        subscribeFormValidator
    );

    when(subscriberRepository.save(any(Subscriber.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void isSubscribed_whenNotExists_thenFalse() {
    var emailAddress = "test@test.com";

    when(subscriberRepository.existsByEmailAddress(emailAddress)).thenReturn(false);

    assertThat(subscriptionService.isSubscribed(emailAddress)).isFalse();
  }

  @Test
  public void isSubscribed_whenExists_thenTrue() {
    var emailAddress = "test@test.com";

    when(subscriberRepository.existsByEmailAddress(emailAddress)).thenReturn(true);

    assertThat(subscriptionService.isSubscribed(emailAddress)).isTrue();
  }

  @Test
  public void subscribe_whenNotSubscribedAndOtherRelationToPathfinder_thenSubscribe() {
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
  }

  @Test
  public void subscribe_whenNotSubscribedAndNotOtherRelationToPathfinder_thenSubscribe() {
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
  }

  @Test
  public void subscribe_whenSubscribed_thenNoSubscribe() {
    var form = SubscriptionTestUtil.createSubscribeForm();

    when(subscriberRepository.existsByEmailAddress(form.getEmailAddress())).thenReturn(true);

    subscriptionService.subscribe(form);

    verify(subscriberRepository, times(0)).save(any());
  }

  @Test
  public void validate() {
    var form = new SubscribeForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    subscriptionService.validate(form, bindingResult);

    verify(subscribeFormValidator, times(1)).validate(form, bindingResult);
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void getSubscribeModelAndView() {
    var form = new SubscribeForm();

    var modelAndView = subscriptionService.getSubscribeModelAndView(form);

    assertThat(modelAndView.getViewName()).isEqualTo(SubscriptionService.SUBSCRIBE_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("form", form),
        entry("supplyChainRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.SUPPLY_CHAIN)),
        entry("operatorRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.OPERATOR)),
        entry("otherRelation", RelationToPathfinder.getEntryAsMap(RelationToPathfinder.OTHER))
    );
  }

  @Test
  public void getSubscribeSuccessModelAndView() {
    var modelAndView = subscriptionService.getSubscribeSuccessModelAndView();

    assertThat(modelAndView.getViewName()).isEqualTo(SubscriptionService.SUBSCRIBE_SUCCESS_TEMPLATE_PATH);
  }
}
