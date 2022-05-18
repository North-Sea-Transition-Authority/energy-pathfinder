package uk.co.ogauthority.pathfinder.service.projectassessment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectDetailVersionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.project.projectcontribution.ProjectContributorsCommonService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectAssessmentContextServiceTest {

  @Mock
  private ProjectContextService projectContextService;

  @Mock
  private ProjectAssessmentService projectAssessmentService;

  private ProjectAssessmentContextService projectAssessmentContextService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();
  private final Set<ProjectStatus> projectStatuses = Set.of(ProjectStatus.QA);
  private final Set<ProjectPermission> projectPermissions = Set.of(ProjectPermission.PROVIDE_ASSESSMENT);
  private final Set<ProjectType> allowedProjectTypes = Set.of(projectDetail.getProjectType());
  private final boolean allowProjectContributors = false;

  @Before
  public void setup() {
    projectAssessmentContextService = new ProjectAssessmentContextService(
        projectContextService,
        projectAssessmentService);

    when(projectContextService.buildProjectContext(any(), any(), any(), any(), any(), anyBoolean()))
        .thenAnswer(invocation -> new ProjectContext(invocation.getArgument(0), invocation.getArgument(3), invocation.getArgument(1)));

    when(projectContextService.getProjectStatusesForClass(TestController.class)).thenReturn(projectStatuses);
    when(projectContextService.getProjectPermissionsForClass(TestController.class)).thenReturn(projectPermissions);
    when(projectContextService.getProjectTypesForClass(TestController.class)).thenReturn(allowedProjectTypes);
    when(projectContextService.getContributorsAllowedForClass(TestController.class)).thenReturn(allowProjectContributors);
  }

  @Test
  public void canBuildContext_whenNotAssessed() {
    when(projectAssessmentService.hasProjectBeenAssessed(projectDetail)).thenReturn(false);

    var result = projectAssessmentContextService.canBuildContext(
        projectDetail,
        authenticatedUser,
        TestController.class
    );

    assertThat(result).isTrue();

    verify(projectContextService, times(1)).buildProjectContext(
        projectDetail,
        authenticatedUser,
        projectStatuses,
        projectPermissions,
        allowedProjectTypes,
        allowProjectContributors
    );
  }

  @Test
  public void canBuildContext_whenAssessed() {
    when(projectAssessmentService.hasProjectBeenAssessed(projectDetail)).thenReturn(true);

    var result = projectAssessmentContextService.canBuildContext(
        projectDetail,
        authenticatedUser,
        TestController.class
    );

    assertThat(result).isFalse();

    verify(projectContextService, times(0)).buildProjectContext(
        projectDetail,
        authenticatedUser,
        projectStatuses,
        projectPermissions,
        allowedProjectTypes,
        allowProjectContributors
    );
  }

  @Test
  public void buildProjectAssessmentContext_whenNotAssessed() {
    when(projectAssessmentService.hasProjectBeenAssessed(projectDetail)).thenReturn(false);

    var projectAssessmentContext = projectAssessmentContextService.buildProjectAssessmentContext(
        projectDetail,
        authenticatedUser,
        projectStatuses,
        projectPermissions,
        allowedProjectTypes,
        allowProjectContributors
    );

    assertThat(projectAssessmentContext.getProjectDetails()).isEqualTo(projectDetail);
    assertThat(projectAssessmentContext.getProjectPermissions()).isEqualTo(projectPermissions);
    assertThat(projectAssessmentContext.getUserAccount()).isEqualTo(authenticatedUser);
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectAssessmentContext_whenAssessed() {
    when(projectAssessmentService.hasProjectBeenAssessed(projectDetail)).thenReturn(true);

    projectAssessmentContextService.buildProjectAssessmentContext(
        projectDetail,
        authenticatedUser,
        projectStatuses,
        projectPermissions,
        allowedProjectTypes,
        allowProjectContributors
    );
  }

  @Test
  public void getProjectDetailsOrError() {
    when(projectContextService.getProjectDetailsOrError(projectDetail.getId(), ProjectDetailVersionType.CURRENT_VERSION)).thenReturn(projectDetail);

    assertThat(projectAssessmentContextService.getProjectDetailsOrError(projectDetail.getId(), ProjectDetailVersionType.CURRENT_VERSION)).isEqualTo(projectDetail);
  }

  private static class TestController {}
}
