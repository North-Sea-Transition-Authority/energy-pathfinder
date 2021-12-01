package uk.co.ogauthority.pathfinder.model.view.dashboard;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.testutil.DashboardProjectItemTestUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class DashboardProjectItemViewUtilTest {

  private final DashboardProjectItem dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();

  @Test
  public void from_allFieldsSetCorrectly() {
    var view = DashboardProjectItemViewUtil.from(dashboardProjectItem);
    assertCommonFieldsMatch(dashboardProjectItem, view);
  }

  private void assertCommonFieldsMatch(DashboardProjectItem dashboardProjectItem, DashboardProjectItemView view) {
    assertThat(view.getProjectTitle()).isEqualTo(dashboardProjectItem.getProjectTitle());
    assertThat(view.getOperatorName()).isEqualTo(dashboardProjectItem.getOperatorName());
    assertThat(view.getStatus()).isEqualTo(dashboardProjectItem.getStatus().getDisplayName());
    assertThat(view.isUpdateRequested()).isEqualTo(dashboardProjectItem.isUpdateRequested());
    assertThat(view.getUpdateDeadlineDate()).isEqualTo(DateUtil.formatDate(dashboardProjectItem.getUpdateDeadlineDate()));
  }
}
