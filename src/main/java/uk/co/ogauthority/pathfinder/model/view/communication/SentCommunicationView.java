package uk.co.ogauthority.pathfinder.model.view.communication;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import uk.co.ogauthority.pathfinder.controller.communication.CommunicationSummaryController;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

public class SentCommunicationView extends CommunicationView {

  private final String submittedByUserName;

  private final String formattedDateSent;

  private final String communicationUrl;

  public SentCommunicationView(int communicationId,
                               EmailView emailView,
                               String recipientType,
                               String submittedByUserName,
                               String formattedDateSent) {
    super(communicationId, emailView, recipientType);
    this.submittedByUserName = submittedByUserName;
    this.formattedDateSent = formattedDateSent;
    this.communicationUrl = ReverseRouter.route(on(CommunicationSummaryController.class).getCommunicationSummary(
        communicationId,
        null,
        null
    ));
  }

  public String getSubmittedByUserName() {
    return submittedByUserName;
  }

  public String getFormattedDateSent() {
    return formattedDateSent;
  }

  public String getCommunicationUrl() {
    return communicationUrl;
  }
}
