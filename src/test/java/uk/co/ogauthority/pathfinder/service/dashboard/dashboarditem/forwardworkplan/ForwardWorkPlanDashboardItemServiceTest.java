package uk.co.ogauthority.pathfinder.service.dashboard.dashboarditem.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.dashboard.infrastructure.InfrastructureProjectDashboardItemViewUtil;
import uk.co.ogauthority.pathfinder.testutil.DashboardProjectItemTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanDashboardItemServiceTest {

  private ForwardWorkPlanDashboardItemService forwardWorkPlanDashboardItemService;

  @Before
  public void setup() {
    forwardWorkPlanDashboardItemService = new ForwardWorkPlanDashboardItemService();
  }

  @Test
  public void getSupportedProjectType_assertForwardWorkPlan() {
    final var supportedProjectType = forwardWorkPlanDashboardItemService.getSupportedProjectType();
    assertThat(supportedProjectType).isEqualTo(ProjectType.FORWARD_WORK_PLAN);
  }

  @Test
  public void getDashboardProjectItemView_assertExpectedViewReturned() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();

    final var expectedDashboardItemView = InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem);

    final var resultingDashboardItemView = forwardWorkPlanDashboardItemService.getDashboardProjectItemView(dashboardProjectItem);

    assertThat(resultingDashboardItemView).isEqualTo(expectedDashboardItemView);
  }

  @Test
  public void getTemplatePath() {
    final var resultingTemplatePath = forwardWorkPlanDashboardItemService.getTemplatePath();
    assertThat(resultingTemplatePath).isEqualTo(ForwardWorkPlanDashboardItemService.TEMPLATE_PATH);
  }

  @Test
  public void getTemplateModel_verifyModelProperties() {
    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    final var resultingModel = forwardWorkPlanDashboardItemService.getTemplateModel(dashboardProjectItem);

    assertThat(resultingModel).containsExactly(
        entry("infrastructureDashboardItem", InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem))
    );
  }

}