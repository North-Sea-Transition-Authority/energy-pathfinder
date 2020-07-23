package uk.co.ogauthority.pathfinder.service.energyportal;

import java.util.Set;
import org.junit.Test;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.testutil.AuthTestingUtil;

public class SystemAccessServiceTest {

  private final SystemAccessService systemAccessService = new SystemAccessService();

  @Test
  public void canAccessTeamAdministration() {
    AuthTestingUtil.testPrivilegeBasedAuthenticationFunction(
        Set.of(
            UserPrivilege.PATHFINDER_REG_ORG_MANAGER,
            UserPrivilege.PATHFINDER_REGULATOR_ADMIN,
            UserPrivilege.PATHFINDER_ORG_ADMIN
        ),
        systemAccessService::canAccessTeamAdministration
    );
  }

  @Test
  public void canAccessWorkArea() {
    AuthTestingUtil.testPrivilegeBasedAuthenticationFunction(
        Set.of(UserPrivilege.PATHFINDER_WORK_AREA),
        systemAccessService::canAccessWorkArea
    );
  }

}
