package uk.co.ogauthority.pathfinder.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.WorkAreaService;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(WorkAreaController.class)
public class WorkAreaControllerTest extends AbstractControllerTest {

  @MockBean
  private WorkAreaService workAreaService;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.WORK_AREA_PRIVILEGES
  );

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();


  @Test
  public void authenticatedUser_hasAccessToWorkArea() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(authenticatedUser)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessWorkArea() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(unAuthenticatedUser)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToFilteredWorkArea() throws Exception {
    mockMvc.perform(post(ReverseRouter.route(on(WorkAreaController.class).getWorkAreaFiltered(authenticatedUser)))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessFilteredWorkArea() throws Exception {
    mockMvc.perform(post(ReverseRouter.route(on(WorkAreaController.class).getWorkAreaFiltered(unAuthenticatedUser)))
        .with(authenticatedUserAndSession(unAuthenticatedUser))
        .with(csrf()))
        .andExpect(status().isForbidden());
  }
}
