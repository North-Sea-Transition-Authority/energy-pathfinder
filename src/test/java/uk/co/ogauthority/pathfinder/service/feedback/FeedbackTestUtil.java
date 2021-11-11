package uk.co.ogauthority.pathfinder.service.feedback;

import uk.co.ogauthority.pathfinder.model.enums.feedback.ServiceFeedbackRating;
import uk.co.ogauthority.pathfinder.model.form.feedback.FeedbackForm;

class FeedbackTestUtil {

  private FeedbackTestUtil() {
    throw new IllegalStateException("FeedbackTestUtil is a utility class and should not be instantiated");
  }

  static FeedbackForm getValidFeedbackForm() {
    var form = new FeedbackForm();
    form.setServiceRating(ServiceFeedbackRating.VERY_SATISFIED);
    form.setFeedback("feedback");
    form.setProjectDetailId(1);
    return form;
  }

}
