package uk.co.ogauthority.pathfinder.service.projectcontext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
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

  @Before
  public void setUp() throws Exception {
    projectContextService = new ProjectContextService(
        projectService,
        projectOperatorService
    );
  }

  @Test
  public void getProjectContext() {

    var context = projectContextService.getProjectContext(detail, authenticatedUser);
    assertThat(context.getProjectDetails()).isEqualTo(detail);
    assertThat(context.getUserAccount()).isEqualTo(authenticatedUser);
    assertThat(context.getProjectPermissions()).containsExactlyInAnyOrder(
        ProjectPermission.VIEW_TASK_LIST,
        ProjectPermission.EDIT,
        ProjectPermission.SUBMIT
    );
  }

  @Test
  public void projectStatusMatches_whenMatch() {
    assertThat(projectContextService.projectStatusMatches(detail, ProjectStatus.DRAFT)).isTrue();
  }

  @Test
  public void projectStatusMatches_whenNotMatched() {
    assertThat(projectContextService.projectStatusMatches(detail, ProjectStatus.QA)).isFalse();
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
        ProjectStatus.DRAFT
        );
    assertThat(context.getProjectDetails()).isEqualTo(detail);
    assertThat(context.getUserAccount()).isEqualTo(authenticatedUser);
    assertThat(context.getProjectPermissions()).containsExactlyInAnyOrder(
        ProjectPermission.VIEW_TASK_LIST,
        ProjectPermission.EDIT,
        ProjectPermission.SUBMIT
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenNoAccess() {
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(false);
    projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        ProjectStatus.DRAFT
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenWrongStatus() {
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);
    projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        ProjectStatus.QA
    );
  }
}
