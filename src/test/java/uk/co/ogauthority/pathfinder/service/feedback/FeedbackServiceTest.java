package uk.co.ogauthority.pathfinder.service.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.model.entity.feedback.Feedback;
import uk.co.ogauthority.pathfinder.model.form.feedback.FeedbackForm;
import uk.co.ogauthority.pathfinder.repository.feedback.FeedbackRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackServiceTest {

  @Mock
  private ValidationService validationService;

  @Mock
  private FeedbackRepository feedbackRepository;

  private FeedbackService feedbackService;

  @Before
  public void setup() {
    feedbackService = new FeedbackService(
        validationService,
        feedbackRepository
    );
  }

  @Test
  public void saveFeedback_whenNoProjectDetailId_thenProjectDetailIdIsNull() {

    var form = FeedbackTestUtil.getValidFeedbackForm();
    form.setProjectDetailId(null);

    var person = UserTestingUtil.getPerson();

    ArgumentCaptor<Feedback> feedbackArgumentCaptor = ArgumentCaptor.forClass(Feedback.class);

    feedbackService.saveFeedback(form, person);

    verify(feedbackRepository, times(1)).save(feedbackArgumentCaptor.capture());

    var persistedFeedback = feedbackArgumentCaptor.getValue();

    assertExpectedEntityProperties(form, persistedFeedback, person);
    assertThat(persistedFeedback.getProjectDetailId()).isNull();
  }

  @Test
  public void saveFeedback_whenProjectDetailId_thenProjectDetailIdIsNotNull() {

    var form = FeedbackTestUtil.getValidFeedbackForm();
    form.setProjectDetailId(10);

    var person = UserTestingUtil.getPerson();

    ArgumentCaptor<Feedback> feedbackArgumentCaptor = ArgumentCaptor.forClass(Feedback.class);

    feedbackService.saveFeedback(form, person);

    verify(feedbackRepository, times(1)).save(feedbackArgumentCaptor.capture());

    var persistedFeedback = feedbackArgumentCaptor.getValue();

    assertExpectedEntityProperties(form, persistedFeedback, person);
    assertThat(persistedFeedback.getProjectDetailId()).isEqualTo(form.getProjectDetailId());
  }

  private void assertExpectedEntityProperties(FeedbackForm sourceForm, Feedback destinationEntity, Person submitter) {
    assertThat(destinationEntity.getRating()).isEqualTo(sourceForm.getServiceRating());
    assertThat(destinationEntity.getServiceFeedback()).isEqualTo(sourceForm.getFeedback());
    assertThat(destinationEntity.getSubmitterName()).isEqualTo(submitter.getFullName());
    assertThat(destinationEntity.getSubmitterEmailAddress()).isEqualTo(submitter.getEmailAddress());
    assertThat(destinationEntity.getSubmittedDate()).isNotNull();
  }

}