package uk.co.ogauthority.pathfinder.service.dashboard.dashboarditem.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.controller.project.start.infrastructure.InfrastructureProjectStartController;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.dashboard.forwardworkplan.ForwardWorkPlanDashboardItemViewUtil;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.DashboardProjectItemTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanDashboardItemServiceTest {

  @Mock
  private ServiceProperties serviceProperties;

  private ForwardWorkPlanDashboardItemService forwardWorkPlanDashboardItemService;

  @Before
  public void setup() {
    forwardWorkPlanDashboardItemService = new ForwardWorkPlanDashboardItemService(
        serviceProperties
    );
  }

  @Test
  public void getSupportedProjectType_assertForwardWorkPlan() {
    final var supportedProjectType = forwardWorkPlanDashboardItemService.getSupportedProjectType();
    assertThat(supportedProjectType).isEqualTo(ProjectType.FORWARD_WORK_PLAN);
  }

  @Test
  public void getDashboardProjectItemView_assertExpectedViewReturned() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();

    final var expectedDashboardItemView = ForwardWorkPlanDashboardItemViewUtil.from(dashboardProjectItem);

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
        entry("forwardWorkPlanDashboardItem", ForwardWorkPlanDashboardItemViewUtil.from(dashboardProjectItem)),
        entry("service", serviceProperties),
        entry("infrastructureProjectLowerCaseDisplayName", ProjectType.INFRASTRUCTURE.getLowercaseDisplayName()),
        entry("forwardWorkPlanProjectLowerCaseDisplayName", ProjectType.FORWARD_WORK_PLAN.getLowercaseDisplayName()),
        entry("startInfrastructureProjectUrl", ReverseRouter.route(on(InfrastructureProjectStartController.class).startPage(null)))
    );
  }

}