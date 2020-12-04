package uk.co.ogauthority.pathfinder.controller.projectmanagement;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementViewService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ManageProjectController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class ManageProjectControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PUBLISHED_PROJECT_ID = 1;
  private static final Integer DRAFT_PROJECT_ID = 2;

  @MockBean
  private ProjectManagementViewService projectManagementViewService;

  private final ProjectDetail publishedProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.PUBLISHED);
  private final ProjectDetail draftProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      ProjectPermission.VIEW.getUserPrivileges());
  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    when(projectService.getLatestDetail(PUBLISHED_PROJECT_ID)).thenReturn(Optional.of(publishedProjectDetail));
    when(projectOperatorService.isUserInProjectTeamOrRegulator(publishedProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(publishedProjectDetail, unauthenticatedUser)).thenReturn(false);

    when(projectService.getLatestDetail(DRAFT_PROJECT_ID)).thenReturn(Optional.of(draftProjectDetail));
    when(projectOperatorService.isUserInProjectTeamOrRegulator(draftProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(draftProjectDetail, unauthenticatedUser)).thenReturn(false);
  }

  @Test
  public void getProject_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ManageProjectController.class).getProject(PUBLISHED_PROJECT_ID, null, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getProject_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ManageProjectController.class).getProject(PUBLISHED_PROJECT_ID, null, null, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getProject_whenAuthenticatedAndDraft_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ManageProjectController.class).getProject(DRAFT_PROJECT_ID, null, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void updateProjectVersion_whenAuthenticated_thenRedirect() throws Exception {
    mockMvc.perform(post(ReverseRouter.route(
        on(ManageProjectController.class).updateProjectVersion(PUBLISHED_PROJECT_ID, null, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  public void updateProjectVersion_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(post(ReverseRouter.route(
        on(ManageProjectController.class).updateProjectVersion(PUBLISHED_PROJECT_ID, null, null, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser))
        .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  public void updateProjectVersion_whenAuthenticatedAndDraft_thenNoAccess() throws Exception {
    mockMvc.perform(post(ReverseRouter.route(
        on(ManageProjectController.class).updateProjectVersion(DRAFT_PROJECT_ID, null, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().isForbidden());
  }
}
