package uk.co.ogauthority.pathfinder.analytics;

public enum AnalyticsEventCategory {

  PROJECT_SUBMISSION("Project submitted"),

  SHOW_DIFFS_PROJECT("Show differences between versions of a project");

  private final String displayName;

  AnalyticsEventCategory(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

}