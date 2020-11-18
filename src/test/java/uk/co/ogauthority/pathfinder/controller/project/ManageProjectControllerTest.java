package uk.co.ogauthority.pathfinder.controller.project;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.management.ProjectManagementViewService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ManageProjectController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class ManageProjectControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;

  @MockBean
  private ProjectManagementViewService projectManagementViewService;

  private final ProjectDetail projectDetail  = ProjectUtil.getProjectDetails(ProjectStatus.PUBLISHED);

  private final AuthenticatedUserAccount authenticatedOperatorUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.VIEW_PROJECT_PRIVILEGES);
  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    when(projectService.getLatestDetail(PROJECT_ID)).thenReturn(Optional.of(projectDetail));
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedOperatorUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, unauthenticatedUser)).thenReturn(false);
  }

  @Test
  public void getProject_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ManageProjectController.class).getProject(PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(authenticatedOperatorUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getProject_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ManageProjectController.class).getProject(PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getProject_whenAuthenticatedAndDraft_thenNoAccess() throws Exception {
    var projectId = 2;
    var projectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    when(projectService.getLatestDetail(projectId)).thenReturn(Optional.of(projectDetail));
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedOperatorUser)).thenReturn(true);

    mockMvc.perform(get(ReverseRouter.route(
        on(ManageProjectController.class).getProject(projectId, null, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }
}
