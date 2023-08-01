package uk.co.ogauthority.pathfinder.controller.subscription;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Optional;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsEventCategory;
import uk.co.ogauthority.pathfinder.config.MetricsProvider;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.exception.SubscriberNotFoundException;
import uk.co.ogauthority.pathfinder.model.form.subscription.ManageSubscriptionForm;
import uk.co.ogauthority.pathfinder.model.form.subscription.SubscribeForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.subscription.SubscriptionService;
import uk.co.ogauthority.pathfinder.testutil.SubscriptionTestUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(SubscriptionController.class)
public class SubscriptionControllerTest extends AbstractControllerTest {

  private static final UUID SUBSCRIBER_UUID = UUID.randomUUID();
  private static final Class<SubscriptionController> CONTROLLER = SubscriptionController.class;

  private static final String TEST_VIEW_NAME = "test";

  @MockBean
  private SubscriptionService subscriptionService;

  @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
  private MetricsProvider metricsProvider;

  @Test
  public void getSubscribe() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(SubscriptionController.class).getSubscribe())))
        .andExpect(status().isOk());

    verify(metricsProvider.getSubscribePageHitCounter(), times(1)).increment();
  }

  @Test
  public void subscribe_whenValidForm_thenSubscribe() throws Exception {
    var form = new SubscribeForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(subscriptionService.validateSubscribeForm(any(), any())).thenReturn(bindingResult);
    when(subscriptionService.getSubscribeConfirmationModelAndView()).thenReturn(new ModelAndView(TEST_VIEW_NAME));

    mockMvc.perform(
        post(ReverseRouter.route(on(CONTROLLER)
            .subscribe(null, null, Optional.empty())
        )))
        .andExpect(status().isOk());

    verify(subscriptionService, times(1)).validateSubscribeForm(any(), any());
    verify(subscriptionService, times(1)).subscribe(any());
    verify(metricsProvider.getSubscribePagePostCounter(), times(1)).increment();
    verify(analyticsService, times(1)).sendAnalyticsEvent(any(), eq(AnalyticsEventCategory.NEW_SUBSCRIBER));
  }

  @Test
  public void subscribe_whenInvalidForm_thenNoSubscribe() throws Exception {
    var form = new SubscribeForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(subscriptionService.validateSubscribeForm(any(), any())).thenReturn(bindingResult);
    when(subscriptionService.getSubscribeModelAndView(any(), eq(SubscriptionController.SUBSCRIBE_PAGE_HEADING_PREFIX)))
        .thenReturn(new ModelAndView(TEST_VIEW_NAME));

    mockMvc.perform(
        post(ReverseRouter.route(on(CONTROLLER)
            .subscribe(null, null, Optional.empty())
        )))
        .andExpect(status().isOk());

    verify(subscriptionService, times(1)).validateSubscribeForm(any(), any());
    verify(subscriptionService, never()).subscribe(any());
    verify(metricsProvider.getSubscribePagePostCounter(), times(1)).increment();
  }

  @Test
  public void getUnsubscribe_userIsSubscribed() throws Exception {
    when(subscriptionService.getUnsubscribeModelAndView(SUBSCRIBER_UUID.toString()))
        .thenReturn(new ModelAndView(TEST_VIEW_NAME));

    var modelAndView = mockMvc.perform(get(ReverseRouter.route(
        on(CONTROLLER).getUnsubscribe(SUBSCRIBER_UUID.toString()))))
        .andExpect(status().isOk())
      .andReturn()
      .getModelAndView();

    assertThat(modelAndView.getViewName()).isEqualTo(TEST_VIEW_NAME);

    verify(metricsProvider.getUnSubscribePageHitCounter(), times(1)).increment();
  }

  @Test
  public void getUnsubscribe_userIsNotSubscribed() throws Exception {
    when(subscriptionService.verifyIsSubscribed(SUBSCRIBER_UUID.toString()))
      .thenThrow(new SubscriberNotFoundException("User is not subscribed"));
    when(subscriptionService.getAlreadyUnsubscribedModelAndView()).thenReturn(new ModelAndView(TEST_VIEW_NAME));

    var modelAndView = mockMvc.perform(get(ReverseRouter.route(
      on(CONTROLLER).getUnsubscribe(SUBSCRIBER_UUID.toString()))))
      .andExpect(status().isOk())
      .andReturn()
      .getModelAndView();

    assertThat(modelAndView.getViewName()).isEqualTo(TEST_VIEW_NAME);

    verify(metricsProvider.getUnSubscribePageHitCounter(), times(1)).increment();
  }

  @Test
  public void unsubscribe_userIsSubscribed() throws Exception {
    when(subscriptionService.verifyIsSubscribed(SUBSCRIBER_UUID.toString())).thenReturn(SUBSCRIBER_UUID);
    when(subscriptionService.getUnsubscribeConfirmationModelAndView()).thenReturn(new ModelAndView(TEST_VIEW_NAME));

    var modelAndView = mockMvc.perform(
        post(ReverseRouter.route(on(CONTROLLER)
            .unsubscribe(SUBSCRIBER_UUID.toString(), Optional.empty())
        )))
        .andExpect(status().isOk())
      .andReturn()
      .getModelAndView();

    assertThat(modelAndView.getViewName()).isEqualTo(TEST_VIEW_NAME);
    
    verify(subscriptionService, times(1)).verifyIsSubscribed(SUBSCRIBER_UUID.toString());
    verify(subscriptionService, times(1)).unsubscribe(SUBSCRIBER_UUID);
    verify(analyticsService, times(1)).sendAnalyticsEvent(any(), eq(AnalyticsEventCategory.SUBSCRIBER_UNSUBSCRIBED));
    verify(metricsProvider.getUnsubscribePagePostCounter(), times(1)).increment();
  }

  @Test
  public void unsubscribe_userIsNotSubscribed() throws Exception {
    when(subscriptionService.verifyIsSubscribed(SUBSCRIBER_UUID.toString()))
      .thenThrow(new SubscriberNotFoundException("User is not subscribed"));
    when(subscriptionService.getAlreadyUnsubscribedModelAndView()).thenReturn(new ModelAndView(TEST_VIEW_NAME));

    var modelAndView = mockMvc.perform(
      post(ReverseRouter.route(on(CONTROLLER)
        .unsubscribe(SUBSCRIBER_UUID.toString(), Optional.empty())
      )))
      .andExpect(status().isOk())
      .andReturn().getModelAndView();

    assertThat(modelAndView.getViewName()).isEqualTo(TEST_VIEW_NAME);

    verify(subscriptionService, times(1)).verifyIsSubscribed(SUBSCRIBER_UUID.toString());
    verify(subscriptionService, never()).unsubscribe(SUBSCRIBER_UUID);
    verify(metricsProvider.getUnsubscribePagePostCounter(), times(1)).increment();
  }

  @Test
  public void getManageSubscription_userIsSubscribed() throws Exception {
    when(subscriptionService.verifyIsSubscribed(SUBSCRIBER_UUID.toString())).thenReturn(SUBSCRIBER_UUID);
    when(subscriptionService.getManageSubscriptionModelAndView(eq(SUBSCRIBER_UUID), any(ManageSubscriptionForm.class)))
        .thenReturn(new ModelAndView(TEST_VIEW_NAME));

    mockMvc.perform(get(ReverseRouter
            .route(on(CONTROLLER).getManageSubscription(SUBSCRIBER_UUID.toString())
            )))
        .andExpect(status().isOk())
        .andExpect(view().name(TEST_VIEW_NAME));

    verify(subscriptionService).verifyIsSubscribed(SUBSCRIBER_UUID.toString());
    verify(subscriptionService).getManageSubscriptionModelAndView(eq(SUBSCRIBER_UUID), any(ManageSubscriptionForm.class));
    verify(subscriptionService, never()).getAlreadyUnsubscribedModelAndView();
  }

  @Test
  public void getManageSubscription_userIsNotSubscribed() throws Exception {
    when(subscriptionService.verifyIsSubscribed(SUBSCRIBER_UUID.toString()))
        .thenThrow(new SubscriberNotFoundException("User is not subscribed"));
    when(subscriptionService.getAlreadyUnsubscribedModelAndView()).thenReturn(new ModelAndView(TEST_VIEW_NAME));

    mockMvc.perform(get(ReverseRouter
            .route(on(CONTROLLER).getManageSubscription(SUBSCRIBER_UUID.toString())
            )))
        .andExpect(status().isOk())
        .andExpect(view().name(TEST_VIEW_NAME));

    verify(subscriptionService).verifyIsSubscribed(SUBSCRIBER_UUID.toString());
    verify(subscriptionService).getAlreadyUnsubscribedModelAndView();
    verify(subscriptionService, never()).getManageSubscriptionModelAndView(any(), any());
  }

  @Test
  public void continueManageSubscription_userIsSubscribed_validForm() throws Exception {
    when(subscriptionService.verifyIsSubscribed(SUBSCRIBER_UUID.toString())).thenReturn(SUBSCRIBER_UUID);
    when(subscriptionService.getManagementRouting(eq(SUBSCRIBER_UUID), any(ManageSubscriptionForm.class)))
        .thenReturn(new ModelAndView(TEST_VIEW_NAME));

    var form = new ManageSubscriptionForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(subscriptionService.validateManageSubscriptionForm(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(CONTROLLER)
            .continueManageSubscription(SUBSCRIBER_UUID.toString(), null, null)))
            .param("subscriptionManagementOption", "UNSUBSCRIBE"))
    .andExpect(status().isOk())
    .andExpect(view().name(TEST_VIEW_NAME));

    verify(subscriptionService).verifyIsSubscribed(SUBSCRIBER_UUID.toString());
    verify(subscriptionService).validateManageSubscriptionForm(any(ManageSubscriptionForm.class), any(BindingResult.class));
    verify(subscriptionService).getManagementRouting(eq(SUBSCRIBER_UUID), any(ManageSubscriptionForm.class));
    verify(subscriptionService, never()).getAlreadyUnsubscribedModelAndView();
  }

  @Test
  public void continueManageSubscription_userIsSubscribed_invalidForm() throws Exception {
    when(subscriptionService.verifyIsSubscribed(SUBSCRIBER_UUID.toString())).thenReturn(SUBSCRIBER_UUID);
    when(subscriptionService.getManageSubscriptionModelAndView(eq(SUBSCRIBER_UUID), any(ManageSubscriptionForm.class)))
        .thenReturn(new ModelAndView(TEST_VIEW_NAME));

    var form = new ManageSubscriptionForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(subscriptionService.validateManageSubscriptionForm(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
            post(ReverseRouter.route(on(CONTROLLER)
                .continueManageSubscription(SUBSCRIBER_UUID.toString(), null, null)))
                .param("subscriptionManagementOption", "UNSUBSCRIBE"))
        .andExpect(status().isOk())
        .andExpect(view().name(TEST_VIEW_NAME));

    verify(subscriptionService).verifyIsSubscribed(SUBSCRIBER_UUID.toString());
    verify(subscriptionService).validateManageSubscriptionForm(any(ManageSubscriptionForm.class), any(BindingResult.class));
    verify(subscriptionService).getManageSubscriptionModelAndView(eq(SUBSCRIBER_UUID), any(ManageSubscriptionForm.class));
    verify(subscriptionService, never()).getManagementRouting(any(), any());
    verify(subscriptionService, never()).getAlreadyUnsubscribedModelAndView();
  }

  @Test
  public void continueManageSubscription_userIsNotSubscribed() throws Exception {
    when(subscriptionService.verifyIsSubscribed(SUBSCRIBER_UUID.toString()))
        .thenThrow(new SubscriberNotFoundException("User is not subscribed"));
    when(subscriptionService.getAlreadyUnsubscribedModelAndView()).thenReturn(new ModelAndView(TEST_VIEW_NAME));

    mockMvc.perform(
            post(ReverseRouter.route(on(CONTROLLER)
                .continueManageSubscription(SUBSCRIBER_UUID.toString(), null, null)))
                .param("subscriptionManagementOption", "UNSUBSCRIBE"))
        .andExpect(status().isOk())
        .andExpect(view().name(TEST_VIEW_NAME));

    verify(subscriptionService).verifyIsSubscribed(SUBSCRIBER_UUID.toString());
    verify(subscriptionService).getAlreadyUnsubscribedModelAndView();
    verify(subscriptionService, never()).validateManageSubscriptionForm(any(), any());
    verify(subscriptionService, never()).getManagementRouting(any(), any());
  }

  @Test
  public void getUpdateSubscriptionPreferences_userIsSubscribed() throws Exception {
    var form = SubscriptionTestUtil.createSubscribeForm();

    when(subscriptionService.getForm(SUBSCRIBER_UUID)).thenReturn(form);
    when(subscriptionService.verifyIsSubscribed(SUBSCRIBER_UUID.toString())).thenReturn(SUBSCRIBER_UUID);
    when(subscriptionService.getUpdateSubscriptionPreferencesModelAndView(SUBSCRIBER_UUID, form))
        .thenReturn(new ModelAndView(TEST_VIEW_NAME));

    mockMvc.perform(get(ReverseRouter
            .route(on(CONTROLLER).getUpdateSubscriptionPreferences(SUBSCRIBER_UUID.toString())
            )))
        .andExpect(status().isOk())
        .andExpect(view().name(TEST_VIEW_NAME));

    verify(subscriptionService).verifyIsSubscribed(SUBSCRIBER_UUID.toString());
    verify(subscriptionService).getForm(SUBSCRIBER_UUID);
    verify(subscriptionService).getUpdateSubscriptionPreferencesModelAndView(SUBSCRIBER_UUID, form);
    verify(subscriptionService, never()).getAlreadyUnsubscribedModelAndView();
  }

  @Test
  public void getUpdateSubscriptionPreferences_userIsNotSubscribed() throws Exception {
    when(subscriptionService.verifyIsSubscribed(SUBSCRIBER_UUID.toString()))
        .thenThrow(new SubscriberNotFoundException("User is not subscribed"));
    when(subscriptionService.getAlreadyUnsubscribedModelAndView()).thenReturn(new ModelAndView(TEST_VIEW_NAME));

    mockMvc.perform(get(ReverseRouter
            .route(on(CONTROLLER).getUpdateSubscriptionPreferences(SUBSCRIBER_UUID.toString())
            )))
        .andExpect(status().isOk())
        .andExpect(view().name(TEST_VIEW_NAME));

    verify(subscriptionService).verifyIsSubscribed(SUBSCRIBER_UUID.toString());
    verify(subscriptionService, never()).getForm(SUBSCRIBER_UUID);
    verify(subscriptionService, never()).getUpdateSubscriptionPreferencesModelAndView(any(), any());
    verify(subscriptionService).getAlreadyUnsubscribedModelAndView();
  }

  @Test
  public void saveUpdatedSubscriptionPreferences_userIsSubscribed_validForm() throws Exception {
    when(subscriptionService.verifyIsSubscribed(SUBSCRIBER_UUID.toString())).thenReturn(SUBSCRIBER_UUID);
    when(subscriptionService.getSubscriptionUpdatedConfirmationModelAndView(SUBSCRIBER_UUID))
        .thenReturn(new ModelAndView(TEST_VIEW_NAME));

    var form = new SubscribeForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(subscriptionService.validateSubscribeForm(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(post(ReverseRouter
            .route(on(CONTROLLER).saveUpdatedSubscriptionPreferences(SUBSCRIBER_UUID.toString(), null, bindingResult)
            )))
        .andExpect(status().isOk())
        .andExpect(view().name(TEST_VIEW_NAME));

    verify(subscriptionService).verifyIsSubscribed(SUBSCRIBER_UUID.toString());
    verify(subscriptionService).updateSubscriptionPreferences(any(SubscribeForm.class), eq(SUBSCRIBER_UUID));
    verify(subscriptionService, never()).getAlreadyUnsubscribedModelAndView();
  }

  @Test
  public void saveUpdatedSubscriptionPreferences_userIsSubscribed_invalidForm() throws Exception {
    when(subscriptionService.verifyIsSubscribed(SUBSCRIBER_UUID.toString())).thenReturn(SUBSCRIBER_UUID);
    when(subscriptionService.getUpdateSubscriptionPreferencesModelAndView(eq(SUBSCRIBER_UUID), any(SubscribeForm.class)))
        .thenReturn(new ModelAndView(TEST_VIEW_NAME));

    var form = new SubscribeForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(subscriptionService.validateSubscribeForm(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(post(ReverseRouter
            .route(on(CONTROLLER).saveUpdatedSubscriptionPreferences(SUBSCRIBER_UUID.toString(), null, null)
            )))
        .andExpect(status().isOk())
        .andExpect(view().name(TEST_VIEW_NAME));

    verify(subscriptionService).verifyIsSubscribed(SUBSCRIBER_UUID.toString());
    verify(subscriptionService).getUpdateSubscriptionPreferencesModelAndView(eq(SUBSCRIBER_UUID), any(SubscribeForm.class));
    verify(subscriptionService, never()).updateSubscriptionPreferences(any(), any());
    verify(subscriptionService, never()).getAlreadyUnsubscribedModelAndView();
  }

  @Test
  public void saveUpdatedSubscriptionPreferences_userIsNotSubscribed() throws Exception {
    when(subscriptionService.verifyIsSubscribed(SUBSCRIBER_UUID.toString()))
        .thenThrow(new SubscriberNotFoundException("User is not subscribed"));
    when(subscriptionService.getAlreadyUnsubscribedModelAndView()).thenReturn(new ModelAndView(TEST_VIEW_NAME));

    mockMvc.perform(post(ReverseRouter
            .route(on(CONTROLLER).saveUpdatedSubscriptionPreferences(SUBSCRIBER_UUID.toString(), null, null)
            )))
        .andExpect(status().isOk())
        .andExpect(view().name(TEST_VIEW_NAME));

    verify(subscriptionService).verifyIsSubscribed(SUBSCRIBER_UUID.toString());
    verify(subscriptionService, never()).getUpdateSubscriptionPreferencesModelAndView(any(), any());
    verify(subscriptionService, never()).updateSubscriptionPreferences(any(), any());
    verify(subscriptionService).getAlreadyUnsubscribedModelAndView();
  }
}
