package uk.co.ogauthority.pathfinder.model.view.projectassessment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.testutil.ProjectAssessmentTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectAssessmentViewUtilTest {

  @Test
  public void from() {
    var projectAssessment = ProjectAssessmentTestUtil.createProjectAssessment();
    var assessor = UserTestingUtil.getAuthenticatedUserAccount();

    var projectAssessmentView = ProjectAssessmentViewUtil.from(projectAssessment, assessor);

    assertThat(projectAssessmentView.getProjectQuality()).isEqualTo(projectAssessment.getProjectQuality().getDisplayName());
    assertThat(projectAssessmentView.isReadyToBePublished()).isEqualTo(projectAssessment.getReadyToBePublished());
    assertThat(projectAssessmentView.isUpdateRequired()).isEqualTo(projectAssessment.getUpdateRequired());
    assertThat(projectAssessmentView.getAssessmentDate()).isEqualTo(DateUtil.formatInstant(projectAssessment.getAssessedInstant()));
    assertThat(projectAssessmentView.getAssessedByUser()).isEqualTo(assessor.getFullName());
  }
}
