package uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos;

import java.util.Collections;
import java.util.Map;
import uk.co.ogauthority.pathfinder.controller.project.platformsfpsos.PlatformsFpsosController;
import uk.co.ogauthority.pathfinder.util.Displayable;

public enum PlatformFpsoInfrastructureType implements Displayable {

  PLATFORM("Platform"),
  FPSO(PlatformsFpsosController.FLOATING_UNIT_TEXT_INIT_CAP);

  private final String displayName;

  PlatformFpsoInfrastructureType(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getEntryAsMap(PlatformFpsoInfrastructureType platformFpsoInfrastructureType) {
    return Collections.singletonMap(
        platformFpsoInfrastructureType.name(),
        platformFpsoInfrastructureType.getDisplayName()
    );
  }
}
