package uk.co.ogauthority.pathfinder.energyportal.service;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;

@Service
public class SystemAccessService {

  public final Set<UserPrivilege> workAreaPrivileges = Set.of(
      UserPrivilege.PATHFINDER_WORK_AREA
  );

  public final Set<UserPrivilege> teamAdministrationPrivileges = Set.of(
      UserPrivilege.PATHFINDER_REGULATOR_ADMIN,
      UserPrivilege.PATHFINDER_REG_ORG_MANAGER,
      UserPrivilege.PATHFINDER_ORG_ADMIN
  );

  /**
   * For use in WebSecurityConfig. In other instances call canAccessWorkArea
   */
  public String[] getWorkAreaGrantedAuthorities() {
    return getGrantedAuthorities(workAreaPrivileges);
  }

  public boolean canAccessWorkArea(AuthenticatedUserAccount user) {
    return hasRelevantPrivilege(user, workAreaPrivileges);
  }

  /**
   * For use in WebSecurityConfig. In other instances call canAccessTeamAdministration
   */
  public String[] getTeamAdministrationGrantedAuthorities() {
    return getGrantedAuthorities(teamAdministrationPrivileges);
  }

  public boolean canAccessTeamAdministration(AuthenticatedUserAccount user) {
    return hasRelevantPrivilege(user, teamAdministrationPrivileges);
  }

  private String[] getGrantedAuthorities(Set<UserPrivilege> userPrivileges) {
    return userPrivileges.stream()
        .map(UserPrivilege::name)
        .toArray(String[]::new);
  }

  private boolean hasRelevantPrivilege(AuthenticatedUserAccount user, Set<UserPrivilege> privileges) {
    return user.getUserPrivileges().stream()
        .anyMatch(privileges::contains);
  }
}
