package uk.co.ogauthority.pathfinder.model.view;

import uk.co.ogauthority.pathfinder.util.Displayable;

public enum SummaryLinkText implements Displayable {
  EDIT("Change"),
  DELETE("Remove"),
  CONVERT_TO_AWARDED_CONTRACT("Convert to awarded contract");

  private final String displayName;

  SummaryLinkText(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }
}
