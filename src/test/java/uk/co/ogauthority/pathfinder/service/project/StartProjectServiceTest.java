package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectRepository;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class StartProjectServiceTest {

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  @Mock
  private ProjectOperatorService projectOperatorService;

  private StartProjectService startProjectService;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  @Before
  public void setUp() throws Exception {
    startProjectService = new StartProjectService(
        projectRepository,
        projectDetailsRepository,
        projectOperatorService
    );
  }

  @Test
  public void startProject_correctCalls() {
    var projectDetails = startProjectService.startProject(authenticatedUser);
    verify(projectRepository, times(1)).save(any());
    verify(projectDetailsRepository, times(1)).save(any());
    verify(projectOperatorService, times(1)).createProjectOperator(projectDetails, authenticatedUser);
  }

  @Test
  public void startProject_correctDetails() {
    var projectDetails = startProjectService.startProject(authenticatedUser);
    assertThat(projectDetails.getCreatedByWua()).isEqualTo(authenticatedUser.getWuaId());
    assertThat(projectDetails.getStatus()).isEqualTo(ProjectStatus.DRAFT);
    assertThat(projectDetails.getVersion()).isEqualTo(1);
    assertThat(projectDetails.getIsCurrentVersion()).isTrue();
  }
}
