package uk.co.ogauthority.pathfinder.model.view;

public enum SummaryLinkText {
  EDIT("Change"),
  DELETE("Remove");

  private final String displayName;

  SummaryLinkText(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}