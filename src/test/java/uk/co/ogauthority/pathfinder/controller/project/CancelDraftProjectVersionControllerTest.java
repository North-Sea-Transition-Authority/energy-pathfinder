package uk.co.ogauthority.pathfinder.controller.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsEventCategory;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.ProjectControllerTesterService;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.cancellation.CancelDraftProjectVersionService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = CancelDraftProjectVersionController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ProjectContextService.class})
)
public class CancelDraftProjectVersionControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;

  @MockBean
  private CancelDraftProjectVersionService cancelDraftProjectVersionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private final Set<ProjectStatus> permittedProjectStatuses = Set.of(ProjectStatus.DRAFT);

  private final Set<ProjectType> permittedProjectTypes = Set.of(ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN);

  private final Set<ProjectPermission> requiredPermissions = ProjectControllerTesterService.PROJECT_CREATE_PERMISSION_SET;

  private ProjectControllerTesterService projectControllerTesterService;

  @Before
  public void setup() {

    projectControllerTesterService = new ProjectControllerTesterService(mockMvc, projectOperatorService);

    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(projectDetail);
  }

  @Test
  public void getCancelDraft_whenCancellable_projectContextSmokeTest() {

    when(cancelDraftProjectVersionService.isCancellable(projectDetail)).thenReturn(true);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CancelDraftProjectVersionController.class).getCancelDraft(PROJECT_ID, null, null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void getCancelDraft_whenNotCancellable_projectContextSmokeTest() {

    when(cancelDraftProjectVersionService.isCancellable(projectDetail)).thenReturn(false);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CancelDraftProjectVersionController.class).getCancelDraft(PROJECT_ID, null, null),
        status().isForbidden(),
        status().isForbidden()
    );
  }

  @Test
  public void cancelDraft_whenCancellable_projectContextSmokeTest() {

    when(cancelDraftProjectVersionService.isCancellable(projectDetail)).thenReturn(true);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CancelDraftProjectVersionController.class).cancelDraft(PROJECT_ID, null, null, Optional.empty()),
        status().is3xxRedirection(),
        status().isForbidden()
    );
  }

  @Test
  public void cancelDraft_whenNotCancellable_projectContextSmokeTest() {

    when(cancelDraftProjectVersionService.isCancellable(projectDetail)).thenReturn(false);

    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.POST)
        .withProjectDetail(projectDetail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions);

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(CancelDraftProjectVersionController.class).cancelDraft(PROJECT_ID, null, null, Optional.empty()),
        status().isForbidden(),
        status().isForbidden()
    );
  }

  @Test
  public void cancelDraft_whenAuthenticatedAndCancellable_thenCancel() throws Exception {

    when(cancelDraftProjectVersionService.isCancellable(any())).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedUser)).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(CancelDraftProjectVersionController.class)
                .cancelDraft(PROJECT_ID, null, null, Optional.empty())
            ))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(cancelDraftProjectVersionService, times(1)).cancelDraft(projectDetail);
    verify(analyticsService, times(1))
        .sendAnalyticsEvent(any(), eq(AnalyticsEventCategory.PROJECT_DRAFT_CANCELLED), eq(
            Map.of("project_type", projectDetail.getProjectType().name())));

  }

  @Test
  public void cancelDraft_whenAuthenticatedAndNotCancellable_noCancel() throws Exception {

    when(cancelDraftProjectVersionService.isCancellable(any())).thenReturn(false);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedUser)).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(CancelDraftProjectVersionController.class)
                .cancelDraft(PROJECT_ID, null, null, Optional.empty())
            ))
                .with(authenticatedUserAndSession(authenticatedUser))
                .with(csrf()))
        .andExpect(status().isForbidden());

    verify(cancelDraftProjectVersionService, times(0)).cancelDraft(projectDetail);
    verifyNoInteractions(analyticsService);

  }

}
