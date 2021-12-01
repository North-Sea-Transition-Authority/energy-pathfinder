package uk.co.ogauthority.pathfinder.service.dashboard.dashboarditem.infrastructure;

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
public class InfrastructureDashboardItemServiceTest {

  private InfrastructureDashboardItemService infrastructureDashboardItemService;

  @Before
  public void setup() {
    infrastructureDashboardItemService = new InfrastructureDashboardItemService();
  }

  @Test
  public void getSupportedProjectType_assertInfrastructure() {
    final var supportedProjectType = infrastructureDashboardItemService.getSupportedProjectType();
    assertThat(supportedProjectType).isEqualTo(ProjectType.INFRASTRUCTURE);
  }

  @Test
  public void getDashboardProjectItemView_assertExpectedViewReturned() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();

    final var expectedDashboardItemView = InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem);

    final var resultingDashboardItemView = infrastructureDashboardItemService.getDashboardProjectItemView(dashboardProjectItem);

    assertThat(resultingDashboardItemView).isEqualTo(expectedDashboardItemView);
  }

  @Test
  public void getTemplatePath() {
    final var resultingTemplatePath = infrastructureDashboardItemService.getTemplatePath();
    assertThat(resultingTemplatePath).isEqualTo(InfrastructureDashboardItemService.TEMPLATE_PATH);
  }

  @Test
  public void getTemplateModel_verifyModelProperties() {
    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    final var resultingModel = infrastructureDashboardItemService.getTemplateModel(dashboardProjectItem);

    assertThat(resultingModel).containsExactly(
        entry("infrastructureDashboardItem", InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem))
    );
  }
}