package uk.co.ogauthority.pathfinder.controller.feedback;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.feedback.FeedbackModelService;
import uk.co.ogauthority.pathfinder.feedback.FeedbackService;
import uk.co.ogauthority.pathfinder.model.form.feedback.FeedbackForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.util.flash.FlashUtils;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

  private final FeedbackModelService feedbackModelService;
  private final FeedbackService feedbackService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public FeedbackController(FeedbackModelService feedbackModelService,
                            FeedbackService feedbackService,
                            ControllerHelperService controllerHelperService) {
    this.feedbackModelService = feedbackModelService;
    this.feedbackService = feedbackService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getFeedback(@RequestParam(required = false) Optional<Integer> projectDetailId,
                                  AuthenticatedUserAccount user) {
    var form = projectDetailId
        .map(FeedbackForm::new)
        .orElse(new FeedbackForm());
    return feedbackModelService.getFeedbackModelAndView(form);
  }

  @PostMapping
  public ModelAndView submitFeedback(@Valid @ModelAttribute("form") FeedbackForm form,
                                     BindingResult bindingResult,
                                     AuthenticatedUserAccount user,
                                     RedirectAttributes redirectAttributes) {
    bindingResult = feedbackService.validateFeedbackForm(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        feedbackModelService.getFeedbackModelAndView(form),
        form,
        () -> {
          feedbackService.saveFeedback(form, user.getLinkedPerson());
          FlashUtils.success(redirectAttributes, "Your feedback has been submitted");
          return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(null, null));
        }
    );
  }
}
