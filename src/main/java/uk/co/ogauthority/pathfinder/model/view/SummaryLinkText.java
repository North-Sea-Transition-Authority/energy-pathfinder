package uk.co.ogauthority.pathfinder.model.view;

public enum SummaryLinkText {
  EDIT("Change"),
  DELETE("Delete");

  private final String displayName;

  SummaryLinkText(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
