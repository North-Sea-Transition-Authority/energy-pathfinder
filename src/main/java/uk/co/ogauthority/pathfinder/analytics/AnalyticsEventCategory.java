package uk.co.ogauthority.pathfinder.analytics;

public enum AnalyticsEventCategory {

  SAVE_PROJECT_FORM("Save project form"),
  SAVE_PROJECT_FORM_COMPLETE_LATER("Save project form and complete later"),

  PROJECT_SUBMISSION("Project submitted"),

  NEW_SUBSCRIBER("New subscriber"),
  SUBSCRIBER_UNSUBSCRIBED("Subscriber unsubscribed"),

  SHOW_DIFFS_PROJECT("Show differences between versions of a project");

  private final String displayName;

  AnalyticsEventCategory(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

}