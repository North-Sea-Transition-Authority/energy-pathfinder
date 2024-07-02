package uk.co.ogauthority.pathfinder.service.searchselector;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class SearchSelectableTestEntityWithEnum {

  @Enumerated(EnumType.STRING)
  private SearchSelectableTestEnum fromListValue;

  private String manualEntry;

  public SearchSelectableTestEnum getFromListValue() {
    return fromListValue;
  }

  public void setFromListValue(SearchSelectableTestEnum fromListValue) {
    this.fromListValue = fromListValue;
  }

  public String getManualEntry() {
    return manualEntry;
  }

  public void setManualEntry(String manualEntry) {
    this.manualEntry = manualEntry;
  }
}
