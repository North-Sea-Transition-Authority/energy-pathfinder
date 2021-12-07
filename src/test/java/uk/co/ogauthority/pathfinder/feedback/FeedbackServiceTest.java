package uk.co.ogauthority.pathfinder.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.ogauthority.pathfinder.feedback.FeedbackService.formatter;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.fivium.feedbackmanagementservice.client.CannotSendFeedbackException;
import uk.co.fivium.feedbackmanagementservice.client.FeedbackClientService;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail;
import uk.co.ogauthority.pathfinder.model.form.feedback.FeedbackForm;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackServiceTest {

  private static final Instant DATETIME = Instant.parse("2020-04-29T10:15:30Z");
  private static final String SERVICE_NAME = "PATHFINDER";
  private static final Integer PROJECT_DETAIL_ID = 2;
  private static final Integer PROJECT_ID = 1;
  private static final String TITLE = "testTitle";
  private static final String SUPPORT_EMAIL = ServiceContactDetail.TECHNICAL_SUPPORT.getEmailAddress();
  private static final String RECIPIENT_NAME = ServiceContactDetail.TECHNICAL_SUPPORT.getServiceName();

  @Mock
  private ValidationService validationService;

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private FeedbackClientService feedbackClientService;

  @Mock
  private FeedbackEmailService feedbackEmailService;

  private Person person;
  private FeedbackService feedbackService;

  @Before
  public void setup() {

    Clock fixedClock = Clock.fixed(DATETIME, ZoneId.of("UTC"));

    feedbackService = new FeedbackService(
        validationService,
        feedbackClientService,
        projectDetailsRepository,
        projectInformationService,
        feedbackEmailService,
        fixedClock,
        SERVICE_NAME);

    person = UserTestingUtil.getPerson();

    ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
    when(projectDetailsRepository.findById(PROJECT_DETAIL_ID)).thenReturn(Optional.of(projectDetail));
    when(projectInformationService.getProjectTitle(projectDetail)).thenReturn(TITLE);

  }

  @Test
  public void saveFeedback_whenNoProjectDetailId_thenTransactionIdAndTransactionReferenceNull() throws CannotSendFeedbackException {
    var form = FeedbackTestUtil.getValidFeedbackFormWithProjectDetailId();
    form.setProjectDetailId(null);

    ArgumentCaptor<Feedback> feedbackArgumentCaptor = ArgumentCaptor.forClass(Feedback.class);

    feedbackService.saveFeedback(form, person);

    verify(feedbackClientService, times(1)).saveFeedback(feedbackArgumentCaptor.capture());

    var savedFeedback = feedbackArgumentCaptor.getValue();

    assertExpectedEntityProperties(form, savedFeedback, person);
    assertThat(savedFeedback.getTransactionId()).isNull();
    assertThat(savedFeedback.getTransactionReference()).isNull();
  }

  @Test
  public void saveFeedback_whenProjectDetailId_thenTransactionIdAndTransactionReferenceNotNull() throws CannotSendFeedbackException {
    var form = FeedbackTestUtil.getValidFeedbackFormWithProjectDetailId();
    form.setProjectDetailId(PROJECT_DETAIL_ID);

    ArgumentCaptor<Feedback> feedbackArgumentCaptor = ArgumentCaptor.forClass(Feedback.class);

    feedbackService.saveFeedback(form, person);

    verify(feedbackClientService, times(1)).saveFeedback(feedbackArgumentCaptor.capture());

    var savedFeedback = feedbackArgumentCaptor.getValue();

    assertExpectedEntityProperties(form, savedFeedback, person);
    //TransactionId is the project id we get from ProjectUtil not the projectDetailId
    assertThat(savedFeedback.getTransactionId()).isEqualTo(1);
    assertThat(savedFeedback.getTransactionReference()).isEqualTo(TITLE);
  }

  @Test(expected = EntityNotFoundException.class)
  public void saveFeedback_projectDetailIdNotFound_throwEntityNotFoundException() {
    var form = FeedbackTestUtil.getValidFeedbackFormWithProjectDetailId();
    form.setProjectDetailId(PROJECT_DETAIL_ID);

    when(projectDetailsRepository.findById(PROJECT_DETAIL_ID)).thenReturn(Optional.empty());
    feedbackService.saveFeedback(form, person);
  }

  @Test
  public void saveFeedback_feedbackManagementServiceDownAndTransactionId_emailSent() throws CannotSendFeedbackException {
    var form = FeedbackTestUtil.getValidFeedbackFormWithProjectDetailId();
    form.setProjectDetailId(PROJECT_DETAIL_ID);

    Exception e = new Exception("testException");
    when(feedbackClientService.saveFeedback(any(Feedback.class))).thenThrow(
        new CannotSendFeedbackException("Cannot send feedback", e));

    ArgumentCaptor<String> feedbackContentCaptor = ArgumentCaptor.forClass(String.class);
    feedbackService.saveFeedback(form, person);
    verify(feedbackEmailService, times(1)).sendFeedbackFailedToSendEmail(feedbackContentCaptor.capture(),
        eq(SUPPORT_EMAIL), eq(RECIPIENT_NAME));

    assertThat(feedbackContentCaptor.getValue()).isEqualTo(
        "Submitter name: " + person.getFullName() +
            "\nSubmitter email: " + person.getEmailAddress() +
            "\nService rating: " + form.getServiceRating().name() +
            "\nService improvement: " + form.getFeedback() +
            "\nDate and time: " + formatter.format(DATETIME)+
            "\nService name: " + SERVICE_NAME +
            "\nTransaction ID: " + PROJECT_ID +
            "\nTransaction reference: " + TITLE
    );
  }

  @Test
  public void saveFeedback_feedbackManagementServiceDownAndNoTransactionId_emailSent() throws CannotSendFeedbackException {
    var form = FeedbackTestUtil.getValidFeedbackFormWithoutProjectDetailId();

    Exception e = new Exception("testException");
    when(feedbackClientService.saveFeedback(any(Feedback.class))).thenThrow(
        new CannotSendFeedbackException("Cannot send feedback", e));

    ArgumentCaptor<String> feedbackContentCaptor = ArgumentCaptor.forClass(String.class);
    feedbackService.saveFeedback(form, person);
    verify(feedbackEmailService, times(1)).sendFeedbackFailedToSendEmail(feedbackContentCaptor.capture(),
        eq(SUPPORT_EMAIL), eq(RECIPIENT_NAME));

    assertThat(feedbackContentCaptor.getValue()).isEqualTo(
        "Submitter name: " + person.getFullName() +
            "\nSubmitter email: " + person.getEmailAddress() +
            "\nService rating: " + form.getServiceRating().name() +
            "\nService improvement: " + form.getFeedback() +
            "\nDate and time: " + formatter.format(DATETIME)+
            "\nService name: " + SERVICE_NAME
    );
  }

  private void assertExpectedEntityProperties(FeedbackForm sourceForm, Feedback destinationEntity, Person submitter) {
    assertThat(destinationEntity.getServiceRating()).isEqualTo(sourceForm.getServiceRating().name());
    assertThat(destinationEntity.getComment()).isEqualTo(sourceForm.getFeedback());
    assertThat(destinationEntity.getSubmitterName()).isEqualTo(submitter.getFullName());
    assertThat(destinationEntity.getSubmitterEmail()).isEqualTo(submitter.getEmailAddress());
    assertThat(destinationEntity.getGivenDatetime()).isEqualTo(DATETIME);
  }

}