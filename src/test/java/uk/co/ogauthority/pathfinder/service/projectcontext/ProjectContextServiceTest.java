package uk.co.ogauthority.pathfinder.service.projectcontext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectDetailVersionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectContextServiceTest {

  @Mock
  private ProjectService projectService;

  @Mock
  private ProjectOperatorService projectOperatorService;

  private ProjectContextService projectContextService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);
  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  private final Set<ProjectPermission> projectPermissions = Set.of(ProjectPermission.EDIT, ProjectPermission.SUBMIT);

  @Before
  public void setUp() throws Exception {
    projectContextService = new ProjectContextService(
        projectService,
        projectOperatorService
    );
    detail.setStatus(ProjectStatus.DRAFT);
  }

  @Test
  public void getProjectContext() {

    var context = projectContextService.getProjectContext(detail, authenticatedUser, projectPermissions);
    assertThat(context.getProjectDetails()).isEqualTo(detail);
    assertThat(context.getUserAccount()).isEqualTo(authenticatedUser);
    assertThat(context.getProjectPermissions()).containsExactlyInAnyOrder(
        ProjectPermission.EDIT,
        ProjectPermission.SUBMIT
    );
  }

  @Test
  public void projectStatusMatches_whenMatch() {
    assertThat(projectContextService.projectStatusMatches(detail, Set.of(ProjectStatus.DRAFT))).isTrue();
  }

  @Test
  public void projectStatusMatches_whenNotMatched() {
    assertThat(projectContextService.projectStatusMatches(detail, Set.of(ProjectStatus.QA))).isFalse();
  }

  @Test
  public void getProjectDetailsOrError_whenCurrentVersionAndPresent() {
    when(projectService.getLatestDetailOrError(any())).thenReturn(detail);
    assertThat(projectContextService.getProjectDetailsOrError(detail.getId(), ProjectDetailVersionType.CURRENT_VERSION)).isEqualTo(detail);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getProjectDetailsOrError_whenCurrentVersionAndNotPresent() {
    doThrow(new PathfinderEntityNotFoundException("test")).when(projectService).getLatestDetailOrError(any());
    projectContextService.getProjectDetailsOrError(detail.getId(), ProjectDetailVersionType.CURRENT_VERSION);
  }

  @Test
  public void getProjectDetailsOrError_whenLatestSubmittedVersionAndPresent() {
    when(projectService.getLatestSubmittedDetailOrError(any())).thenReturn(detail);
    assertThat(projectContextService.getProjectDetailsOrError(detail.getId(), ProjectDetailVersionType.LATEST_SUBMITTED_VERSION)).isEqualTo(detail);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getProjectDetailsOrError_whenLatestSubmittedVersionAndNotPresent() {
    doThrow(new PathfinderEntityNotFoundException("test")).when(projectService).getLatestSubmittedDetailOrError(any());
    projectContextService.getProjectDetailsOrError(detail.getId(), ProjectDetailVersionType.LATEST_SUBMITTED_VERSION);
  }

  @Test
  public void canBuildContext_whenNoException() {
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);

    assertThat(projectContextService.canBuildContext(detail, authenticatedUser, TestController.class)).isTrue();
  }

  @Test
  public void canBuildContext_whenException() {
    assertThat(projectContextService.canBuildContext(detail, authenticatedUser, TestController.class)).isFalse();
  }

  @Test
  public void buildProjectContext_whenHasAccess() {
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);
    var context = projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.DRAFT),
        projectPermissions
        );
    assertThat(context.getProjectDetails()).isEqualTo(detail);
    assertThat(context.getUserAccount()).isEqualTo(authenticatedUser);
    assertThat(context.getProjectPermissions()).containsExactlyInAnyOrder(
        ProjectPermission.EDIT,
        ProjectPermission.SUBMIT,
        ProjectPermission.PROVIDE_UPDATE,
        ProjectPermission.ARCHIVE
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenNoAccess() {
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(false);
    projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.DRAFT),
        projectPermissions
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenWrongStatus() {
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);
    projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.QA),
        projectPermissions
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenNoPermissions() {
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, unAuthenticatedUser)).thenReturn(true);
    var context = projectContextService.buildProjectContext(
        detail,
        unAuthenticatedUser,
        Set.of(ProjectStatus.DRAFT),
        projectPermissions
    );
  }

  @Test
  public void getUserProjectPermissions_withMatch() {
    assertThat(projectContextService.getUserProjectPermissions(authenticatedUser)).containsExactlyInAnyOrder(
        ProjectPermission.EDIT,
        ProjectPermission.SUBMIT,
        ProjectPermission.PROVIDE_UPDATE,
        ProjectPermission.ARCHIVE
    );
  }

  @Test
  public void getUserProjectPermissions_withNoMatch() {
    assertThat(projectContextService.getUserProjectPermissions(unAuthenticatedUser)).isEmpty();
  }

  @Test
  public void getProjectStatusesForClass_whenNoStatuses() {
    assertThat(projectContextService.getProjectStatusesForClass(Object.class)).isEmpty();
  }

  @Test
  public void getProjectStatusesForClass_whenStatuses() {
    var statuses = projectContextService.getProjectStatusesForClass(TestClassWithContextAnnotations.class);

    assertThat(statuses).containsExactly(
        ProjectStatus.QA
    );
  }

  @Test
  public void getProjectPermissionsForClass_whenNoPermissions() {
    assertThat(projectContextService.getProjectPermissionsForClass(Object.class)).isEmpty();
  }

  @Test
  public void getProjectPermissionsForClass_whenPermissions() {
    var statuses = projectContextService.getProjectPermissionsForClass(TestClassWithContextAnnotations.class);

    assertThat(statuses).containsExactly(
        ProjectPermission.PROVIDE_ASSESSMENT
    );
  }

  @ProjectStatusCheck(status = ProjectStatus.DRAFT)
  @ProjectFormPagePermissionCheck
  private static final class TestController {}
}
