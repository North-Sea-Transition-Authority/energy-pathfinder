package uk.co.ogauthority.pathfinder.service.feedback;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.model.entity.feedback.Feedback;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.feedback.FeedbackForm;
import uk.co.ogauthority.pathfinder.repository.feedback.FeedbackRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class FeedbackService {

  private final ValidationService validationService;
  private final FeedbackRepository feedbackRepository;

  @Autowired
  public FeedbackService(ValidationService validationService,
                         FeedbackRepository feedbackRepository) {
    this.validationService = validationService;
    this.feedbackRepository = feedbackRepository;
  }

  public BindingResult validateFeedbackForm(FeedbackForm form,
                                            BindingResult bindingResult) {
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  @Transactional
  public void saveFeedback(FeedbackForm feedbackForm, Person submittingPerson) {
    var feedbackEntity = new Feedback();
    feedbackEntity.setRating(feedbackForm.getServiceRating());
    feedbackEntity.setServiceFeedback(feedbackForm.getFeedback());
    feedbackEntity.setSubmitterName(submittingPerson.getFullName());
    feedbackEntity.setSubmitterEmailAddress(submittingPerson.getEmailAddress());
    feedbackEntity.setSubmittedDate(Instant.now());
    feedbackEntity.setProjectDetailId(feedbackForm.getProjectDetailId());
    feedbackRepository.save(feedbackEntity);
  }
}
