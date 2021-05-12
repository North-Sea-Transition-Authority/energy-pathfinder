package uk.co.ogauthority.pathfinder.model.view.projectmanagement.details;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.service.projectmanagement.details.TestProjectManagementDetailView;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementDetailViewUtilTest {

  @Test
  public void setProjectManagementDetailViewCommonFields_assertProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var userAccount = UserTestingUtil.getAuthenticatedUserAccount();

    final var projectDetailView = new TestProjectManagementDetailView();

    ProjectManagementDetailViewUtil.setProjectManagementDetailViewCommonFields(
        projectDetailView,
        projectDetail,
        userAccount
    );

    assertThat(projectDetailView.getStatus()).isEqualTo(projectDetail.getStatus().getDisplayName());
    assertThat(projectDetailView.getSubmissionDate()).isEqualTo(
        DateUtil.formatInstant(projectDetail.getSubmittedInstant())
    );
    assertThat(projectDetailView.getSubmittedByUser()).isEqualTo(userAccount.getFullName());
    assertThat(projectDetailView.getSubmittedByUserEmail()).isEqualTo(userAccount.getEmailAddress());

  }
}
