package uk.co.ogauthority.pathfinder.controller.subscription;

import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsEventCategory;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsService;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsUtils;
import uk.co.ogauthority.pathfinder.config.MetricsProvider;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.form.subscription.ManageSubscriptionForm;
import uk.co.ogauthority.pathfinder.model.form.subscription.SubscribeForm;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.subscription.SubscriptionService;

@Controller
public class SubscriptionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionController.class);
  public static final String SUBSCRIBE_PAGE_HEADING_PREFIX = "Subscribe to";

  private final SubscriptionService subscriptionService;
  private final ControllerHelperService controllerHelperService;
  private final MetricsProvider metricsProvider;
  private final AnalyticsService analyticsService;
  private final ServiceProperties serviceProperties;

  @Autowired
  public SubscriptionController(
      SubscriptionService subscriptionService,
      ControllerHelperService controllerHelperService,
      MetricsProvider metricsProvider,
      AnalyticsService analyticsService,
      ServiceProperties serviceProperties) {
    this.subscriptionService = subscriptionService;
    this.controllerHelperService = controllerHelperService;
    this.metricsProvider = metricsProvider;
    this.analyticsService = analyticsService;
    this.serviceProperties = serviceProperties;
  }

  @GetMapping("/subscribe")
  public ModelAndView getSubscribe() {
    metricsProvider.getSubscribePageHitCounter().increment();
    return subscriptionService.getSubscribeModelAndView(
        new SubscribeForm(),
        getSubscribePageHeading()
    );
  }

  @PostMapping("/subscribe")
  public ModelAndView subscribe(@Valid @ModelAttribute("form") SubscribeForm form,
                                BindingResult bindingResult,
                                @CookieValue(name = AnalyticsUtils.GA_CLIENT_ID_COOKIE_NAME, required = false)
                                      Optional<String> analyticsClientId) {
    metricsProvider.getSubscribePagePostCounter().increment();
    bindingResult = subscriptionService.validateSubscribeForm(form, bindingResult);
    var pageHeading = getSubscribePageHeading();
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        subscriptionService.getSubscribeModelAndView(form, pageHeading),
        form,
        () -> {
          subscriptionService.subscribe(form);
          analyticsService.sendAnalyticsEvent(analyticsClientId, AnalyticsEventCategory.NEW_SUBSCRIBER);
          // We don't redirect to a separate endpoint here as we don't want the confirmation
          // page to be publicly accessible. We considered adding errors to the email
          // address form field if already subscribed, however this would give anyone the
          // ability to tell if an email address is currently subscribed by submitting the form.
          return subscriptionService.getSubscribeConfirmationModelAndView();
        });
  }

  private String getSubscribePageHeading() {
    return String.format("%s %s", SUBSCRIBE_PAGE_HEADING_PREFIX, serviceProperties.getServiceName());
  }

  @GetMapping("/unsubscribe/{subscriberUuid}")
  public ModelAndView getUnsubscribe(@PathVariable("subscriberUuid") String subscriberUuid) {
    metricsProvider.getUnSubscribePageHitCounter().increment();
    AuditService.audit(
        AuditEvent.UNSUBSCRIBE_GET_REQUEST,
        String.format(AuditEvent.UNSUBSCRIBE_GET_REQUEST.getMessage(), subscriberUuid)
    );

    var subscriber = subscriptionService.verifyIsSubscribed(subscriberUuid);
    if (subscriber.isEmpty()) {
      LOGGER.info(String.format(
          "No subscriber found when attempting to unsubscribe subscriber with UUID %s using GET endpoint",
          subscriberUuid
      ));
      return subscriptionService.getAlreadyUnsubscribedModelAndView();
    }

    return subscriptionService.getUnsubscribeModelAndView(subscriberUuid);
  }

  @PostMapping("/unsubscribe/{subscriberUuid}")
  public ModelAndView unsubscribe(@PathVariable("subscriberUuid") String subscriberUuid,
                                  @CookieValue(name = AnalyticsUtils.GA_CLIENT_ID_COOKIE_NAME, required = false)
                                      Optional<String> analyticsClientId) {
    metricsProvider.getUnsubscribePagePostCounter().increment();
    AuditService.audit(
        AuditEvent.UNSUBSCRIBE_POST_REQUEST,
        String.format(AuditEvent.UNSUBSCRIBE_GET_REQUEST.getMessage(), subscriberUuid)
    );

    var subscriberOptional = subscriptionService.verifyIsSubscribed(subscriberUuid);
    if (subscriberOptional.isEmpty()) {
      LOGGER.info(String.format(
          "No subscriber found when attempting to unsubscribe subscriber with UUID %s using POST endpoint",
          subscriberUuid
      ));
      return subscriptionService.getAlreadyUnsubscribedModelAndView();
    }

    var subscriber = subscriberOptional.get();
    subscriptionService.unsubscribe(subscriber.getUuid());
    analyticsService.sendAnalyticsEvent(analyticsClientId, AnalyticsEventCategory.SUBSCRIBER_UNSUBSCRIBED);
    return subscriptionService.getUnsubscribeConfirmationModelAndView();

  }

  @GetMapping("/subscription/{subscriberUuid}/manage")
  public ModelAndView getManageSubscription(@PathVariable("subscriberUuid") String subscriberUuid) {
    metricsProvider.getManageSubscriptionCounter().increment();

    var subscriberOptional = subscriptionService.verifyIsSubscribed(subscriberUuid);
    if (subscriberOptional.isEmpty()) {
      LOGGER.info(String.format(
          "No subscriber found when attempting to manage subscription with UUID %s using GET endpoint",
          subscriberUuid
      ));
      return subscriptionService.getAlreadyUnsubscribedModelAndView();
    }

    var subscriber = subscriberOptional.get();
    return subscriptionService.getManageSubscriptionModelAndView(subscriber.getUuid(), new ManageSubscriptionForm());
  }

  @PostMapping("/subscription/{subscriberUuid}/manage")
  public ModelAndView continueManageSubscription(@PathVariable("subscriberUuid") String subscriberUuid,
                                                 @Valid @ModelAttribute("form") ManageSubscriptionForm form,
                                                 BindingResult bindingResult) {
    var subscriberOptional = subscriptionService.verifyIsSubscribed(subscriberUuid);
    if (subscriberOptional.isEmpty()) {
      LOGGER.info(String.format(
          "No subscriber found when attempting to continue with subscription management with UUID %s using POST endpoint",
          subscriberUuid
      ));
      return subscriptionService.getAlreadyUnsubscribedModelAndView();
    }

    var subscriber = subscriberOptional.get();
    bindingResult = subscriptionService.validateManageSubscriptionForm(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        subscriptionService.getManageSubscriptionModelAndView(subscriber.getUuid(), form),
        form,
        () -> subscriptionService.getManagementRouting(subscriber.getUuid(), form));
  }

  @GetMapping("/subscription/{subscriberUuid}/preferences")
  public ModelAndView getUpdateSubscriptionPreferences(@PathVariable("subscriberUuid") String subscriberUuid) {
    metricsProvider.getUpdateSubscriptionHitCounter().increment();

    var subscriberOptional = subscriptionService.verifyIsSubscribed(subscriberUuid);
    if (subscriberOptional.isEmpty()) {
      LOGGER.info(String.format(
          "No subscriber found when attempting to update subscription preferences with UUID %s using GET endpoint",
          subscriberUuid
      ));
      return subscriptionService.getAlreadyUnsubscribedModelAndView();
    }

    var subscriber = subscriberOptional.get();
    var form = subscriptionService.getForm(subscriber.getUuid());
    return subscriptionService.getUpdateSubscriptionPreferencesModelAndView(subscriber.getUuid(), form);
  }

  @PostMapping("/subscription/{subscriberUuid}/preferences")
  public ModelAndView saveUpdatedSubscriptionPreferences(@PathVariable("subscriberUuid") String subscriberUuid,
                                                         @Valid @ModelAttribute("form") SubscribeForm form,
                                                         BindingResult bindingResult) {
    metricsProvider.getUpdateSubscriptionPostCounter().increment();

    var subscriberOptional = subscriptionService.verifyIsSubscribed(subscriberUuid);
    if (subscriberOptional.isEmpty()) {
      LOGGER.info(String.format(
          "No subscriber found when attempting to update subscription preferences with UUID %s using POST endpoint",
          subscriberUuid
      ));
      return subscriptionService.getAlreadyUnsubscribedModelAndView();
    }

    var subscriber = subscriberOptional.get();
    bindingResult = subscriptionService.validateSubscribeForm(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        subscriptionService.getUpdateSubscriptionPreferencesModelAndView(subscriber.getUuid(), form),
        form,
        () -> {
          subscriptionService.updateSubscriptionPreferences(form, subscriber.getUuid());
          AuditService.audit(
              AuditEvent.SUBSCRIBER_DETAILS_UPDATED,
              String.format(AuditEvent.SUBSCRIBER_DETAILS_UPDATED.getMessage(), subscriberUuid)
          );
          return subscriptionService.getSubscriptionUpdatedConfirmationModelAndView(subscriber.getUuid());
        });
  }

}
