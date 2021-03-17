package uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule;

import java.util.Collections;
import java.util.Map;

public enum DecommissioningStartDateType {

  EXACT("I know the decommissioning work start date"),
  ESTIMATED("I have an estimated decommissioning work start date"),
  UNKNOWN("I am unable to provide the decommissioning work start date at this time");

  private final String displayName;

  DecommissioningStartDateType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getEntryAsMap(DecommissioningStartDateType decommissioningStartDateType) {
    return Collections.singletonMap(decommissioningStartDateType.name(), decommissioningStartDateType.getDisplayName());
  }
}
