package uk.co.ogauthority.pathfinder.model.enums.communication;

import uk.co.ogauthority.pathfinder.util.Displayable;

public enum CommunicationStatus implements Displayable {
  DRAFT("Draft"),
  SENDING("Sending"),
  SENT("Sent");

  private final String displayName;

  CommunicationStatus(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }
}
