package uk.co.ogauthority.pathfinder.model.team;

import java.util.Arrays;

public enum TeamType {
  REGULATOR("PATHFINDER_REGULATOR_TEAM"),
  ORGANISATION("PATHFINDER_ORGANISATION_TEAM");

  private final String portalTeamType;

  TeamType(String portalTeamType) {
    this.portalTeamType = portalTeamType;
  }

  public String getPortalTeamType() {
    return portalTeamType;
  }

  public static TeamType findByPortalTeamType(String portalTeamType) {
    return Arrays.stream(values())
        .filter(t -> t.getPortalTeamType().equals(portalTeamType))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Portal Team Type " + portalTeamType + " not known"));
  }
}
