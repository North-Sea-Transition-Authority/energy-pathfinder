package uk.co.ogauthority.pathfinder.controller.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.config.MetricsProvider;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.StartProjectService;
import uk.co.ogauthority.pathfinder.testutil.MetricsProviderTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(StartProjectController.class)
public class StartProjectControllerTest extends AbstractControllerTest {

  @MockBean
  private StartProjectService startProjectService;

  @MockBean
  private MetricsProvider metricsProvider;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  private static final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setUp() throws Exception {
    when(metricsProvider.getProjectStartCounter()).thenReturn(MetricsProviderTestUtil.getNoOpCounter());
  }

  @Test
  public void authenticatedUser_hasAccessToStartProject() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(StartProjectController.class).startPage(authenticatedUser)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessStartProject() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(StartProjectController.class).startPage(unAuthenticatedUser)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void redirect_whenMultipleTeams() throws Exception {
    when(teamService.getOrganisationTeamsPersonIsMemberOf(authenticatedUser.getLinkedPerson())).thenReturn(
        List.of(TeamTestingUtil.getOrganisationTeam(ProjectOperatorTestUtil.ORG_GROUP), TeamTestingUtil.getOrganisationTeam(
            ProjectOperatorTestUtil.ORG_GROUP))
    );
    mockMvc.perform(
          MockMvcRequestBuilders.post(
              ReverseRouter.route(on(StartProjectController.class).startProject(authenticatedUser))
          )
          .with(authenticatedUserAndSession(authenticatedUser))
          .with(csrf())
        )
        .andExpect(status().is3xxRedirection());
    verify(startProjectService, times(0)).startProject(any(), any());
  }

  @Test
  public void redirect_whenSingleTeam() throws Exception {
    when(teamService.getOrganisationTeamsPersonIsMemberOf(authenticatedUser.getLinkedPerson())).thenReturn(
        List.of(TeamTestingUtil.getOrganisationTeam(ProjectOperatorTestUtil.ORG_GROUP))
    );
    when(startProjectService.startProject(authenticatedUser, ProjectOperatorTestUtil.ORG_GROUP)).thenReturn(detail);

    mockMvc.perform(
        MockMvcRequestBuilders.post(
            ReverseRouter.route(on(StartProjectController.class).startProject(authenticatedUser))
        )
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
    )
        .andExpect(status().is3xxRedirection());
    verify(startProjectService, times(1)).startProject(any(), any());
  }
}
