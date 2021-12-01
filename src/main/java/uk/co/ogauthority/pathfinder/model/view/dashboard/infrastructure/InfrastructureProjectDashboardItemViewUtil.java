package uk.co.ogauthority.pathfinder.model.view.dashboard.infrastructure;

import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.form.useraction.DashboardLink;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemViewUtil;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorDisplayNameUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.DateUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class InfrastructureProjectDashboardItemViewUtil {

  protected static final String TITLE_PLACEHOLDER = "%s project created on %s";
  protected static final String SCREEN_READER_TEXT = " created on %s";

  private InfrastructureProjectDashboardItemViewUtil() {
    throw new IllegalStateException("InfrastructureProjectDashboardItemViewUtil is a utility class and should not be instantiated");
  }

  public static InfrastructureProjectDashboardItemView from(DashboardProjectItem dashboardProjectItem) {

    final var dashboardProjectItemView = DashboardProjectItemViewUtil.from(dashboardProjectItem);

    final var fieldStage = dashboardProjectItem.getFieldStage() != null
        ? dashboardProjectItem.getFieldStage().getDisplayName()
        : StringDisplayUtil.NOT_SET_TEXT;

    final var fieldName = dashboardProjectItem.getFieldName() != null
        ? dashboardProjectItem.getFieldName()
        : StringDisplayUtil.NOT_SET_TEXT;

    final var formattedCreatedDateTime = DateUtil.formatInstant(dashboardProjectItem.getCreatedDatetime());
    final var status = dashboardProjectItem.getStatus().getDisplayName();

    var title = dashboardProjectItem.getProjectTitle() != null
        ? dashboardProjectItem.getProjectTitle()
        : String.format(TITLE_PLACEHOLDER, status, formattedCreatedDateTime);

    var screenReaderText = dashboardProjectItem.getProjectTitle() != null
        ? String.format(SCREEN_READER_TEXT, formattedCreatedDateTime)
        : "";

    var projectOperatorDisplayName = ProjectOperatorDisplayNameUtil.getProjectOperatorDisplayName(
        dashboardProjectItem.getOrganisationGroup(),
        dashboardProjectItem.getPublishableOperator()
    );

    dashboardProjectItemView.setOperatorName(projectOperatorDisplayName);

    return new InfrastructureProjectDashboardItemView(
        dashboardProjectItemView,
        getLink(dashboardProjectItem, title, screenReaderText),
        fieldStage,
        fieldName
    );
  }

  private static DashboardLink getLink(DashboardProjectItem dashboardProjectItem, String title, String screenReaderText) {
    var status = dashboardProjectItem.getStatus();
    String url;

    switch (status) {
      case DRAFT:
        if (dashboardProjectItem.getVersion() == 1) {
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
        title,
        url,
        true,
        screenReaderText
    );
  }
}
