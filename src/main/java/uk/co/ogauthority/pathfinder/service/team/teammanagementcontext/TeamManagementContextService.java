package uk.co.ogauthority.pathfinder.service.team.teammanagementcontext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.team.Team;
import uk.co.ogauthority.pathfinder.service.teammanagement.TeamManagementService;

@Service
public class TeamManagementContextService {

  private final TeamManagementService teamManagementService;

  @Autowired
  public TeamManagementContextService(TeamManagementService teamManagementService) {
    this.teamManagementService = teamManagementService;
  }

  public TeamManagementContext buildTeamManagementContext(Set<TeamManagementPermission> requiredTeamManagementPermissions,
                                                          Integer resourceId,
                                                          AuthenticatedUserAccount userAccount) {

    var userTeamManagementPermissions = checkTeamManagementPermissions(
        requiredTeamManagementPermissions,
        userAccount
    );

    return createTeamManagementContext(
        userTeamManagementPermissions,
        (resourceId != null)
            ? checkUserCanAccessTeamAndHasCorrectRole(resourceId, requiredTeamManagementPermissions, userAccount)
            : null,
        userAccount
    );
  }

  private Set<TeamManagementPermission> checkTeamManagementPermissions(Set<TeamManagementPermission> teamManagementPermissions,
                                                                       AuthenticatedUserAccount userAccount) {

    var userTeamManagementPermissions = getUserTeamManagementPermissions(userAccount);

    if (userTeamManagementPermissions.stream().noneMatch(teamManagementPermissions::contains)) {
      throw new AccessDeniedException(
          String.format(
              "User with person id %d does not have the required team management permissions: %s. " +
                  "User's TeamManagementPermission's: %s",
              userAccount.getLinkedPerson().getId().asInt(),
              teamManagementPermissions.stream().map(Enum::name).collect(Collectors.joining(",")),
              StringUtils.defaultIfEmpty(
                  userTeamManagementPermissions.stream().map(Enum::name).collect(Collectors.joining(",")),
                  "null"
              )
          )
      );
    }

    return userTeamManagementPermissions;
  }

  private TeamManagementContext createTeamManagementContext(Set<TeamManagementPermission> teamManagementPermissions,
                                                            Team team,
                                                            AuthenticatedUserAccount userAccount) {
    return new TeamManagementContext(
        teamManagementPermissions,
        team,
        userAccount,
        getTeamManagementContextPrivileges(team, userAccount)
    );
  }

  private Set<TeamManagementContextPrivilege> getTeamManagementContextPrivileges(Team team,
                                                                                 AuthenticatedUserAccount userAccount) {
    var teamManagementContextPrivileges = new HashSet<TeamManagementContextPrivilege>();

    if (team != null && teamManagementService.canManageTeam(team, userAccount)) {
      teamManagementContextPrivileges.add(TeamManagementContextPrivilege.TEAM_ACCESS_MANAGER);
    }

    if (teamManagementService.canManageAnyOrgTeam(userAccount)) {
      teamManagementContextPrivileges.add(TeamManagementContextPrivilege.ORGANISATION_ACCESS_MANAGER);
    }

    return teamManagementContextPrivileges;
  }

  private Set<TeamManagementPermission> getUserTeamManagementPermissions(AuthenticatedUserAccount userAccount) {
    return userAccount.getUserPrivileges()
        .stream()
        .flatMap(userPrivilege ->
            Arrays.stream(TeamManagementPermission.values())
                .filter(permission -> permission.hasPrivilege(userPrivilege)))
        .collect(Collectors.toSet());
  }

  private Team checkUserCanAccessTeamAndHasCorrectRole(Integer resourceId,
                                                       Set<TeamManagementPermission> requiredTeamManagementPermissions,
                                                       AuthenticatedUserAccount authenticatedUserAccount) {
    var team = teamManagementService.getTeamOrError(resourceId);

    if (requiredTeamManagementPermissions.contains(TeamManagementPermission.VIEW)
        &&
        !teamManagementService.canViewTeam(team, authenticatedUserAccount)
    ) {
      throw new AccessDeniedException(String.format(
          "User with wua id %s attempted to view resId %s but does not have the correct privileges",
          authenticatedUserAccount.getWuaId(),
          team.getId()
      ));
    }

    if (requiredTeamManagementPermissions.contains(TeamManagementPermission.MANAGE)
        &&
        !teamManagementService.canManageTeam(team, authenticatedUserAccount)
    ) {
      throw new AccessDeniedException(String.format(
          "User with wua id %s attempted to manage resId %s but does not have the correct privileges",
          authenticatedUserAccount.getWuaId(),
          team.getId()
      ));
    }

    return team;
  }
}
