package uk.co.ogauthority.pathfinder.service.team.teammanagementcontext;

import java.util.Set;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.team.Team;

public class TeamManagementContext {

  private final Set<TeamManagementPermission> teamManagementPermissions;

  private final Team team;

  private final AuthenticatedUserAccount userAccount;

  private final Set<TeamManagementContextPrivilege> teamManagementContextPrivileges;

  public TeamManagementContext(Set<TeamManagementPermission> teamManagementPermissions,
                               Team team,
                               AuthenticatedUserAccount userAccount,
                               Set<TeamManagementContextPrivilege> teamManagementContextPrivileges) {
    this.teamManagementPermissions = teamManagementPermissions;
    this.team = team;
    this.userAccount = userAccount;
    this.teamManagementContextPrivileges = teamManagementContextPrivileges;
  }

  public Set<TeamManagementPermission> getTeamManagementPermissions() {
    return teamManagementPermissions;
  }

  public Team getTeam() {
    return team;
  }

  public AuthenticatedUserAccount getUserAccount() {
    return userAccount;
  }

  public boolean isOrganisationAccessManager() {
    return teamManagementContextPrivileges.contains(TeamManagementContextPrivilege.ORGANISATION_ACCESS_MANAGER);
  }

  public boolean isTeamAccessManager() {
    return teamManagementContextPrivileges.contains(TeamManagementContextPrivilege.TEAM_ACCESS_MANAGER);
  }

  public boolean canManageTeam() {
    return isTeamAccessManager() || isOrganisationAccessManager();
  }
}
