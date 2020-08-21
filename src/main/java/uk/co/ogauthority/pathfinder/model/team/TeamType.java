package uk.co.ogauthority.pathfinder.model.team;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public enum TeamType {
  REGULATOR("PATHFINDER_REGULATOR_TEAM", StringUtils.EMPTY),
  ORGANISATION("PATHFINDER_ORGANISATION_TEAM",
      "The users listed below have access to view all Pathfinder projects for " +
      "your organisation. The roles a user has determines the actions they can carry " +
      "out on behalf of your organisation."
  );

  private final String portalTeamType;
  private final String teamManagementGuidance;

  TeamType(String portalTeamType, String teamManagementGuidance) {
    this.portalTeamType = portalTeamType;
    this.teamManagementGuidance = teamManagementGuidance;
  }

  public String getPortalTeamType() {
    return portalTeamType;
  }

  public String getTeamManagementGuidance() {
    return this.teamManagementGuidance;
  }

  public static TeamType findByPortalTeamType(String portalTeamType) {
    return Arrays.stream(values())
        .filter(t -> t.getPortalTeamType().equals(portalTeamType))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Portal Team Type " + portalTeamType + " not known"));
  }
}
