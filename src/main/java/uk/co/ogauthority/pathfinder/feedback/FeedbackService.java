package uk.co.ogauthority.pathfinder.feedback;

import jakarta.persistence.EntityNotFoundException;
import java.time.Clock;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.fivium.feedbackmanagementservice.client.CannotSendFeedbackException;
import uk.co.fivium.feedbackmanagementservice.client.FeedbackClientService;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.contact.ServiceContactDetail;
import uk.co.ogauthority.pathfinder.model.form.feedback.FeedbackForm;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class FeedbackService {

  public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
      .withZone((ZoneId.systemDefault()));

  private final ValidationService validationService;
  private final FeedbackClientService feedbackClientService;
  private final ProjectDetailsRepository projectDetailsRepository;
  private final ProjectInformationService projectInformationService;
  private final FeedbackEmailService feedbackEmailService;
  private final LinkService linkService;
  private final String serviceName;
  private final Clock utcClock;

  private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackService.class);

  @Autowired
  public FeedbackService(ValidationService validationService,
                         FeedbackClientService feedbackClientService,
                         ProjectDetailsRepository projectDetailsRepository,
                         ProjectInformationService projectInformationService,
                         FeedbackEmailService feedbackEmailService,
                         LinkService linkService,
                         @Qualifier("utcClock") Clock utcClock,
                         @Value("${fms.service.name}") String serviceName) {
    this.validationService = validationService;
    this.feedbackClientService = feedbackClientService;
    this.projectDetailsRepository = projectDetailsRepository;
    this.projectInformationService = projectInformationService;
    this.feedbackEmailService = feedbackEmailService;
    this.linkService = linkService;
    this.serviceName = serviceName;
    this.utcClock = utcClock;
  }

  public BindingResult validateFeedbackForm(FeedbackForm form,
                                            BindingResult bindingResult) {
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  public void saveFeedback(FeedbackForm feedbackForm, Person submittingPerson) {
    var feedback = new Feedback();

    feedback.setServiceRating(feedbackForm.getServiceRating().name());
    feedback.setComment(feedbackForm.getFeedback());
    feedback.setSubmitterName(submittingPerson.getFullName());
    feedback.setSubmitterEmail(submittingPerson.getEmailAddress());
    feedback.setGivenDatetime(utcClock.instant());

    var projectDetailId = feedbackForm.getProjectDetailId();
    if (projectDetailId != null) {
      var projectDetail = projectDetailsRepository.findById(projectDetailId)
          .orElseThrow(() -> new EntityNotFoundException("Unable to find project detail with id:" + projectDetailId));

      var project = projectDetail.getProject();

      feedback.setTransactionId(project.getId());
      feedback.setTransactionReference(projectInformationService.getProjectTitle(projectDetail));
      feedback.setTransactionLink(linkService.generateProjectManagementUrl(project));
    }

    try {
      feedbackClientService.saveFeedback(feedback);
    } catch (CannotSendFeedbackException e) {
      feedbackEmailService.sendFeedbackFailedToSendEmail(getFeedbackContent(feedback),
          ServiceContactDetail.TECHNICAL_SUPPORT.getEmailAddress(),
          ServiceContactDetail.TECHNICAL_SUPPORT.getServiceName());
      LOGGER.warn(String.format("Feedback failed to send: %s", e.getMessage()));
    }
  }

  private String getFeedbackContent(Feedback feedback) {
    var feedbackContent = "Submitter name: " + feedback.getSubmitterName() +
        "\nSubmitter email: " + feedback.getSubmitterEmail() +
        "\nService rating: " + feedback.getServiceRating();

    if (feedback.getComment() != null) {
      feedbackContent += "\nService improvement: " + feedback.getComment();
    }

    feedbackContent += "\nDate and time: " + formatter.format(feedback.getGivenDatetime()) +
        "\nService name: " + serviceName;

    if (feedback.getTransactionId() != null) {
      feedbackContent += "\nTransaction ID: " + feedback.getTransactionId() +
          "\nTransaction reference: " + feedback.getTransactionReference() +
          "\nTransaction link: " + feedback.getTransactionLink();
    }
    return feedbackContent;
  }
}
