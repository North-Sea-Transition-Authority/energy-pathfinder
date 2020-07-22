package uk.co.ogauthority.pathfinder.model.team;

public enum OrganisationRole {
  TEAM_ADMINISTRATOR("RESOURCE_COORDINATOR"),
  PROJECT_CREATOR("PROJECT_CREATE");

  private final String portalTeamRoleName;

  OrganisationRole(String portalTeamRoleName) {
    this.portalTeamRoleName = portalTeamRoleName;
  }

  public String getPortalTeamRoleName() {
    return portalTeamRoleName;
  }
}
