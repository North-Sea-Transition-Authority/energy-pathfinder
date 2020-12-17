package uk.co.ogauthority.pathfinder.model.view.projectassessment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.projectassessment.ProjectAssessment;
import uk.co.ogauthority.pathfinder.model.enums.projectassessment.ProjectQuality;
import uk.co.ogauthority.pathfinder.testutil.ProjectAssessmentTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectAssessmentViewUtilTest {

  @Test
  public void from_withNotNullProjectQuality() {
    var projectAssessment = ProjectAssessmentTestUtil.createProjectAssessment();
    projectAssessment.setProjectQuality(ProjectQuality.GOOD);

    var assessor = UserTestingUtil.getAuthenticatedUserAccount();

    var projectAssessmentView = ProjectAssessmentViewUtil.from(projectAssessment, assessor);

    checkCommonFields(projectAssessment, assessor, projectAssessmentView);

    assertThat(projectAssessmentView.getProjectQuality()).isEqualTo(projectAssessment.getProjectQuality().getDisplayName());
  }

  @Test
  public void from_withNullProjectQuality() {
    var projectAssessment = ProjectAssessmentTestUtil.createProjectAssessment();
    projectAssessment.setProjectQuality(null);

    var assessor = UserTestingUtil.getAuthenticatedUserAccount();

    var projectAssessmentView = ProjectAssessmentViewUtil.from(projectAssessment, assessor);

    checkCommonFields(projectAssessment, assessor, projectAssessmentView);

    assertThat(projectAssessmentView.getProjectQuality()).isNull();
  }

  @Test
  public void from_whenReadyToBePublished() {
    var projectAssessment = ProjectAssessmentTestUtil.createProjectAssessment();
    projectAssessment.setReadyToBePublished(true);

    var assessor = UserTestingUtil.getAuthenticatedUserAccount();

    var projectAssessmentView = ProjectAssessmentViewUtil.from(projectAssessment, assessor);

    checkCommonFields(projectAssessment, assessor, projectAssessmentView);

    assertThat(projectAssessmentView.getUpdateRequired()).isEqualTo(projectAssessment.getUpdateRequired());
  }

  @Test
  public void from_whenNotReadyToBePublished() {
    var projectAssessment = ProjectAssessmentTestUtil.createProjectAssessment();
    projectAssessment.setReadyToBePublished(false);

    var assessor = UserTestingUtil.getAuthenticatedUserAccount();

    var projectAssessmentView = ProjectAssessmentViewUtil.from(projectAssessment, assessor);

    checkCommonFields(projectAssessment, assessor, projectAssessmentView);

    assertThat(projectAssessmentView.getUpdateRequired()).isNull();
  }

  private void checkCommonFields(ProjectAssessment projectAssessment, AuthenticatedUserAccount assessor, ProjectAssessmentView projectAssessmentView) {
    assertThat(projectAssessmentView.getReadyToBePublished()).isEqualTo(projectAssessment.getReadyToBePublished());
    assertThat(projectAssessmentView.getAssessmentDate()).isEqualTo(DateUtil.formatInstant(projectAssessment.getAssessedInstant()));
    assertThat(projectAssessmentView.getAssessedByUser()).isEqualTo(assessor.getFullName());
  }
}
