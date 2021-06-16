package uk.co.ogauthority.pathfinder.model.view.dashboard.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.testutil.DashboardProjectItemTestUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanDashboardItemViewUtilTest {

  @Test
  public void from_whenAllPropertiesPopulated() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();

    final var dashboardProjectItemView = ForwardWorkPlanDashboardItemViewUtil.from(dashboardProjectItem);

    assertCommonViewProperties(dashboardProjectItemView, dashboardProjectItem);
  }

  @Test
  public void from_whenStatusIsDraftAndVersionIsOne_thenUrlIsTaskList() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setStatus(ProjectStatus.DRAFT);
    dashboardProjectItem.setVersion(1);

    assertDashboardItemUrlIsExpected(
        dashboardProjectItem,
        getTaskListUrl(dashboardProjectItem.getProjectId())
    );
  }

  @Test
  public void from_whenStatusIsDraftAndVersionIsNotOne_thenUrlIsManagementPage() {

    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setStatus(ProjectStatus.DRAFT);
    dashboardProjectItem.setVersion(2);

    assertDashboardItemUrlIsExpected(
        dashboardProjectItem,
        getManagementPageUrl(dashboardProjectItem.getProjectId())
    );
  }

  @Test
  public void from_whenStatusIsQA_thenUrlIsManagementPage() {
    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setStatus(ProjectStatus.QA);

    assertDashboardItemUrlIsExpected(
        dashboardProjectItem,
        getManagementPageUrl(dashboardProjectItem.getProjectId())
    );
  }

  @Test
  public void from_whenStatusIsPublished_thenUrlIsManagementPage() {
    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setStatus(ProjectStatus.PUBLISHED);

    assertDashboardItemUrlIsExpected(
        dashboardProjectItem,
        getManagementPageUrl(dashboardProjectItem.getProjectId())
    );
  }

  @Test
  public void from_whenStatusIsArchived_thenUrlIsManagementPage() {
    final var dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
    dashboardProjectItem.setStatus(ProjectStatus.ARCHIVED);

    assertDashboardItemUrlIsExpected(
        dashboardProjectItem,
        getManagementPageUrl(dashboardProjectItem.getProjectId())
    );
  }

  private void assertCommonViewProperties(ForwardWorkPlanDashboardItemView dashboardProjectItemView,
                                          DashboardProjectItem dashboardProjectItem) {
    assertThat(dashboardProjectItemView.getProjectTitle()).isEqualTo(dashboardProjectItem.getProjectTitle());
    assertThat(dashboardProjectItemView.getOperatorName()).isEqualTo(dashboardProjectItem.getOperatorName());
    assertThat(dashboardProjectItemView.getStatus()).isEqualTo(dashboardProjectItem.getStatus().getDisplayName());
    assertThat(dashboardProjectItemView.isUpdateRequested()).isEqualTo(dashboardProjectItem.isUpdateRequested());
    assertThat(dashboardProjectItemView.getUpdateDeadlineDate()).isEqualTo(DateUtil.formatDate(dashboardProjectItem.getUpdateDeadlineDate()));
    assertThat(dashboardProjectItemView.getProjectType()).isEqualTo(dashboardProjectItem.getProjectType());
    assertThat(dashboardProjectItemView.getDashboardLink()).isNotNull();
  }

  private void assertDashboardItemUrlIsExpected(DashboardProjectItem dashboardProjectItem,
                                                String expectedUrl) {

    final var dashboardUrl = ForwardWorkPlanDashboardItemViewUtil.from(dashboardProjectItem)
        .getDashboardLink()
        .getUrl();

    assertThat(dashboardUrl).isEqualTo(expectedUrl);
  }

  private String getTaskListUrl(int projectId) {
    return ControllerUtils.getBackToTaskListUrl(projectId);
  }

  private String getManagementPageUrl(int projectId) {
    return ControllerUtils.getProjectManagementUrl(projectId);
  }

}