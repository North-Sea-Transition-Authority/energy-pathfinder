package uk.co.ogauthority.pathfinder.model.view.dashboard;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.form.useraction.Link;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class DashboardProjectItemView {

  public static final String TITLE_PLACEHOLDER = "%s project created on %s";
  public static final String SCREEN_READER_TEXT = " created on %s";

  private Link projectLink;

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

    var screenReaderText = String.format(SCREEN_READER_TEXT, formattedCreatedDateTime);

    return new DashboardProjectItemView(
        getLink(dashboardProjectItem, title, screenReaderText),
        title,
        dashboardProjectItem.getFieldStage() != null ? dashboardProjectItem.getFieldStage().getDisplayName() : "",
        dashboardProjectItem.getFieldName(),
        dashboardProjectItem.getOperatorName(),
        status
      );
  }

  public static Link getLink(DashboardProjectItem dashboardProjectItem, String title, String screenReaderText) {
    return new Link(
          title,
          ReverseRouter.route(on(TaskListController.class).viewTaskList(dashboardProjectItem.getProjectId(), null)),
          true,
          screenReaderText
      );
  }

  private DashboardProjectItemView(Link projectLink, String projectTitle, String fieldStage, String fieldName,
                                  String operatorName, String status) {
    this.projectLink = projectLink;
    this.projectTitle = projectTitle;
    this.fieldStage = fieldStage;
    this.fieldName = fieldName;
    this.operatorName = operatorName;
    this.status = status;
  }

  public Link getProjectLink() {
    return projectLink;
  }

  public void setProjectLink(Link projectLink) {
    this.projectLink = projectLink;
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
