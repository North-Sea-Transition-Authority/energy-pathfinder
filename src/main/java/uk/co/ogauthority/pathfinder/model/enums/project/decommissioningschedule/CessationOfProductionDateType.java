package uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule;

import java.util.Collections;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.Displayable;

public enum CessationOfProductionDateType implements Displayable {

  EXACT("I know the CoP date"),
  ESTIMATED("I have an estimated CoP date"),
  UNKNOWN("I am unable to provide the CoP date at this time");

  private final String displayName;

  CessationOfProductionDateType(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getEntryAsMap(CessationOfProductionDateType cessationOfProductionDateType) {
    return Collections.singletonMap(cessationOfProductionDateType.name(), cessationOfProductionDateType.getDisplayName());
  }
}
