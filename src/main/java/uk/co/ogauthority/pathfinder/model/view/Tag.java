package uk.co.ogauthority.pathfinder.model.view;

import uk.co.ogauthority.pathfinder.util.Displayable;

public enum Tag implements Displayable {

  NOT_FROM_LIST("NOT FROM LIST"),
  NONE("");

  private final String displayName;

  Tag(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }
}
