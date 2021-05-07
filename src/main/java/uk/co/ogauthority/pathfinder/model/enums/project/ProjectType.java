package uk.co.ogauthority.pathfinder.model.enums.project;

public enum ProjectType {
  INFRASTRUCTURE("Project"),
  FORWARD_WORK_PLAN("Forward work plan");

  private final String displayName;

  ProjectType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getLowercaseDisplayName() {
    return getDisplayName().toLowerCase();
  }
}
