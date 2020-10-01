package uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell;

import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

public enum WellOperationalStatus implements SearchSelectable {
  PLANNED("Planned"),
  CONSTRUCTING("Constructing"),
  CONSTRUCTED("Constructed"),
  SUSPENDED("Suspended"),
  DECOMMISSIONED("Decommissioned");

  private final String displayName;

  WellOperationalStatus(String displayName) {
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
