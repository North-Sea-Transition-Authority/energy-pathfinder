package uk.co.ogauthority.pathfinder.epsci.feedback;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.feedback.ServiceFeedbackRating;


@Valid public record EpsciFeedbackForm(
  @NotNull(message = "You must choose a rating") ServiceFeedbackRating serviceRating,
    String feedback,
    String epsciPath) {

  @Override
  public String toString() {
    return "EpsciFeedbackForm{" +
      "serviceRating=" + serviceRating +
      ", feedback='" + feedback + '\'' +
      ", epsciPath='" + epsciPath + '\'' +
      '}';
  }
}
