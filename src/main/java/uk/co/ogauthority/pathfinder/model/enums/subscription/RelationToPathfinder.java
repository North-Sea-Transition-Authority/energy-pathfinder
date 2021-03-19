package uk.co.ogauthority.pathfinder.model.enums.subscription;

import java.util.Collections;
import java.util.Map;

public enum RelationToPathfinder {

  DEVELOPER("Developer"),
  SUPPLY_CHAIN("Supply chain"),
  OPERATOR("Operator"),
  OTHER("Other");

  private final String displayName;

  RelationToPathfinder(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getEntryAsMap(RelationToPathfinder relationToPathfinder) {
    return Collections.singletonMap(
        relationToPathfinder.name(),
        relationToPathfinder.getDisplayName()
    );
  }
}
