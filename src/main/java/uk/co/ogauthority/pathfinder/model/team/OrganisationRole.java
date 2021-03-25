package uk.co.ogauthority.pathfinder.model.team;

public enum OrganisationRole {
  TEAM_ADMINISTRATOR("RESOURCE_COORDINATOR"),
  PROJECT_SUBMITTER("PROJECT_SUBMITTER");

  private final String portalTeamRoleName;

  OrganisationRole(String portalTeamRoleName) {
    this.portalTeamRoleName = portalTeamRoleName;
  }

  public String getPortalTeamRoleName() {
    return portalTeamRoleName;
  }
}
