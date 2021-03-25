package uk.co.ogauthority.pathfinder.energyportal.service;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;

@Service
public class SystemAccessService {

  public static final Set<UserPrivilege> WORK_AREA_PRIVILEGES = Set.of(
      UserPrivilege.PATHFINDER_WORK_AREA
  );

  public static final Set<UserPrivilege> CREATE_PROJECT_PRIVILEGES = Set.of(
      UserPrivilege.PATHFINDER_PROJECT_CREATE
  );

  public static final Set<UserPrivilege> VIEW_TEAM_PRIVILEGES = Set.of(
      UserPrivilege.PATHFINDER_TEAM_VIEWER
  );

  public static final Set<UserPrivilege> QUARTERLY_STATISTICS_PRIVILEGES = Set.of(
      UserPrivilege.PATHFINDER_STATISTIC_VIEWER
  );

  public static final Set<UserPrivilege> COMMUNICATION_PRIVILEGES = Set.of(
      UserPrivilege.PATHFINDER_COMMUNICATIONS
  );

  /**
   * For use in WebSecurityConfig. In other instances call canAccessWorkArea
   */
  public String[] getWorkAreaGrantedAuthorities() {
    return getGrantedAuthorities(WORK_AREA_PRIVILEGES);
  }

  public boolean canAccessWorkArea(AuthenticatedUserAccount user) {
    return hasRelevantPrivilege(user, WORK_AREA_PRIVILEGES);
  }

  /**
   * For use in WebSecurityConfig. In other instances call canViewTeam
   */
  public String[] getViewTeamGrantedAuthorities() {
    return getGrantedAuthorities(VIEW_TEAM_PRIVILEGES);
  }

  public boolean canViewTeam(AuthenticatedUserAccount user) {
    return hasRelevantPrivilege(user, VIEW_TEAM_PRIVILEGES);
  }

  public String[] getCreateProjectGrantedAuthorities() {
    return getGrantedAuthorities(CREATE_PROJECT_PRIVILEGES);
  }

  public boolean canCreateProject(AuthenticatedUserAccount user) {
    return hasRelevantPrivilege(user, CREATE_PROJECT_PRIVILEGES);
  }

  public boolean canAccessQuarterlyStatistics(AuthenticatedUserAccount user) {
    return hasRelevantPrivilege(user, QUARTERLY_STATISTICS_PRIVILEGES);
  }

  public String[] getQuarterlyStatisticsGrantedAuthorities() {
    return getGrantedAuthorities(QUARTERLY_STATISTICS_PRIVILEGES);
  }

  public boolean canAccessCommunications(AuthenticatedUserAccount user) {
    return hasRelevantPrivilege(user, COMMUNICATION_PRIVILEGES);
  }

  public String[] getCommunicationsGrantedAuthorities() {
    return getGrantedAuthorities(COMMUNICATION_PRIVILEGES);
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
