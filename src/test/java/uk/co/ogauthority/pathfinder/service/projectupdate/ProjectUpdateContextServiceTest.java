package uk.co.ogauthority.pathfinder.service.projectupdate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectUpdateContextServiceTest {

  @Mock
  private ProjectContextService projectContextService;

  @Mock
  private ProjectUpdateService projectUpdateService;

  private ProjectUpdateContextService projectUpdateContextService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();
  private final Set<ProjectStatus> projectStatuses = Set.of(ProjectStatus.QA);
  private final Set<ProjectPermission> projectPermissions = Set.of(ProjectPermission.PROVIDE_ASSESSMENT);

  @Before
  public void setup() {
    projectUpdateContextService = new ProjectUpdateContextService(
        projectContextService,
        projectUpdateService
    );

    when(projectContextService.buildProjectContext(any(), any(), any(), any()))
        .thenAnswer(invocation -> new ProjectContext(invocation.getArgument(0), invocation.getArgument(3), invocation.getArgument(1)));

    when(projectContextService.getProjectStatusesForClass(TestController.class)).thenReturn(projectStatuses);
    when(projectContextService.getProjectPermissionsForClass(TestController.class)).thenReturn(projectPermissions);
  }

  @Test
  public void canBuildContext_whenUpdateNotInProgress() {
    when(projectUpdateService.isUpdateInProgress(projectDetail.getProject())).thenReturn(false);

    var result = projectUpdateContextService.canBuildContext(
        projectDetail,
        authenticatedUser,
        TestController.class
    );

    assertThat(result).isTrue();

    verify(projectContextService, times(1)).buildProjectContext(projectDetail, authenticatedUser, projectStatuses, projectPermissions);
  }

  @Test
  public void canBuildContext_whenUpdateInProgress() {
    when(projectUpdateService.isUpdateInProgress(projectDetail.getProject())).thenReturn(true);

    var result = projectUpdateContextService.canBuildContext(
        projectDetail,
        authenticatedUser,
        TestController.class
    );

    assertThat(result).isFalse();

    verify(projectContextService, times(0)).buildProjectContext(projectDetail, authenticatedUser, projectStatuses, projectPermissions);
  }

  @Test
  public void buildProjectUpdateContext_whenUpdateNotInProgress() {
    when(projectUpdateService.isUpdateInProgress(projectDetail.getProject())).thenReturn(false);

    var projectAssessmentContext = projectUpdateContextService.buildProjectUpdateContext(
        projectDetail,
        authenticatedUser,
        projectStatuses,
        projectPermissions
    );

    assertThat(projectAssessmentContext.getProjectDetails()).isEqualTo(projectDetail);
    assertThat(projectAssessmentContext.getProjectPermissions()).isEqualTo(projectPermissions);
    assertThat(projectAssessmentContext.getUserAccount()).isEqualTo(authenticatedUser);
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectUpdateContext_whenUpdateInProgress() {
    when(projectUpdateService.isUpdateInProgress(projectDetail.getProject())).thenReturn(true);

    projectUpdateContextService.buildProjectUpdateContext(
        projectDetail,
        authenticatedUser,
        projectStatuses,
        projectPermissions
    );
  }

  @Test
  public void getProjectDetailsOrError_whenPresent_thenReturn() {
    when(projectContextService.getProjectDetailsOrError(projectDetail.getId())).thenReturn(projectDetail);

    assertThat(projectContextService.getProjectDetailsOrError(projectDetail.getId())).isEqualTo(projectDetail);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getProjectDetailsOrError_whenNotPresent_thenError() {
    when(projectContextService.getProjectDetailsOrError(projectDetail.getId())).thenThrow(
        new PathfinderEntityNotFoundException("")
    );

    projectContextService.getProjectDetailsOrError(projectDetail.getId());
  }

  private static class TestController {}
}
