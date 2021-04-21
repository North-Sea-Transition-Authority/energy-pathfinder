package uk.co.ogauthority.pathfinder.controller.rest;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.WorkPlanUpcomingTenderService;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;


@RunWith(SpringRunner.class)
@WebMvcTest(
    value = WorkPlanUpcomingTenderRestController.class)
public class WorkPlanUpcomingTenderRestControllerTest extends AbstractControllerTest {

  private static final String SEARCH_TERM = "searchTerm";

  @MockBean
  private WorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  private AuthenticatedUserAccount authenticatedUser;
  private AuthenticatedUserAccount unauthenticatedUser;

  @Before
  public void setup() {
    authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.WORK_AREA_PRIVILEGES);
    unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();
  }

  @Test
  public void searchTenderDepartments_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(WorkPlanUpcomingTenderRestController.class).searchTenderDepartments(SEARCH_TERM)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());

    verify(workPlanUpcomingTenderService, times(1)).findDepartmentTenderLikeWithManualEntry(SEARCH_TERM);
  }

  @Test
  public void searchTenderDepartments_whenUnauthenticated_thenForbidden() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(WorkPlanUpcomingTenderRestController.class).searchTenderDepartments(SEARCH_TERM)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());

    verify(workPlanUpcomingTenderService, times(0)).findDepartmentTenderLikeWithManualEntry(SEARCH_TERM);
  }
}