package uk.co.ogauthority.pathfinder.controller.projectmanagement;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerTesterService;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementViewService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ManageProjectController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class ManageProjectControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PUBLISHED_PROJECT_ID = 1;
  private static final Integer UNSUBMITTED_PROJECT_ID = 2;

  @MockitoBean
  private ProjectManagementViewService projectManagementViewService;

  private ProjectControllerTesterService projectControllerTesterService;

  private final ProjectDetail publishedProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.PUBLISHED);
  private final ProjectDetail unsubmittedProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      ProjectPermission.VIEW.getUserPrivileges());
  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();
  private final Set<ProjectStatus> permittedProjectStatuses = Set.of(
      ProjectStatus.QA,
      ProjectStatus.PUBLISHED,
      ProjectStatus.ARCHIVED
  );
  private final Set<ProjectType> permittedProjectTypes = Set.of(ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN);
  private final Set<ProjectPermission> requiredPermissions = Set.of(ProjectPermission.VIEW);

  @Before
  public void setup() {
    when(projectService.getLatestSubmittedDetailOrError(PUBLISHED_PROJECT_ID)).thenReturn(publishedProjectDetail);
    when(projectService.getLatestDetailOrError(PUBLISHED_PROJECT_ID)).thenReturn(publishedProjectDetail);
    when(projectOperatorService.isUserInProjectTeam(publishedProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeam(publishedProjectDetail, unauthenticatedUser)).thenReturn(false);

    doThrow(new PathfinderEntityNotFoundException("test")).when(projectService).getLatestSubmittedDetailOrError(UNSUBMITTED_PROJECT_ID);
    when(projectOperatorService.isUserInProjectTeam(unsubmittedProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeam(unsubmittedProjectDetail, unauthenticatedUser)).thenReturn(false);

    projectControllerTesterService = new ProjectControllerTesterService(
        mockMvc,
        projectOperatorService,
        projectContributorsCommonService,
        teamService
    );
  }

  @Test
  public void getProject_projectContextSmokeTest() {

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(publishedProjectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(ManageProjectController.class).getProject(PUBLISHED_PROJECT_ID, null, null, null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void updateProjectVersion_projectContextSmokeTest() {

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(publishedProjectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(ManageProjectController.class).updateProjectVersion(PUBLISHED_PROJECT_ID, null, null, null),
        status().is3xxRedirection(),
        status().isForbidden()
    );
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
  public void getProject_whenAuthenticatedAndUnsubmitted_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ManageProjectController.class).getProject(UNSUBMITTED_PROJECT_ID, null, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isNotFound());
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
  public void updateProjectVersion_whenAuthenticatedAndUnsubmitted_thenNoAccess() throws Exception {
    mockMvc.perform(post(ReverseRouter.route(
        on(ManageProjectController.class).updateProjectVersion(UNSUBMITTED_PROJECT_ID, null, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().isNotFound());
  }
}
