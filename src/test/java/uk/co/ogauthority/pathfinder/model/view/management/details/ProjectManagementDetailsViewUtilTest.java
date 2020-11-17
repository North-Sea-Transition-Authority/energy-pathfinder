package uk.co.ogauthority.pathfinder.model.view.management.details;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementDetailsViewUtilTest {

  private static final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private static void checkCommonFields(ProjectManagementDetailsView projectManagementDetailsView,
                                        ProjectInformation projectInformation,
                                        AuthenticatedUserAccount submitterAccount) {
    assertThat(projectManagementDetailsView.getFieldStage()).isEqualTo(
        projectInformation.getFieldStage().getDisplayName());
    assertThat(projectManagementDetailsView.getVersion()).isEqualTo(projectDetail.getVersion());
    assertThat(projectManagementDetailsView.getSubmissionDate()).isEqualTo(
        DateUtil.formatInstant(projectDetail.getSubmittedInstant()));
    assertThat(projectManagementDetailsView.getSubmittedByUser()).isEqualTo(submitterAccount.getFullName());
    assertThat(projectManagementDetailsView.getSubmittedByUserEmail()).isEqualTo(submitterAccount.getEmailAddress());
  }

  @Test
  public void from_withFieldFromList() {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    var projectLocation = ProjectLocationTestUtil.getProjectLocation_withField(projectDetail);
    var submitterAccount = UserTestingUtil.getAuthenticatedUserAccount();

    var projectManagementDetailsView = ProjectManagementDetailsViewUtil.from(
        projectDetail,
        projectInformation,
        projectLocation,
        submitterAccount
    );

    checkCommonFields(projectManagementDetailsView, projectInformation, submitterAccount);
    assertThat(projectManagementDetailsView.getField()).isEqualTo(projectLocation.getField().getFieldName());
  }

  @Test
  public void from_withManualField() {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    var projectLocation = ProjectLocationTestUtil.getProjectLocation_withManualField(projectDetail);
    var submitterAccount = UserTestingUtil.getAuthenticatedUserAccount();

    var projectManagementDetailsView = ProjectManagementDetailsViewUtil.from(
        projectDetail,
        projectInformation,
        projectLocation,
        submitterAccount
    );

    checkCommonFields(projectManagementDetailsView, projectInformation, submitterAccount);
    assertThat(projectManagementDetailsView.getField()).isEqualTo(projectLocation.getManualFieldName());
  }
}
