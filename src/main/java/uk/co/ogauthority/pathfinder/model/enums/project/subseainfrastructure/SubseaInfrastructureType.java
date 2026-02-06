package uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure;

import java.util.Collections;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.Displayable;

public enum SubseaInfrastructureType implements Displayable {
  CONCRETE_MATTRESSES("Concrete mattresses"),
  SUBSEA_STRUCTURE("Subsea structure"),
  OTHER("Other");

  private final String displayName;

  SubseaInfrastructureType(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getEntryAsMap(SubseaInfrastructureType subseaInfrastructureType) {
    return Collections.singletonMap(subseaInfrastructureType.name(), subseaInfrastructureType.getDisplayName());
  }
}
