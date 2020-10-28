package uk.co.ogauthority.pathfinder.service.team.teammanagementcontext;

import java.util.Set;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;

public enum TeamManagementPermission {
  CREATE(Set.of(UserPrivilege.PATHFINDER_REG_ORG_MANAGER)),
  MANAGE(Set.of(
      UserPrivilege.PATHFINDER_REG_ORG_MANAGER,
      UserPrivilege.PATHFINDER_ORG_ADMIN,
      UserPrivilege.PATHFINDER_REGULATOR_ADMIN
  )),
  VIEW(Set.of(UserPrivilege.PATHFINDER_TEAM_VIEWER));

  private final Set<UserPrivilege> userPrivileges;

  TeamManagementPermission(Set<UserPrivilege> userPrivileges) {
    this.userPrivileges = userPrivileges;
  }

  public Set<UserPrivilege> getUserPrivileges() {
    return userPrivileges;
  }

  public boolean hasPrivilege(UserPrivilege userPrivilege) {
    return userPrivileges.contains(userPrivilege);
  }
}
