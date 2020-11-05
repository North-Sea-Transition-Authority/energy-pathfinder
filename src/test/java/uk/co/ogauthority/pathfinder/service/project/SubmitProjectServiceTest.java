package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class SubmitProjectServiceTest {

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  private SubmitProjectService submitProjectService;

  @Before
  public void setup() {
    submitProjectService = new SubmitProjectService(projectDetailsRepository);
  }

  @Test
  public void submitProject() {
    var projectDetail = ProjectUtil.getProjectDetails();
    var authenticatedUserAccount = UserTestingUtil.getAuthenticatedUserAccount();

    submitProjectService.submitProject(projectDetail, authenticatedUserAccount);

    assertThat(projectDetail.getStatus()).isEqualTo(ProjectStatus.QA);
    assertThat(projectDetail.getSubmittedByWua()).isEqualTo(authenticatedUserAccount.getWuaId());
    assertThat(projectDetail.getSubmittedInstant()).isNotNull();

    verify(projectDetailsRepository, times(1)).save(projectDetail);
  }
}
