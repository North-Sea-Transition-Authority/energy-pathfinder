package uk.co.ogauthority.pathfinder.model.view.dashboard;

import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class DashboardProjectItemViewUtil {

  private DashboardProjectItemViewUtil() {
    throw new IllegalStateException("DashboardProjectItemViewUtil is a utility class and should not be instantiated");
  }

  public static DashboardProjectItemView from(DashboardProjectItem dashboardProjectItem) {
    return new DashboardProjectItemView(
        dashboardProjectItem.getProjectId(),
        dashboardProjectItem.getProjectTitle(),
        dashboardProjectItem.getOperatorName(),
        dashboardProjectItem.getStatus().getDisplayName(),
        dashboardProjectItem.isUpdateRequested(),
        DateUtil.formatDate(dashboardProjectItem.getUpdateDeadlineDate()),
        dashboardProjectItem.getProjectType()
    );
  }
}
