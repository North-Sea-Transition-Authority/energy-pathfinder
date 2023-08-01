package uk.co.ogauthority.pathfinder.controller.subscription;

import java.util.Optional;
import java.util.UUID;
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
import uk.co.ogauthority.pathfinder.exception.SubscriberNotFoundException;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.form.subscription.ManageSubscriptionForm;
import uk.co.ogauthority.pathfinder.model.form.subscription.SubscribeForm;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.subscription.SubscriptionService;

@Controller
public class SubscriptionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionController.class);
  static final String SUBSCRIBE_PAGE_HEADING_PREFIX = "Subscribe to";

  private final SubscriptionService subscriptionService;
  private final ControllerHelperService controllerHelperService;
  private final MetricsProvider metricsProvider;
  private final AnalyticsService analyticsService;

  @Autowired
  public SubscriptionController(
      SubscriptionService subscriptionService,
      ControllerHelperService controllerHelperService,
      MetricsProvider metricsProvider,
      AnalyticsService analyticsService) {
    this.subscriptionService = subscriptionService;
    this.controllerHelperService = controllerHelperService;
    this.metricsProvider = metricsProvider;
    this.analyticsService = analyticsService;
  }

  @GetMapping("/subscribe")
  public ModelAndView getSubscribe() {
    metricsProvider.getSubscribePageHitCounter().increment();
    return subscriptionService.getSubscribeModelAndView(new SubscribeForm(), SUBSCRIBE_PAGE_HEADING_PREFIX);
  }

  @PostMapping("/subscribe")
  public ModelAndView subscribe(@Valid @ModelAttribute("form") SubscribeForm form,
                                BindingResult bindingResult,
                                @CookieValue(name = AnalyticsUtils.GA_CLIENT_ID_COOKIE_NAME, required = false)
                                      Optional<String> analyticsClientId) {
    metricsProvider.getSubscribePagePostCounter().increment();
    bindingResult = subscriptionService.validateSubscribeForm(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        subscriptionService.getSubscribeModelAndView(form, SUBSCRIBE_PAGE_HEADING_PREFIX),
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

  @GetMapping("/unsubscribe/{subscriberUuid}")
  public ModelAndView getUnsubscribe(@PathVariable("subscriberUuid") String subscriberUuid) {
    metricsProvider.getUnSubscribePageHitCounter().increment();
    AuditService.audit(
        AuditEvent.UNSUBSCRIBE_GET_REQUEST,
        String.format(AuditEvent.UNSUBSCRIBE_GET_REQUEST.getMessage(), subscriberUuid)
    );
    try {
      subscriptionService.verifyIsSubscribed(subscriberUuid);
      return subscriptionService.getUnsubscribeModelAndView(subscriberUuid);
    } catch (SubscriberNotFoundException e) {
      LOGGER.info(
          String.format(
              "No subscriber found when attempting to unsubscribe subscriber with UUID %s using GET endpoint",
              subscriberUuid
          )
      );
      return subscriptionService.getAlreadyUnsubscribedModelAndView();
    }
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
    try {
      var uuid = subscriptionService.verifyIsSubscribed(subscriberUuid);
      subscriptionService.unsubscribe(uuid);
      analyticsService.sendAnalyticsEvent(analyticsClientId, AnalyticsEventCategory.SUBSCRIBER_UNSUBSCRIBED);
      return subscriptionService.getUnsubscribeConfirmationModelAndView();
    } catch (SubscriberNotFoundException e) {
      LOGGER.info(
          String.format(
              "No subscriber found when attempting to unsubscribe subscriber with UUID %s using POST endpoint",
              subscriberUuid
          )
      );
      return subscriptionService.getAlreadyUnsubscribedModelAndView();
    }
  }

  @GetMapping("/subscription/manage/{subscriberUuid}")
  public ModelAndView getManageSubscription(@PathVariable("subscriberUuid") String subscriberUuid) {
    UUID uuid;
    try {
      uuid = subscriptionService.verifyIsSubscribed(subscriberUuid);
    } catch (SubscriberNotFoundException e) {
      LOGGER.info(
          String.format(
              "No subscriber found when attempting to manage subscription with UUID %s using GET endpoint",
              subscriberUuid
          )
      );
      return subscriptionService.getAlreadyUnsubscribedModelAndView();
    }

    return subscriptionService.getManageSubscriptionModelAndView(uuid, new ManageSubscriptionForm());
  }

  @PostMapping("/subscription/manage/{subscriberUuid}")
  public ModelAndView continueManageSubscription(@PathVariable("subscriberUuid") String subscriberUuid,
                                                 @Valid @ModelAttribute("form") ManageSubscriptionForm form,
                                                 BindingResult bindingResult) {
    UUID uuid;
    try {
      uuid = subscriptionService.verifyIsSubscribed(subscriberUuid);
    } catch (SubscriberNotFoundException e) {
      LOGGER.info(
          String.format(
              "No subscriber found when attempting to continue with subscription management with UUID %s using POST endpoint",
              subscriberUuid
          )
      );
      return subscriptionService.getAlreadyUnsubscribedModelAndView();
    }
    bindingResult = subscriptionService.validateManageSubscriptionForm(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        subscriptionService.getManageSubscriptionModelAndView(uuid, form),
        form,
        () -> subscriptionService.getManagementRouting(uuid, form));
  }

  @GetMapping("/subscription/update-preferences/{subscriberUuid}")
  public ModelAndView getUpdateSubscriptionPreferences(@PathVariable("subscriberUuid") String subscriberUuid) {
    UUID uuid;
    try {
      uuid = subscriptionService.verifyIsSubscribed(subscriberUuid);
    } catch (SubscriberNotFoundException e) {
      LOGGER.info(
          String.format(
              "No subscriber found when attempting to update subscription preferences with UUID %s using GET endpoint",
              subscriberUuid
          )
      );
      return subscriptionService.getAlreadyUnsubscribedModelAndView();
    }

    var form = subscriptionService.getForm(uuid);
    return subscriptionService.getUpdateSubscriptionPreferencesModelAndView(uuid, form);
  }

  @PostMapping("/subscription/update-preferences/{subscriberUuid}")
  public ModelAndView saveUpdatedSubscriptionPreferences(@PathVariable("subscriberUuid") String subscriberUuid,
                                                         @Valid @ModelAttribute("form") SubscribeForm form,
                                                         BindingResult bindingResult) {
    UUID uuid;
    try {
      uuid = subscriptionService.verifyIsSubscribed(subscriberUuid);
    } catch (SubscriberNotFoundException e) {
      LOGGER.info(
          String.format(
              "No subscriber found when attempting to update subscription preferences with UUID %s using POST endpoint",
              subscriberUuid
          )
      );
      return subscriptionService.getAlreadyUnsubscribedModelAndView();
    }

    bindingResult = subscriptionService.validateSubscribeForm(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        subscriptionService.getUpdateSubscriptionPreferencesModelAndView(uuid, form),
        form,
        () -> {
          subscriptionService.updateSubscriptionPreferences(form, uuid);
          AuditService.audit(
              AuditEvent.SUBSCRIBER_DETAILS_UPDATED,
              String.format(AuditEvent.SUBSCRIBER_DETAILS_UPDATED.getMessage(), subscriberUuid)
          );
          return subscriptionService.getSubscriptionUpdatedConfirmationModelAndView(uuid);
        });
  }

}
