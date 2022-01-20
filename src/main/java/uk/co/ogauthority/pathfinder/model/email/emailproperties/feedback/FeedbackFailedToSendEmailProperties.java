package uk.co.ogauthority.pathfinder.model.email.emailproperties.feedback;

import java.util.Map;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class FeedbackFailedToSendEmailProperties extends EmailProperties {

  private final String feedbackContent;

  public FeedbackFailedToSendEmailProperties(String feedbackContent, String recipientName) {
    super(NotifyTemplate.FEEDBACK_FAILED_TO_SEND, recipientName);
    this.feedbackContent = feedbackContent;
  }

  @Override
  public Map<String, Object> getEmailPersonalisation() {
    var emailPersonalisation = super.getEmailPersonalisation();
    emailPersonalisation.put("FEEDBACK_CONTENT", feedbackContent);
    return emailPersonalisation;
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }
    if (!(o instanceof FeedbackFailedToSendEmailProperties)) {
      return false;
    }
    FeedbackFailedToSendEmailProperties emailProperties = (FeedbackFailedToSendEmailProperties) o;
    return this.feedbackContent.equals(emailProperties.feedbackContent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), feedbackContent);
  }

}