package uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanManagementDetailViewUtilTest {

  @Test
  public void from_assertProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var submitterUserAccount = UserTestingUtil.getAuthenticatedUserAccount();

    final var workPlanManagementDetailView = ForwardWorkPlanManagementDetailViewUtil.from(
        projectDetail,
        submitterUserAccount
    );

    assertThat(workPlanManagementDetailView.getStatus()).isEqualTo(projectDetail.getStatus().getDisplayName());
    assertThat(workPlanManagementDetailView.getSubmissionDate()).isEqualTo(
        DateUtil.formatInstant(projectDetail.getSubmittedInstant())
    );
    assertThat(workPlanManagementDetailView.getSubmittedByUser()).isEqualTo(submitterUserAccount.getFullName());
    assertThat(workPlanManagementDetailView.getSubmittedByUserEmail()).isEqualTo(submitterUserAccount.getEmailAddress());

  }

}