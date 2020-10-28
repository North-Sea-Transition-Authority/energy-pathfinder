package uk.co.ogauthority.pathfinder.model.team;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public enum TeamType {
  REGULATOR(
      "PATHFINDER_REGULATOR_TEAM",
      "Pathfinder regulator team",
      StringUtils.EMPTY
  ),
  ORGANISATION(
      "PATHFINDER_ORGANISATION_TEAM",
      "Pathfinder organisation team",
      "The users listed below have access to view all Pathfinder projects for " +
      "your organisation. The roles a user has determines the actions they can carry " +
      "out on behalf of your organisation."
  );

  private final String portalTeamType;
  private final String portalTeamTypeDisplayName;
  private final String teamManagementGuidance;

  TeamType(String portalTeamType, String portalTeamTypeDisplayName, String teamManagementGuidance) {
    this.portalTeamType = portalTeamType;
    this.portalTeamTypeDisplayName = portalTeamTypeDisplayName;
    this.teamManagementGuidance = teamManagementGuidance;
  }

  public String getPortalTeamType() {
    return portalTeamType;
  }

  public String getPortalTeamTypeDisplayName() {
    return portalTeamTypeDisplayName;
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
