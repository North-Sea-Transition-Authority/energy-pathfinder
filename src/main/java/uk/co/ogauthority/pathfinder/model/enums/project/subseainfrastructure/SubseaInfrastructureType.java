package uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure;

import java.util.Collections;
import java.util.Map;

public enum SubseaInfrastructureType {
  CONCRETE_MATTRESSES("Concrete mattresses"),
  SUBSEA_STRUCTURE("Subsea structure"),
  OTHER("Other");

  private final String displayName;

  SubseaInfrastructureType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getEntryAsMap(SubseaInfrastructureType subseaInfrastructureType) {
    return Collections.singletonMap(subseaInfrastructureType.name(), subseaInfrastructureType.getDisplayName());
  }
}
