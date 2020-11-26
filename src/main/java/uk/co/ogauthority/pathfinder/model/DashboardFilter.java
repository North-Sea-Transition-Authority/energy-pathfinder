package uk.co.ogauthority.pathfinder.model;

import java.util.List;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;

//Will need sessionAttributes annotation
public class DashboardFilter {

  private String projectTitle;

  private FieldStage fieldStage;

  private List<ProjectStatus> projectStatusList;


  public String getProjectTitle() {
    return projectTitle;
  }

  public void setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle;
  }

  public FieldStage getFieldStage() {
    return fieldStage;
  }

  public void setFieldStage(FieldStage fieldStage) {
    this.fieldStage = fieldStage;
  }

  public List<ProjectStatus> getProjectStatusList() {
    return projectStatusList;
  }

  public void setProjectStatusList(List<ProjectStatus> projectStatusList) {
    this.projectStatusList = projectStatusList;
  }

//  public List<DashboardProjectItem> apply(List<DashboardProjectItem> dashboardItems) {
//
//  }

  public List<DashboardProjectItem> filterByProjectTitle(List<DashboardProjectItem> dashboardItems) {
    return dashboardItems.stream()
        .filter(dpi -> dpi.getProjectTitle() != null)
        .filter(dpi -> dpi.getProjectTitle().toLowerCase().contains(this.projectTitle.toLowerCase()))
        .collect(Collectors.toList());
  }

  public List<DashboardProjectItem> filterByFieldStage(List<DashboardProjectItem> userItems) {
    return userItems.stream()
        .filter(dpi -> this.fieldStage.equals(dpi.getFieldStage()))
        .collect(Collectors.toList());
  }
}
