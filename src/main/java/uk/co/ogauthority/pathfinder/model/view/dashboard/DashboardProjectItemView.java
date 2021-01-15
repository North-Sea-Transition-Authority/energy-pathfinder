package uk.co.ogauthority.pathfinder.model.view.dashboard;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.form.useraction.DashboardLink;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.DateUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class DashboardProjectItemView {

  public static final String TITLE_PLACEHOLDER = "%s project created on %s";
  public static final String SCREEN_READER_TEXT = " created on %s";

  private DashboardLink dashboardLink;

  private String projectTitle;

  private String fieldStage;

  private String fieldName;

  private String operatorName;

  private String status;

  private boolean updateRequested;

  private String updateDeadlineDate;

  public static DashboardProjectItemView from(DashboardProjectItem dashboardProjectItem) {
    var formattedCreatedDateTime = DateUtil.formatInstant(dashboardProjectItem.getCreatedDatetime());
    var status = dashboardProjectItem.getStatus().getDisplayName();

    var title = dashboardProjectItem.getProjectTitle() != null
        ? dashboardProjectItem.getProjectTitle()
        : String.format(TITLE_PLACEHOLDER, status, formattedCreatedDateTime);

    var screenReaderText = dashboardProjectItem.getProjectTitle() != null
        ? String.format(SCREEN_READER_TEXT, formattedCreatedDateTime)
        : "";

    return new DashboardProjectItemView(
        getLink(dashboardProjectItem, title, screenReaderText),
        title,
        dashboardProjectItem.getFieldStage() != null
            ? dashboardProjectItem.getFieldStage().getDisplayName()
            : StringDisplayUtil.NOT_SET_TEXT,
        dashboardProjectItem.getFieldName() != null
            ? dashboardProjectItem.getFieldName()
            : StringDisplayUtil.NOT_SET_TEXT,
        dashboardProjectItem.getOperatorName(),
        status,
        dashboardProjectItem.isUpdateRequested(),
        DateUtil.formatDate(dashboardProjectItem.getUpdateDeadlineDate())
      );
  }

  public static DashboardLink getLink(DashboardProjectItem dashboardProjectItem, String title, String screenReaderText) {
    var status = dashboardProjectItem.getStatus();
    String url;
    switch (status) {
      case DRAFT:
        if (dashboardProjectItem.getVersion() == 1) {
          url = ReverseRouter.route(on(TaskListController.class).viewTaskList(dashboardProjectItem.getProjectId(), null));
        } else {
          url = ReverseRouter.route(on(ManageProjectController.class).getProject(dashboardProjectItem.getProjectId(), null, null, null));
        }
        break;
      case QA:
      case ARCHIVED:
      case PUBLISHED:
        url = ReverseRouter.route(on(ManageProjectController.class).getProject(dashboardProjectItem.getProjectId(), null, null, null));
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

  private DashboardProjectItemView(DashboardLink dashboardLink,
                                   String projectTitle,
                                   String fieldStage,
                                   String fieldName,
                                   String operatorName,
                                   String status,
                                   boolean updateRequested,
                                   String updateDeadlineDate) {
    this.dashboardLink = dashboardLink;
    this.projectTitle = projectTitle;
    this.fieldStage = fieldStage;
    this.fieldName = fieldName;
    this.operatorName = operatorName;
    this.status = status;
    this.updateRequested = updateRequested;
    this.updateDeadlineDate = updateDeadlineDate;
  }

  public DashboardLink getDashboardLink() {
    return dashboardLink;
  }

  public void setDashboardAction(DashboardLink dashboardLink) {
    this.dashboardLink = dashboardLink;
  }

  public String getProjectTitle() {
    return projectTitle;
  }

  public void setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle;
  }

  public String getFieldStage() {
    return fieldStage;
  }

  public void setFieldStage(String fieldStage) {
    this.fieldStage = fieldStage;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getOperatorName() {
    return operatorName;
  }

  public void setOperatorName(String operatorName) {
    this.operatorName = operatorName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public boolean isUpdateRequested() {
    return updateRequested;
  }

  public void setUpdateRequested(boolean updateRequested) {
    this.updateRequested = updateRequested;
  }

  public String getUpdateDeadlineDate() {
    return updateDeadlineDate;
  }

  public void setUpdateDeadlineDate(String updateDeadlineDate) {
    this.updateDeadlineDate = updateDeadlineDate;
  }
}
