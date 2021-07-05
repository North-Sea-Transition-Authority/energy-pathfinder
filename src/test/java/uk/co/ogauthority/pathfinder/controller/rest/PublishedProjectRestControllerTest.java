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
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.PublishedProjectAccessorService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = PublishedProjectRestController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class PublishedProjectRestControllerTest extends AbstractControllerTest {

  private static final String SEARCH_TERM = "searchTerm";

  @MockBean
  private PublishedProjectAccessorService publishedProjectAccessorService;

  private AuthenticatedUserAccount authenticatedUser;
  private AuthenticatedUserAccount unauthenticatedUser;

  @Before
  public void setup() {
    authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.WORK_AREA_PRIVILEGES);
    unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();
  }

  @Test
  public void searchPublishedInfrastructureProjects_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PublishedProjectRestController.class).searchPublishedInfrastructureProjects(SEARCH_TERM)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());

    verify(publishedProjectAccessorService, times(1)).searchProjectsWithDisplayNameContaining(
        SEARCH_TERM,
        ProjectType.INFRASTRUCTURE
    );
  }

  @Test
  public void searchPublishedInfrastructureProjects_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PublishedProjectRestController.class).searchPublishedInfrastructureProjects(SEARCH_TERM)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());

    verify(publishedProjectAccessorService, never()).searchProjectsWithDisplayNameContaining(
        SEARCH_TERM,
        ProjectType.INFRASTRUCTURE
    );
  }

}