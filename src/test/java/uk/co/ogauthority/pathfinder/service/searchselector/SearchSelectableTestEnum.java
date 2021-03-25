package uk.co.ogauthority.pathfinder.service.searchselector;

import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

public enum SearchSelectableTestEnum implements SearchSelectable {
  VALUE_1("value 1"),
  VALUE_2("value 2"),
  VALUE_3("value 3");

  private final String displayName;

  SearchSelectableTestEnum(String displayName) {
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
