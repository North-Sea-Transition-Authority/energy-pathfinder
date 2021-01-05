package uk.co.ogauthority.pathfinder.controller.development;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.development.controller.DevelopmentProjectCreatorController;
import uk.co.ogauthority.pathfinder.development.service.DevelopmentProjectCreatorSchedulerService;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = DevelopmentProjectCreatorController.class)
@TestPropertySource(properties = {
    "PATHFINDER_DB_URL=jdbc:oracle:thin:@edu-dev-db5.decc.local:1521:edudev1",
    "PATHFINDER_DB_SCHEMA=dev",
    "PATHFINDER_DB_PASSWORD=dev1",
    "PATHFINDER_FOX_LOGIN_URL=http://edu-app3.decc.local/engedudev1/fox/oga/OGA_LOGIN/login?REFERRED_BY=PATHFINDER",
    "PATHFINDER_FOX_LOGIN_URL=http://edu-app3.decc.local/engedudev1/fox/oga/OGA_LOGIN/logout",
    "PATHFINDER_FOX_REGISTRATION_URL=http://edu-app3.decc.local/engedudev1/fox?foxopen=oga/LOGIN001L/register",
    "PATHFINDER_CLAMAV_HOST=dkr1.prod.fivium.local",
    "PATHFINDER_CLAMAV_PORT=3310",
    "PATHFINDER_CLAMAV_TIMEOUT=60000",
    "PATHFINDER_CONTEXT=/engedudev1"
})
@ActiveProfiles("production")
public class DevelopmentProjectCreatorControllerTest extends AbstractControllerTest {

  @MockBean
  DevelopmentProjectCreatorSchedulerService notificationCreatorService;


  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);


  @Test
  public void testGetDevelopmentProjectCreator_notInProductionProfile() throws Exception {

    mockMvc.perform(get(ReverseRouter.route(on(DevelopmentProjectCreatorController.class).createProjects(authenticatedUser)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isNotFound());
  }
}
