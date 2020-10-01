package uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell;

import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

public enum DecommissionedWellType implements SearchSelectable {
  EXPLORATION_AND_APPRAISAL("Exploration & appraisal"),
  OPEN_WATER("Open-water"),
  PLATFORM("Platform"),
  SUBSEA("Subsea"),
  SUSPENDED("Suspended");

  private final String displayName;

  DecommissionedWellType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String getSelectionId() {
    return name();
  }

  @Override
  public String getSelectionText() {
    return getDisplayName();
  }
}
