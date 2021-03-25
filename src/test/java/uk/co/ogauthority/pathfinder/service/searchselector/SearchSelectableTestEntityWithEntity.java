package uk.co.ogauthority.pathfinder.service.searchselector;

public class SearchSelectableTestEntityWithEntity {

  private SearchSelectableTestItem searchSelectableTestItem;

  private String manualEntryItem;

  public SearchSelectableTestItem getSearchSelectableTestItem() {
    return searchSelectableTestItem;
  }

  public void setSearchSelectableTestItem(
      SearchSelectableTestItem searchSelectableTestItem) {
    this.searchSelectableTestItem = searchSelectableTestItem;
  }

  public String getManualEntryItem() {
    return manualEntryItem;
  }

  public void setManualEntryItem(String manualEntryItem) {
    this.manualEntryItem = manualEntryItem;
  }
}
