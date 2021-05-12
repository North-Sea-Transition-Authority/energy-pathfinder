package uk.co.ogauthority.pathfinder.service.projectmanagement.heading.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanManagementHeadingSectionServiceTest {

  @Mock
  private ProjectOperatorService projectOperatorService;

  private ForwardWorkPlanManagementHeadingSectionService forwardWorkPlanManagementHeadingSectionService;

  @Before
  public void setup() {
    forwardWorkPlanManagementHeadingSectionService = new ForwardWorkPlanManagementHeadingSectionService(
        projectOperatorService
    );
  }

  @Test
  public void getSupportedProjectType_assertForwardWorkPlan() {
    assertThat(forwardWorkPlanManagementHeadingSectionService.getSupportedProjectType()).isEqualTo(ProjectType.FORWARD_WORK_PLAN);
  }

  @Test
  public void getHeadingText_assertCorrectHeadingText() {

    final var expectedHeadingText = ProjectType.FORWARD_WORK_PLAN.getDisplayName();

    final var resultingHeadingText = forwardWorkPlanManagementHeadingSectionService.getHeadingText(
        ProjectUtil.getProjectDetails()
    );

    assertThat(resultingHeadingText).isEqualTo(expectedHeadingText);
  }

  @Test
  public void getCaptionText_assertCorrectCaptionText() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var projectOperator = ProjectOperatorTestUtil.getOperator();

    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(projectOperator);

    final var resultingCaptionText = forwardWorkPlanManagementHeadingSectionService.getCaptionText(projectDetail);

    assertThat(resultingCaptionText).isEqualTo(projectOperator.getOrganisationGroup().getName());

  }

}