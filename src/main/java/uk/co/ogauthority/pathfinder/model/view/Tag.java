package uk.co.ogauthority.pathfinder.model.view;

public enum Tag {

  NOT_FROM_LIST("NOT FROM LIST"),
  NONE("");

  private final String displayName;

  Tag(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
