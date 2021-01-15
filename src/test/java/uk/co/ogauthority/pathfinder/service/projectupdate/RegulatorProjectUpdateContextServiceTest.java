package uk.co.ogauthority.pathfinder.service.projectupdate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class RegulatorProjectUpdateContextServiceTest {

  @Mock
  private ProjectContextService projectContextService;

  @Mock
  private ProjectUpdateService projectUpdateService;

  @Mock
  private ProjectService projectService;

  @Mock
  private RegulatorUpdateRequestService regulatorUpdateRequestService;

  private RegulatorProjectUpdateContextService regulatorProjectUpdateContextService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();
  private final Set<ProjectStatus> projectStatuses = Set.of(ProjectStatus.QA);
  private final Set<ProjectPermission> projectPermissions = Set.of(ProjectPermission.REQUEST_UPDATE);

  @Before
  public void setup() {
    regulatorProjectUpdateContextService = new RegulatorProjectUpdateContextService(
        projectContextService,
        projectUpdateService,
        projectService,
        regulatorUpdateRequestService
    );

    when(projectService.getLatestSubmittedDetailOrError(projectDetail.getProject().getId())).thenReturn(projectDetail);

    when(projectContextService.buildProjectContext(any(), any(), any(), any()))
        .thenAnswer(invocation -> new ProjectContext(invocation.getArgument(0), invocation.getArgument(3), invocation.getArgument(1)));
  }

  @Test
  public void buildProjectUpdateContext_whenUpdateNotRequested() {
    when(regulatorUpdateRequestService.hasUpdateBeenRequested(projectDetail)).thenReturn(false);

    var regulatorProjectUpdateContext = regulatorProjectUpdateContextService.buildProjectUpdateContext(
        projectDetail,
        authenticatedUser,
        projectStatuses,
        projectPermissions
    );

    assertThat(regulatorProjectUpdateContext.getProjectDetails()).isEqualTo(projectDetail);
    assertThat(regulatorProjectUpdateContext.getProjectPermissions()).isEqualTo(projectPermissions);
    assertThat(regulatorProjectUpdateContext.getUserAccount()).isEqualTo(authenticatedUser);
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectUpdateContext_whenUpdateRequested() {
    when(regulatorUpdateRequestService.hasUpdateBeenRequested(projectDetail)).thenReturn(true);

    regulatorProjectUpdateContextService.buildProjectUpdateContext(
        projectDetail,
        authenticatedUser,
        projectStatuses,
        projectPermissions
    );
  }
}
