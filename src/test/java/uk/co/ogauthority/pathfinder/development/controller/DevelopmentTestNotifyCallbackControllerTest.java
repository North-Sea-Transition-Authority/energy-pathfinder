package uk.co.ogauthority.pathfinder.development.controller;

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
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.email.notify.callback.NotifyCallbackService;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = DevelopmentTestNotifyCallbackController.class)
@ActiveProfiles("production")
public class DevelopmentTestNotifyCallbackControllerTest extends AbstractControllerTest {

  @MockitoBean
  NotifyCallbackService notifyCallbackService;

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("analytics.config.enabled", () -> false);
    registry.add("analytics.config.connection-timeout-seconds", () -> 1);
  }

  @Test
  public void triggerNotifyCallback_ensureNotValidInProductionProfile() throws Exception {

    final var authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
        SystemAccessService.WORK_AREA_PRIVILEGES
    );

    mockMvc.perform(get(ReverseRouter.route(
        on(DevelopmentTestNotifyCallbackController.class)
            .triggerNotifyCallback("123-456")
        ))
            .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isNotFound());
  }

}