package uk.co.ogauthority.pathfinder.model.view.projectmanagement.details;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.testutil.DevUkTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureProjectManagementDetailViewUtilTest {

  private static final ProjectDetail PROJECT_DETAIL = ProjectUtil.getProjectDetails();

  private static void checkCommonFields(InfrastructureProjectManagementDetailView projectManagementDetailView,
                                        FieldStage fieldStage,
                                        boolean isEnergyTransitionProject,
                                        AuthenticatedUserAccount submitterAccount
  ) {
    assertThat(projectManagementDetailView.getFieldStage()).isEqualTo(fieldStage.getDisplayName());
    assertThat(projectManagementDetailView.getStatus()).isEqualTo(PROJECT_DETAIL.getStatus().getDisplayName());
    assertThat(projectManagementDetailView.getSubmissionDate()).isEqualTo(
        DateUtil.formatInstant(PROJECT_DETAIL.getSubmittedInstant())
    );
    assertThat(projectManagementDetailView.getIsEnergyTransitionProject()).isEqualTo(isEnergyTransitionProject);
    assertThat(projectManagementDetailView.getSubmittedByUser()).isEqualTo(submitterAccount.getFullName());
    assertThat(projectManagementDetailView.getSubmittedByUserEmail()).isEqualTo(submitterAccount.getEmailAddress());
  }

  @Test
  public void from_withFieldFromList() {

    final var devUkField = DevUkTestUtil.getDevUkField();

    final var fieldStage = FieldStage.DISCOVERY;
    final var submitterAccount = UserTestingUtil.getAuthenticatedUserAccount();
    final var isEnergyTransitionProject = false;

    var projectManagementDetailView = InfrastructureProjectManagementDetailViewUtil.from(
        PROJECT_DETAIL,
        fieldStage,
        devUkField,
        isEnergyTransitionProject,
        submitterAccount
    );

    checkCommonFields(projectManagementDetailView, fieldStage, isEnergyTransitionProject, submitterAccount);
    assertThat(projectManagementDetailView.getField()).isEqualTo(devUkField.getFieldName());
  }

  @Test
  public void from_withNullField() {

    final DevUkField devUkField = null;

    final var fieldStage = FieldStage.DECOMMISSIONING;
    final var submitterAccount = UserTestingUtil.getAuthenticatedUserAccount();
    final var isEnergyTransitionProject = true;

    var projectManagementDetailView = InfrastructureProjectManagementDetailViewUtil.from(
        PROJECT_DETAIL,
        fieldStage,
        devUkField,
        isEnergyTransitionProject,
        submitterAccount
    );

    checkCommonFields(projectManagementDetailView, fieldStage, isEnergyTransitionProject, submitterAccount);
    assertThat(projectManagementDetailView.getField()).isNull();
  }
}
