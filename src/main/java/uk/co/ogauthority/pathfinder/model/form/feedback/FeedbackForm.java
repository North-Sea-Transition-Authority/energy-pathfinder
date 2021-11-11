package uk.co.ogauthority.pathfinder.model.form.feedback;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.feedback.ServiceFeedbackRating;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedString;

public class FeedbackForm {

  @NotNull(message = "Enter how satisfied you were when using the service", groups = FullValidation.class)
  private ServiceFeedbackRating serviceRating;

  @LengthRestrictedString(max = 2000, messagePrefix = "Your feedback", groups = FullValidation.class)
  private String feedback;

  private Integer projectDetailId;

  public FeedbackForm() {}

  public FeedbackForm(Integer projectDetailId) {
    this.projectDetailId = projectDetailId;
  }

  public ServiceFeedbackRating getServiceRating() {
    return serviceRating;
  }

  public void setServiceRating(ServiceFeedbackRating serviceRating) {
    this.serviceRating = serviceRating;
  }

  public String getFeedback() {
    return feedback;
  }

  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }

  public Integer getProjectDetailId() {
    return projectDetailId;
  }

  public void setProjectDetailId(Integer projectDetailId) {
    this.projectDetailId = projectDetailId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FeedbackForm)) {
      return false;
    }
    FeedbackForm that = (FeedbackForm) o;
    return serviceRating == that.serviceRating
        && Objects.equals(feedback, that.feedback)
        && Objects.equals(projectDetailId, that.projectDetailId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        serviceRating,
        feedback,
        projectDetailId
    );
  }
}
