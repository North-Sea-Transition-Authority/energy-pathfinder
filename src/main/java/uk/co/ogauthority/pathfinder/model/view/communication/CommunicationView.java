package uk.co.ogauthority.pathfinder.model.view.communication;

import uk.co.ogauthority.pathfinder.model.enums.communication.RecipientType;

public class CommunicationView {

  private final int communicationId;

  private final EmailView emailView;

  private final String recipientType;

  private final boolean isOperatorRecipientType;

  private final boolean isSubscriberRecipientType;

  public CommunicationView(int communicationId, EmailView emailView, String recipientType) {
    this.communicationId = communicationId;
    this.emailView = emailView;
    this.recipientType = recipientType.toUpperCase();
    this.isOperatorRecipientType = (RecipientType.OPERATORS.getDisplayName().equals(recipientType));
    this.isSubscriberRecipientType = (RecipientType.SUBSCRIBERS.getDisplayName().equals(recipientType));
  }

  public int getCommunicationId() {
    return communicationId;
  }

  public EmailView getEmailView() {
    return emailView;
  }

  public String getRecipientType() {
    return recipientType;
  }

  public boolean isOperatorRecipientType() {
    return isOperatorRecipientType;
  }

  public boolean getOperatorRecipientType() {
    return isOperatorRecipientType();
  }

  public boolean isSubscriberRecipientType() {
    return isSubscriberRecipientType;
  }

  public boolean getSubscriberRecipientType() {
    return isSubscriberRecipientType();
  }
}
