package uk.co.ogauthority.pathfinder.controller.subscription;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.config.MetricsProvider;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.model.form.subscription.SubscribeForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.subscription.SubscriptionService;
import uk.co.ogauthority.pathfinder.testutil.MetricsProviderTestUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(SubscriptionController.class)
public class SubscriptionControllerTest extends AbstractControllerTest {

  private static final UUID SUBSCRIBER_UUID = UUID.randomUUID();

  @MockBean
  private SubscriptionService subscriptionService;

  @MockBean
  private MetricsProvider metricsProvider;

  @Before
  public void setUp() throws Exception {
    when(metricsProvider.getSubscribePageHitCounter()).thenReturn(MetricsProviderTestUtil.getNoOpCounter());
    when(metricsProvider.getUnSubscribePageHitCounter()).thenReturn(MetricsProviderTestUtil.getNoOpCounter());
  }

  @Test
  public void getSubscribe() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(SubscriptionController.class).getSubscribe())))
        .andExpect(status().isOk());
  }

  @Test
  public void subscribe_whenValidForm_thenSubscribe() throws Exception {
    var form = new SubscribeForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(subscriptionService.validate(any(), any())).thenReturn(bindingResult);
    when(subscriptionService.getSubscribeConfirmationModelAndView()).thenReturn(new ModelAndView("test"));

    mockMvc.perform(
        post(ReverseRouter.route(on(SubscriptionController.class)
            .subscribe(null, null)
        )))
        .andExpect(status().isOk());

    verify(subscriptionService, times(1)).validate(any(), any());
    verify(subscriptionService, times(1)).subscribe(any());
  }

  @Test
  public void subscribe_whenInvalidForm_thenNoSubscribe() throws Exception {
    var form = new SubscribeForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(subscriptionService.validate(any(), any())).thenReturn(bindingResult);
    when(subscriptionService.getSubscribeModelAndView(any())).thenReturn(new ModelAndView("test"));

    mockMvc.perform(
        post(ReverseRouter.route(on(SubscriptionController.class)
            .subscribe(null, null)
        )))
        .andExpect(status().isOk());

    verify(subscriptionService, times(1)).validate(any(), any());
    verify(subscriptionService, never()).subscribe(any());
  }

  @Test
  public void getUnsubscribe() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(SubscriptionController.class).getUnsubscribe(SUBSCRIBER_UUID.toString()))))
        .andExpect(status().isOk());
  }

  @Test
  public void unsubscribe() throws Exception {
    when(subscriptionService.verifyIsSubscribed(SUBSCRIBER_UUID.toString())).thenReturn(SUBSCRIBER_UUID);
    when(subscriptionService.getUnsubscribeConfirmationModelAndView()).thenReturn(new ModelAndView("test"));

    mockMvc.perform(
        post(ReverseRouter.route(on(SubscriptionController.class)
            .unsubscribe(SUBSCRIBER_UUID.toString())
        )))
        .andExpect(status().isOk());

    verify(subscriptionService, times(1)).verifyIsSubscribed(SUBSCRIBER_UUID.toString());
    verify(subscriptionService, times(1)).unsubscribe(SUBSCRIBER_UUID);
  }
}
