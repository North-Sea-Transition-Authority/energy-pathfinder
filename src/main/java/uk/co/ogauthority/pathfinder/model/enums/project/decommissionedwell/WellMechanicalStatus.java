package uk.co.ogauthority.pathfinder.model.enums.project.decommissionedwell;

import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

public enum WellMechanicalStatus implements SearchSelectable {
  PLANNED("Planned"),
  DRILLING("Drilling"),
  COMPLETED_OPERATING("Completed (operating)"),
  COMPLETED_SHUT_IN("Completed (shut in)"),
  PLUGGED("Plugged"),
  ABANDONED_PHASE_1("Abandonment phase 1"),
  ABANDONED_PHASE_2("Abandonment phase 2"),
  ABANDONED_PHASE_3("Abandonment phase 3");

  private final String displayName;

  WellMechanicalStatus(String displayName) {
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
