package uk.co.ogauthority.pathfinder.service.searchselector;

import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

public class SearchSelectableTestItem implements SearchSelectable {

  private final Integer id;

  private final String displayName;

  public SearchSelectableTestItem(Integer id, String displayName) {
    this.id = id;
    this.displayName = displayName;
  }

  public Integer getId() {
    return id;
  }

  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String getSelectionId() {
    return String.valueOf(getId());
  }

  @Override
  public String getSelectionText() {
    return getDisplayName();
  }
}
