package uk.co.ogauthority.pathfinder.model.view.dashboard.forwardworkplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import uk.co.ogauthority.pathfinder.controller.project.start.forwardworkplan.ForwardWorkPlanProjectStartController;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.useraction.DashboardLink;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemViewUtil;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
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
    final var projectId = dashboardProjectItem.getProjectId();

    var url = "";

    switch (status) {
      case DRAFT:
        if (dashboardProjectItem.getVersion() == 1) {
          url = ReverseRouter.route(on(ForwardWorkPlanProjectStartController.class).startPage(
              projectId,
              null,
              null
          ));
        } else {
          url = ControllerUtils.getProjectManagementUrl(projectId);
        }
        break;
      case QA:
      case ARCHIVED:
      case PUBLISHED:
        url = ControllerUtils.getProjectManagementUrl(projectId);
        break;
      default:
        throw new IllegalStateException(String.format("Project with id %s has unsupported status %s",
            projectId,
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
