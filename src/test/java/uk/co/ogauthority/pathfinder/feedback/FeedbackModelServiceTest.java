package uk.co.ogauthority.pathfinder.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.feedback.ServiceFeedbackRating;
import uk.co.ogauthority.pathfinder.model.form.feedback.FeedbackForm;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackModelServiceTest {

  private FeedbackModelService feedbackModelService;

  @Before
  public void setup() {
    feedbackModelService = new FeedbackModelService();
  }

  @Test
  public void getFeedbackModelAndView_assertModelProperties() {

    var form = new FeedbackForm();

    var resultingModel = feedbackModelService.getFeedbackModelAndView(form);

    assertThat(resultingModel.getViewName()).isEqualTo(FeedbackModelService.TEMPLATE_PATH);
    assertThat(resultingModel.getModelMap()).containsExactly(
        entry("form", form),
        entry("serviceRatings", ServiceFeedbackRating.getAllAsMap()),
        entry("feedbackCharacterLimit", String.valueOf(FeedbackModelService.FEEDBACK_CHARACTER_LIMIT)),
        entry("cancelUrl", ControllerUtils.getWorkAreaUrl()),
        entry("showBackLink", true)
    );
  }

  @Test
  public void getFeedbackModelAndView_assertModelProperties_projectDetailIdNotNull() {

    var form = new FeedbackForm();
    form.setProjectDetailId(1);

    var resultingModel = feedbackModelService.getFeedbackModelAndView(form);

    assertThat(resultingModel.getViewName()).isEqualTo(FeedbackModelService.TEMPLATE_PATH);
    assertThat(resultingModel.getModelMap()).containsExactly(
        entry("form", form),
        entry("serviceRatings", ServiceFeedbackRating.getAllAsMap()),
        entry("feedbackCharacterLimit", String.valueOf(FeedbackModelService.FEEDBACK_CHARACTER_LIMIT)),
        entry("cancelUrl", ControllerUtils.getWorkAreaUrl()),
        entry("showBackLink", false)
    );
  }

}