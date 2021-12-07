package uk.co.ogauthority.pathfinder.feedback;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.model.enums.feedback.ServiceFeedbackRating;
import uk.co.ogauthority.pathfinder.model.form.feedback.FeedbackForm;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Service
public class FeedbackModelService {

  static final String TEMPLATE_PATH = "feedback/feedback";

  static final Integer FEEDBACK_CHARACTER_LIMIT = 2000;

  public ModelAndView getFeedbackModelAndView(FeedbackForm feedbackForm) {
    return new ModelAndView(TEMPLATE_PATH)
        .addObject("form", feedbackForm)
        .addObject("serviceRatings", ServiceFeedbackRating.getAllAsMap())
        .addObject("feedbackCharacterLimit", String.valueOf(FEEDBACK_CHARACTER_LIMIT))
        .addObject("cancelUrl", ControllerUtils.getWorkAreaUrl())
        // Only show the back link if we aren't at the end of the submission process.
        // Users can't go back to the submission confirmation page currently.
        .addObject("showBackLink", feedbackForm.getProjectDetailId() == null);
  }
}
