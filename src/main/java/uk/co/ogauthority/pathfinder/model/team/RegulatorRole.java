package uk.co.ogauthority.pathfinder.model.team;

import java.util.stream.Stream;

public enum RegulatorRole {

  TEAM_ADMINISTRATOR("RESOURCE_COORDINATOR"),
  ORGANISATION_MANAGER("ORGANISATION_MANAGER"),
  COMMENT_PROVIDER("COMMENT_PROVIDER"),
  PROJECT_VIEWER("PROJECT_VIEWER");

  private final String portalTeamRoleName;

  RegulatorRole(String portalTeamRoleName) {
    this.portalTeamRoleName = portalTeamRoleName;
  }

  public String getPortalTeamRoleName() {
    return portalTeamRoleName;
  }

  public static RegulatorRole getValueByPortalTeamRoleName(String portalTeamRoleName) {
    return Stream.of(RegulatorRole.values())
        .filter(r -> r.getPortalTeamRoleName().equals(portalTeamRoleName))
        .findFirst()
        .orElseThrow(() -> new RuntimeException(String.format(
            "Couldn't map portal team role name: %s to a RegulatorRole value", portalTeamRoleName)));
  }

}
