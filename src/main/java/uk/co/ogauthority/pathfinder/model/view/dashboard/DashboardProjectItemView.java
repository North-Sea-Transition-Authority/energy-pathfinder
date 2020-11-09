package uk.co.ogauthority.pathfinder.model.view.dashboard;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
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
        status
      );
  }

  public static DashboardLink getLink(DashboardProjectItem dashboardProjectItem, String title, String screenReaderText) {
    return new DashboardLink(
          title,
          ReverseRouter.route(on(TaskListController.class).viewTaskList(dashboardProjectItem.getProjectId(), null)),
          true,
          screenReaderText
      );
  }

  private DashboardProjectItemView(DashboardLink dashboardLink,
                                   String projectTitle,
                                   String fieldStage,
                                   String fieldName,
                                   String operatorName,
                                   String status) {
    this.dashboardLink = dashboardLink;
    this.projectTitle = projectTitle;
    this.fieldStage = fieldStage;
    this.fieldName = fieldName;
    this.operatorName = operatorName;
    this.status = status;
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
}
