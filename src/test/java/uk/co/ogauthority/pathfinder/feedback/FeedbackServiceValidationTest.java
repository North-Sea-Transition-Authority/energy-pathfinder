package uk.co.ogauthority.pathfinder.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.time.Clock;
import java.util.Set;
import javax.validation.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.fivium.feedbackmanagementservice.client.FeedbackClientService;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackServiceValidationTest {

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private FeedbackClientService feedbackClientService;

  @Mock
  private FeedbackEmailService feedbackEmailService;

  @Mock
  private LinkService linkService;

  @Mock
  Clock clock;

  private FeedbackService feedbackService;

  @Before
  public void setup() {
    var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    var validationService = new ValidationService(validator);

    feedbackService = new FeedbackService(validationService, feedbackClientService, projectDetailsRepository,
        projectInformationService, feedbackEmailService, linkService, clock, "PATHFINDER");
  }

  @Test
  public void validateFeedbackForm_whenNoServiceRating_thenValidationErrorExpected() {

    var form = FeedbackTestUtil.getValidFeedbackFormWithProjectDetailId();
    form.setServiceRating(null);

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var errors = feedbackService.validateFeedbackForm(form, bindingResult);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).containsExactly(
        entry("serviceRating", Set.of("NotNull"))
    );

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("serviceRating", Set.of("Enter how satisfied you were when using the service"))
    );
  }

  @Test
  public void validateFeedbackForm_whenNoFeedback_thenNoValidationErrorExpected() {

    var form = FeedbackTestUtil.getValidFeedbackFormWithProjectDetailId();
    form.setFeedback(null);

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var errors = feedbackService.validateFeedbackForm(form, bindingResult);

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  public void validateFeedbackForm_whenFeedbackExceedsCharacterLimit_thenValidationErrorExpected() {

    var feedbackOverLimit = ValidatorTestingUtil.getStringOfLength(
        FeedbackModelService.FEEDBACK_CHARACTER_LIMIT + 1
    );

    var form = FeedbackTestUtil.getValidFeedbackFormWithProjectDetailId();
    form.setFeedback(feedbackOverLimit);

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var errors = feedbackService.validateFeedbackForm(form, bindingResult);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).containsExactly(
        entry("feedback", Set.of("LengthRestrictedString"))
    );
  }

  @Test
  public void validateFeedbackForm_whenValidFeedbackForm_thenNoValidationErrorExpected() {
    var form = FeedbackTestUtil.getValidFeedbackFormWithProjectDetailId();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var errors = feedbackService.validateFeedbackForm(form, bindingResult);

    assertThat(errors.hasErrors()).isFalse();
  }
}
