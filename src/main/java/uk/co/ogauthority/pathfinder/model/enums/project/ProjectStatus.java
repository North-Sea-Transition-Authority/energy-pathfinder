package uk.co.ogauthority.pathfinder.model.enums.project;

public enum ProjectStatus {
  DRAFT("Draft"),
  QA("QA"),
  PUBLISHED("Published");

  private final String displayName;

  ProjectStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
