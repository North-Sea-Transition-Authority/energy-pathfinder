package uk.co.ogauthority.pathfinder.model.view.communication;

import java.util.List;

public class EmailView {

  private final String senderName;

  private final List<String> recipientList;

  private final String subject;

  private final String greetingText;

  private final String body;

  private final String signOffText;

  private final String signOffIdentifier;

  public EmailView(String senderName,
                   List<String> recipientList,
                   String subject,
                   String greetingText,
                   String body,
                   String signOffText,
                   String signOffIdentifier) {
    this.senderName = senderName;
    this.recipientList = recipientList;
    this.subject = subject;
    this.greetingText = greetingText;
    this.body = body;
    this.signOffText = signOffText;
    this.signOffIdentifier = signOffIdentifier;
  }

  public String getSenderName() {
    return senderName;
  }

  public List<String> getRecipientList() {
    return recipientList;
  }

  public String getSubject() {
    return subject;
  }

  public String getGreetingText() {
    return greetingText;
  }

  public String getBody() {
    return body;
  }

  public String getSignOffText() {
    return signOffText;
  }

  public String getSignOffIdentifier() {
    return signOffIdentifier;
  }
}
