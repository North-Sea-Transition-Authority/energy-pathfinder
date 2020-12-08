package uk.co.ogauthority.pathfinder.service.projectcontext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
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
  public void getProjectDetailsOrError_whenPresent() {
    when(projectService.getLatestDetail(any())).thenReturn(Optional.of(detail));
    assertThat(projectContextService.getProjectDetailsOrError(detail.getId())).isEqualTo(detail);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getProjectDetailsOrError_whenNotPresent() {
    when(projectService.getLatestDetail(any())).thenReturn(Optional.empty());
    projectContextService.getProjectDetailsOrError(detail.getId());
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
        ProjectPermission.PROVIDE_UPDATE
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
        ProjectPermission.PROVIDE_UPDATE
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
}
