package uk.co.ogauthority.pathfinder.model.view.dashboard.forwardworkplan;

import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.useraction.DashboardLink;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemViewUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

public class ForwardWorkPlanDashboardItemViewUtil {

  protected static final String TITLE = ProjectType.FORWARD_WORK_PLAN.getDisplayName();
  protected static final String SCREEN_READER_TEXT = " for %s";

  private ForwardWorkPlanDashboardItemViewUtil() {
    throw new IllegalStateException("ForwardWorkPlanDashboardItemViewUtil is a utility class and should not be instantiated");
  }

  public static ForwardWorkPlanDashboardItemView from(DashboardProjectItem dashboardProjectItem) {

    final var dashboardProjectItemView = DashboardProjectItemViewUtil.from(dashboardProjectItem);

    final var screenReaderText = String.format(SCREEN_READER_TEXT, dashboardProjectItemView.getOperatorName());

    return new ForwardWorkPlanDashboardItemView(
        dashboardProjectItemView,
        getLink(dashboardProjectItem, screenReaderText)
    );
  }

  private static DashboardLink getLink(DashboardProjectItem dashboardProjectItem, String screenReaderText) {

    final var status = dashboardProjectItem.getStatus();
    var url = "";

    switch (status) {
      case DRAFT:
        if (dashboardProjectItem.getVersion() == 1) {
          //TODO PAT-466 goes to "start/manage" page instead of task list
          url = ControllerUtils.getBackToTaskListUrl(dashboardProjectItem.getProjectId());
        } else {
          url = ControllerUtils.getProjectManagementUrl(dashboardProjectItem.getProjectId());
        }
        break;
      case QA:
      case ARCHIVED:
      case PUBLISHED:
        url = ControllerUtils.getProjectManagementUrl(dashboardProjectItem.getProjectId());
        break;
      default:
        throw new IllegalStateException(String.format("Project with id %s has unsupported status %s",
            dashboardProjectItem.getProjectId(),
            status
        ));
    }
    return new DashboardLink(
        TITLE,
        url,
        true,
        screenReaderText
    );
  }
}
