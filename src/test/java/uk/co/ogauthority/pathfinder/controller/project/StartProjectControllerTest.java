package uk.co.ogauthority.pathfinder.controller.project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

@RunWith(SpringRunner.class)
@WebMvcTest(StartProjectController.class)
public class StartProjectControllerTest extends AbstractControllerTest {

  private final AuthenticatedUserAccount authenticatedUser = new AuthenticatedUserAccount(
      new WebUserAccount(1), SystemAccessService.CREATE_PROJECT_PRIVILEGES
  );

  private final AuthenticatedUserAccount unAuthenticatedUser = new AuthenticatedUserAccount(
      new WebUserAccount(1), Collections.emptySet()
  );

  @Test
  public void authenticatedUser_hasAccessToStartProject() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(StartProjectController.class).startPage()))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessStartProject() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(StartProjectController.class).startPage()))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }
}
