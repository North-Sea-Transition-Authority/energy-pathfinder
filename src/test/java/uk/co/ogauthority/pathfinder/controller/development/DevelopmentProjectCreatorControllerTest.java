package uk.co.ogauthority.pathfinder.controller.development;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
@ActiveProfiles("production")
public class DevelopmentProjectCreatorControllerTest extends AbstractControllerTest {

  @MockitoBean
  DevelopmentProjectCreatorSchedulerService notificationCreatorService;

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("analytics.config.enabled", () -> false);
    registry.add("analytics.config.connection-timeout-seconds", () -> 1);
  }

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  @Test
  public void testGetDevelopmentProjectCreator_notInProductionProfile() throws Exception {

    mockMvc.perform(get(ReverseRouter.route(on(DevelopmentProjectCreatorController.class).createProjects(authenticatedUser)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isNotFound());
  }

}
