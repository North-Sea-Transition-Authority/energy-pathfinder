package uk.co.ogauthority.pathfinder.model.view.communication;

public class SentCommunicationView extends CommunicationView {

  private final String submittedByUserName;

  private final String submittedByEmailAddress;

  private final String formattedDateSent;

  public SentCommunicationView(String senderName,
                               String recipientCsv,
                               String subject,
                               String body,
                               String greetingText,
                               String signOffText,
                               String signOffIdentifier,
                               String submittedByUserName,
                               String submittedByEmailAddress,
                               String formattedDateSent) {
    super(
        senderName,
        recipientCsv,
        subject,
        body,
        greetingText,
        signOffText,
        signOffIdentifier
    );
    this.submittedByUserName = submittedByUserName;
    this.submittedByEmailAddress = submittedByEmailAddress;
    this.formattedDateSent = formattedDateSent;
  }

  public String getSubmittedByUserName() {
    return submittedByUserName;
  }

  public String getSubmittedByEmailAddress() {
    return submittedByEmailAddress;
  }

  public String getFormattedDateSent() {
    return formattedDateSent;
  }
}
