package uk.co.ogauthority.pathfinder.model.enums.project;

import uk.co.ogauthority.pathfinder.util.Displayable;

public enum ProjectType implements Displayable {
  INFRASTRUCTURE("Project"),
  FORWARD_WORK_PLAN("Forward work plan");

  private final String displayName;

  ProjectType(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  public String getLowercaseDisplayName() {
    return getDisplayName().toLowerCase();
  }
}
