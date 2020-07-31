package uk.co.ogauthority.pathfinder.service.energyportal;

import java.util.Set;
import org.junit.Test;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.testutil.AuthTestingUtil;

public class SystemAccessServiceTest {

  private final SystemAccessService systemAccessService = new SystemAccessService();

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
  public void canAccessWorkArea() {
    AuthTestingUtil.testPrivilegeBasedAuthenticationFunction(
        Set.of(UserPrivilege.PATHFINDER_WORK_AREA),
        systemAccessService::canAccessWorkArea
    );
  }

  @Test
  public void canCreateProject() {
    AuthTestingUtil.testPrivilegeBasedAuthenticationFunction(
        Set.of(UserPrivilege.PATHFINDER_PROJECT_CREATE),
        systemAccessService::canCreateProject
    );
  }

}
