package uk.co.ogauthority.pathfinder.epsci.feedback;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.ogauthority.pathfinder.model.enums.feedback.ServiceFeedbackRating;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.util.flash.FlashUtils;

@Controller
@RequestMapping("/energy-pathfinder-feedback")
public class EpsciFeedbackController {

  private final ControllerHelperService controllerHelperService;
  private final EpsciFeedbackService epsciFeedbackService;

  @Autowired
  public EpsciFeedbackController(ControllerHelperService controllerHelperService, EpsciFeedbackService epsciFeedbackService) {
    this.controllerHelperService = controllerHelperService;
    this.epsciFeedbackService = epsciFeedbackService;
  }

  @GetMapping
  public ModelAndView getFeedback(@ModelAttribute("form") EpsciFeedbackForm form) {
    return getFeedbackModelAndView();
  }

  @PostMapping
  public ModelAndView submitFeedback(@Valid @ModelAttribute("form") EpsciFeedbackForm form,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getFeedbackModelAndView(),
        form,
        () -> {
          epsciFeedbackService.saveFeedback(form);
          FlashUtils.success(redirectAttributes, "Your feedback has been submitted");
          return ReverseRouter.redirect(on(EpsciFeedbackController.class).getFeedbackSubmitted());
        }
    );
  }

  @GetMapping("/submitted")
  public ModelAndView getFeedbackSubmitted() {
    return new ModelAndView("epsci/feedback-submitted");
  }

  private ModelAndView getFeedbackModelAndView() {
    return new ModelAndView("epsci/feedback")
      .addObject("serviceRatings", ServiceFeedbackRating.getAllAsMap());
  }
}
