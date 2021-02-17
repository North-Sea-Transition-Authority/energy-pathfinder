package uk.co.ogauthority.pathfinder.model.view.communication;

public class CommunicationView {

  private final String senderName;

  private final String recipientCsv;

  private final String subject;

  private final String body;

  private final String greetingText;

  private final String signOffText;

  private final String signOffIdentifier;

  public CommunicationView(String senderName,
                           String recipientCsv,
                           String subject,
                           String body,
                           String greetingText,
                           String signOffText,
                           String signOffIdentifier) {
    this.senderName = senderName;
    this.recipientCsv = recipientCsv;
    this.subject = subject;
    this.body = body;
    this.greetingText = greetingText;
    this.signOffText = signOffText;
    this.signOffIdentifier = signOffIdentifier;
  }

  public String getSenderName() {
    return senderName;
  }

  public String getRecipientCsv() {
    return recipientCsv;
  }

  public String getSubject() {
    return subject;
  }

  public String getBody() {
    return body;
  }

  public String getGreetingText() {
    return greetingText;
  }

  public String getSignOffText() {
    return signOffText;
  }

  public String getSignOffIdentifier() {
    return signOffIdentifier;
  }
}
