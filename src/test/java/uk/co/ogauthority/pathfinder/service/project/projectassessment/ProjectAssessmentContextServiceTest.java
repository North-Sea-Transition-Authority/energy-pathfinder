package uk.co.ogauthority.pathfinder.service.project.projectassessment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.ProjectAssessmentTestUtil;
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

  @Before
  public void setup() {
    projectAssessmentContextService = new ProjectAssessmentContextService(
        projectContextService,
        projectAssessmentService
    );

    when(projectContextService.buildProjectContext(projectDetail, authenticatedUser, projectStatuses, projectPermissions)).thenReturn(
        new ProjectContext(projectDetail, projectPermissions, authenticatedUser)
    );
  }

  @Test
  public void buildProjectAssessmentContext_whenNotAssessed() {
    var projectAssessmentContext = projectAssessmentContextService.buildProjectAssessmentContext(
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
  public void buildProjectAssessmentContext_whenAssessed() {
    when(projectAssessmentService.hasProjectBeenAssessed(projectDetail)).thenReturn(true);

    projectAssessmentContextService.buildProjectAssessmentContext(
        projectDetail,
        authenticatedUser,
        projectStatuses,
        projectPermissions
    );
  }
}
