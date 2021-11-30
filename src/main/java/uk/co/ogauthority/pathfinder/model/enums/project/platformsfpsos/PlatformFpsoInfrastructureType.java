package uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos;

import java.util.Collections;
import java.util.Map;
import uk.co.ogauthority.pathfinder.controller.project.platformsfpsos.PlatformsFpsosController;

public enum PlatformFpsoInfrastructureType {

  PLATFORM("Platform"),
  FPSO(PlatformsFpsosController.FLOATING_UNIT_TEXT_INIT_CAP);

  private final String displayName;

  PlatformFpsoInfrastructureType(String displayName) {
    this.displayName = displayName;
  }

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
