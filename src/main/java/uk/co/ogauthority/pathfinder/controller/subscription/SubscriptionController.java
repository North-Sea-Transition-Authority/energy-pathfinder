package uk.co.ogauthority.pathfinder.controller.subscription;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.model.form.subscription.SubscribeForm;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.subscription.SubscriptionService;

@Controller
public class SubscriptionController {

  private final SubscriptionService subscriptionService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public SubscriptionController(
      SubscriptionService subscriptionService,
      ControllerHelperService controllerHelperService) {
    this.subscriptionService = subscriptionService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping("/subscribe")
  public ModelAndView getSubscribe() {
    return subscriptionService.getSubscribeModelAndView(new SubscribeForm());
  }

  @PostMapping("/subscribe")
  public ModelAndView subscribe(@Valid @ModelAttribute("form") SubscribeForm form,
                                BindingResult bindingResult) {
    bindingResult = subscriptionService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        subscriptionService.getSubscribeModelAndView(form),
        form,
        () -> {
          subscriptionService.subscribe(form);
          // We don't redirect to a separate endpoint here as we don't want the confirmation
          // page to be publicly accessible. We considered adding errors to the the email
          // address form field if already subscribed, however this would give anyone the
          // ability to tell if an email address is currently subscribed by submitting the form.
          return subscriptionService.getSubscribeConfirmationModelAndView();
        });
  }

  @GetMapping("/unsubscribe/{subscriberUuid}")
  public ModelAndView getUnsubscribe(@PathVariable("subscriberUuid") String subscriberUuid) {
    subscriptionService.verifyIsSubscribed(subscriberUuid);
    return subscriptionService.getUnsubscribeModelAndView();
  }

  @PostMapping("/unsubscribe/{subscriberUuid}")
  public ModelAndView unsubscribe(@PathVariable("subscriberUuid") String subscriberUuid) {
    var uuid = subscriptionService.verifyIsSubscribed(subscriberUuid);
    subscriptionService.unsubscribe(uuid);
    return subscriptionService.getUnsubscribeConfirmationModelAndView();
  }
}
