package uk.co.ogauthority.pathfinder.controller.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Collections;
import java.util.List;
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
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSummaryView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.overview.OverviewService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.ProjectContributorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = OverviewController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class OverviewControllerTest extends ProjectContextAbstractControllerTest {

  private final int projectId = 1;
  private final ProjectDetail detail = ProjectUtil.getProjectDetails();
  private final ProjectSummaryView projectSummaryView = new ProjectSummaryView("test", Collections.emptyList());
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES
  );
  private final Set<ProjectStatus> permittedProjectStatuses = Set.of(ProjectStatus.DRAFT);
  private final Set<ProjectType> permittedProjectTypes = Set.of(ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN);
  private final Set<ProjectPermission> requiredPermissions = ProjectControllerTesterService.PROJECT_CREATE_PERMISSION_SET;

  @MockitoBean
  private OverviewService overviewService;

  private ProjectControllerTesterService projectControllerTesterService;

  @Before
  public void setup() {
    var portalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "org", "org");
    var organisationTeam = TeamTestingUtil.getOrganisationTeam(portalOrganisationGroup);
    when(projectOperatorService.isUserInProjectTeam(any(), any())).thenReturn(false);
    when(projectService.getLatestDetailOrError(projectId)).thenReturn(detail);
    when(projectContributorsCommonService.getProjectContributorsForDetail(detail))
        .thenReturn(List.of(ProjectContributorTestUtil.contributorWithGroupOrg(detail, portalOrganisationGroup)));
    when(teamService.getOrganisationTeamsPersonIsMemberOf(authenticatedUser.getLinkedPerson()))
        .thenReturn(List.of(organisationTeam));
    when(teamService.isPersonMemberOfRegulatorTeam(any())).thenReturn(false);
    projectControllerTesterService = new ProjectControllerTesterService(
        mockMvc,
        projectOperatorService,
        projectContributorsCommonService,
        teamService
    );
  }

  @Test
  public void getOverview_projectContextSmokeTest() {
    projectControllerTesterService
        .withHttpRequestMethod(HttpMethod.GET)
        .withProjectDetail(detail)
        .withUser(authenticatedUser)
        .withPermittedProjectStatuses(permittedProjectStatuses)
        .withPermittedProjectTypes(permittedProjectTypes)
        .withRequiredProjectPermissions(requiredPermissions)
        .withProjectContributorAccess();

    projectControllerTesterService.smokeTestProjectContextAnnotationsForControllerEndpoint(
        on(OverviewController.class).getOverview(projectId, null),
        status().isOk(),
        status().isForbidden()
    );
  }

  @Test
  public void getOverview_assertModelObjects() throws Exception {
    mockMvc.perform(
        get(ReverseRouter.route(on(OverviewController.class).getOverview(projectId, null)))
            .with(authenticatedUserAndSession(authenticatedUser))
        )
        .andExpect(status().isOk());

    verify(overviewService, times(1)).getModelAndView(projectId, detail);
  }
}