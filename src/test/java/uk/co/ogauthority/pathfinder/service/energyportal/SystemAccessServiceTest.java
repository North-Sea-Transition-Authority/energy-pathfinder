package uk.co.ogauthority.pathfinder.service.energyportal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.testutil.AuthTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class SystemAccessServiceTest {

  private SystemAccessService systemAccessService;

  @Before
  public void setup() {
    systemAccessService = new SystemAccessService();
  }

  @Test
  public void canViewTeam() {
    AuthTestingUtil.testPrivilegeBasedAuthenticationFunction(
        Set.of(
            UserPrivilege.PATHFINDER_TEAM_VIEWER
        ),
        systemAccessService::canViewTeam
    );
  }

  @Test
  public void getViewTeamGrantedAuthorities() {
    assertGrantedAuthorities(
        SystemAccessService.VIEW_TEAM_PRIVILEGES,
        systemAccessService::getViewTeamGrantedAuthorities
    );
  }

  @Test
  public void canAccessWorkArea() {
    AuthTestingUtil.testPrivilegeBasedAuthenticationFunction(
        Set.of(UserPrivilege.PATHFINDER_WORK_AREA),
        systemAccessService::canAccessWorkArea
    );
  }

  @Test
  public void getWorkAreaGrantedAuthorities() {
    assertGrantedAuthorities(
        SystemAccessService.WORK_AREA_PRIVILEGES,
        systemAccessService::getWorkAreaGrantedAuthorities
    );
  }

  @Test
  public void canCreateProject() {
    AuthTestingUtil.testPrivilegeBasedAuthenticationFunction(
        Set.of(UserPrivilege.PATHFINDER_PROJECT_CREATE),
        systemAccessService::canCreateProject
    );
  }

  @Test
  public void getCreateProjectGrantedAuthorities() {
    assertGrantedAuthorities(
        SystemAccessService.CREATE_PROJECT_PRIVILEGES,
        systemAccessService::getCreateProjectGrantedAuthorities
    );
  }

  @Test
  public void canAccessQuarterlyStatistics() {
    AuthTestingUtil.testPrivilegeBasedAuthenticationFunction(
        Set.of(UserPrivilege.PATHFINDER_STATISTIC_VIEWER),
        systemAccessService::canAccessQuarterlyStatistics
    );
  }

  @Test
  public void getQuarterlyStatisticsGrantedAuthorities() {
    assertGrantedAuthorities(
        SystemAccessService.QUARTERLY_STATISTICS_PRIVILEGES,
        systemAccessService::getQuarterlyStatisticsGrantedAuthorities
    );
  }

  @Test
  public void canAccessCommunications() {
    AuthTestingUtil.testPrivilegeBasedAuthenticationFunction(
        Set.of(UserPrivilege.PATHFINDER_COMMUNICATIONS),
        systemAccessService::canAccessCommunications
    );
  }

  @Test
  public void getCommunicationsGrantedAuthorities() {
    assertGrantedAuthorities(
        SystemAccessService.COMMUNICATION_PRIVILEGES,
        systemAccessService::getCommunicationsGrantedAuthorities
    );
  }

  @Test
  public void getFeedbackGrantedAuthorities_verifyAuthorities() {
    assertGrantedAuthorities(
        SystemAccessService.FEEDBACK_PRIVILEGES,
        systemAccessService::getFeedbackGrantedAuthorities
    );
  }

  private void assertGrantedAuthorities(Set<UserPrivilege> expectedUserPrivileges, Callable<String[]> testFunction) {

    final var privileges = expectedUserPrivileges
        .stream()
        .map(UserPrivilege::name)
        .collect(Collectors.toList());

    var grantedAuthorities = new String[0];
    try {
      grantedAuthorities = testFunction.call();
    } catch (Exception e) {
      e.printStackTrace();
    }

    assertThat(grantedAuthorities).containsExactlyElementsOf(privileges);
  }

}
