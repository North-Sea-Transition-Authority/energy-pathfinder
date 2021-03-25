package uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule;

import java.util.Collections;
import java.util.Map;

public enum CessationOfProductionDateType {

  EXACT("I know the CoP date"),
  ESTIMATED("I have an estimated CoP date"),
  UNKNOWN("I am unable to provide the CoP date at this time");

  private final String displayName;

  CessationOfProductionDateType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getEntryAsMap(CessationOfProductionDateType cessationOfProductionDateType) {
    return Collections.singletonMap(cessationOfProductionDateType.name(), cessationOfProductionDateType.getDisplayName());
  }
}
