package uk.co.ogauthority.pathfinder.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.feedback.FeedbackFailedToSendEmailProperties;
import uk.co.ogauthority.pathfinder.service.email.EmailService;

@Service
class FeedbackEmailService {

  private final EmailService emailService;

  @Autowired
  public FeedbackEmailService(EmailService emailService) {
    this.emailService = emailService;
  }

  void sendFeedbackFailedToSendEmail(String feedbackContent, String emailAddress, String recipientName) {
    var emailProperties = new FeedbackFailedToSendEmailProperties(feedbackContent, recipientName);
    emailService.sendEmail(emailProperties, emailAddress);
  }

}