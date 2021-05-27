package uk.co.ogauthority.pathfinder.controller.rest;

import static org.mockito.Mockito.never;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = ForwardWorkPlanCollaborationOpportunityRestController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class ForwardWorkPlanCollaborationOpportunityRestControllerTest extends AbstractControllerTest {

  private static final String SEARCH_TERM = "searchTerm";

  @MockBean
  protected ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;

  private AuthenticatedUserAccount authenticatedUser;
  private AuthenticatedUserAccount unauthenticatedUser;

  @Before
  public void setup() {
    authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.WORK_AREA_PRIVILEGES);
    unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();
  }

  @Test
  public void searchFunctions_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ForwardWorkPlanCollaborationOpportunityRestController.class).searchFunctions(SEARCH_TERM)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());

    verify(forwardWorkPlanCollaborationOpportunityService, times(1)).findFunctionsLikeWithManualEntry(SEARCH_TERM);
  }

  @Test
  public void searchFunctions_whenUnauthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ForwardWorkPlanCollaborationOpportunityRestController.class).searchFunctions(SEARCH_TERM)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());

    verify(forwardWorkPlanCollaborationOpportunityService, never()).findFunctionsLikeWithManualEntry(SEARCH_TERM);
  }

}