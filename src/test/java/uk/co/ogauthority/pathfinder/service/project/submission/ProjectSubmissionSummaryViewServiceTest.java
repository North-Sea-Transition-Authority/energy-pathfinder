package uk.co.ogauthority.pathfinder.service.project.submission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.exception.UnsupportedProjectSubmissionSummaryServiceException;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.submission.ProjectNoUpdateSubmissionSummaryView;
import uk.co.ogauthority.pathfinder.model.view.submission.ProjectSubmissionSummaryView;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSubmissionSummaryViewServiceTest {

  @Mock
  private TestProjectSubmissionSummaryService testProjectSubmissionSummaryService;

  private ProjectSubmissionSummaryViewService projectSubmissionSummaryViewService;

  @Before
  public void setup() {
    projectSubmissionSummaryViewService = new ProjectSubmissionSummaryViewService(
        List.of(testProjectSubmissionSummaryService)
    );
  }

  @Test(expected = UnsupportedProjectSubmissionSummaryServiceException.class)
  public void getProjectSubmissionSummaryView_whenNoSupportedImplemented_thenException() {

    final var supportedProjectType = ProjectType.FORWARD_WORK_PLAN;
    final var unsupportedProjectType = ProjectType.INFRASTRUCTURE;

    final var projectDetails = ProjectUtil.getProjectDetails();
    projectDetails.setProjectType(unsupportedProjectType);

    when(testProjectSubmissionSummaryService.getSupportedProjectType()).thenReturn(supportedProjectType);
    projectSubmissionSummaryViewService.getProjectSubmissionSummaryView(projectDetails);
  }

  @Test
  public void getProjectSubmissionSummaryView_whenSupported_thenAssertSummaryViewProperties() {

    final var supportedProjectType = ProjectType.FORWARD_WORK_PLAN;

    final var projectDetails = ProjectUtil.getProjectDetails();
    projectDetails.setProjectType(supportedProjectType);

    when(testProjectSubmissionSummaryService.getSupportedProjectType()).thenReturn(supportedProjectType);

    var expectedSummaryView = new ProjectSubmissionSummaryView(
        "displayName",
        "submission timestamp",
        "john"
    );
    when(testProjectSubmissionSummaryService.getSubmissionSummaryView(projectDetails)).thenReturn(expectedSummaryView);

    final var submissionSummaryView = projectSubmissionSummaryViewService.getProjectSubmissionSummaryView(projectDetails);

    assertThat(submissionSummaryView).isEqualTo(expectedSummaryView);
  }

  @Test(expected = UnsupportedProjectSubmissionSummaryServiceException.class)
  public void getProjectNoUpdateSubmissionSummaryView_whenNoSupportedImplemented_thenException() {

    final var supportedProjectType = ProjectType.FORWARD_WORK_PLAN;
    final var unsupportedProjectType = ProjectType.INFRASTRUCTURE;

    final var projectDetails = ProjectUtil.getProjectDetails();
    projectDetails.setProjectType(unsupportedProjectType);

    when(testProjectSubmissionSummaryService.getSupportedProjectType()).thenReturn(supportedProjectType);
    projectSubmissionSummaryViewService.getProjectNoUpdateSubmissionSummaryView(projectDetails);
  }

  @Test
  public void getProjectNoUpdateSubmissionSummaryView_whenSupported_thenAssertSummaryViewProperties() {

    final var supportedProjectType = ProjectType.FORWARD_WORK_PLAN;

    final var projectDetails = ProjectUtil.getProjectDetails();
    projectDetails.setProjectType(supportedProjectType);

    when(testProjectSubmissionSummaryService.getSupportedProjectType()).thenReturn(supportedProjectType);

    var expectedSummaryView = new ProjectNoUpdateSubmissionSummaryView(
        "displayName",
        "submission timestamp",
        "john"
    );
    when(testProjectSubmissionSummaryService.getNoUpdateSubmissionSummaryView(projectDetails)).thenReturn(expectedSummaryView);

    final var submissionSummaryView = projectSubmissionSummaryViewService.getProjectNoUpdateSubmissionSummaryView(projectDetails);

    assertThat(submissionSummaryView).isEqualTo(expectedSummaryView);
  }
}
