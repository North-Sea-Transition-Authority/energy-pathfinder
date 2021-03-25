package uk.co.ogauthority.pathfinder.model.enums.communication;

public enum CommunicationStatus {
  DRAFT("Draft"),
  SENDING("Sending"),
  SENT("Sent");

  private final String displayName;

  CommunicationStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
