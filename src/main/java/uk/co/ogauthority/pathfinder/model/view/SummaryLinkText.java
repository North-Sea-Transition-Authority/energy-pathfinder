package uk.co.ogauthority.pathfinder.model.view;

public enum SummaryLinkText {
  EDIT("Change"),
  DELETE("Remove"),
  CONVERT_TO_AWARDED_CONTRACT("Convert to awarded contract");

  private final String displayName;

  SummaryLinkText(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
