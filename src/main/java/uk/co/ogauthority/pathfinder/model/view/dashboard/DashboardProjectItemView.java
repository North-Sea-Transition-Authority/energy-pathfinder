package uk.co.ogauthority.pathfinder.model.view.dashboard;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class DashboardProjectItemView {

  private String projectTitle;

  private String operatorName;

  private String status;

  private boolean updateRequested;

  private String updateDeadlineDate;

  private ProjectType projectType;

  public DashboardProjectItemView(String projectTitle,
                                  String operatorName,
                                  String status,
                                  boolean updateRequested,
                                  String updateDeadlineDate,
                                  ProjectType projectType) {
    this.projectTitle = projectTitle;
    this.operatorName = operatorName;
    this.status = status;
    this.updateRequested = updateRequested;
    this.updateDeadlineDate = updateDeadlineDate;
    this.projectType = projectType;
  }

  public String getProjectTitle() {
    return projectTitle;
  }

  public void setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle;
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

  public ProjectType getProjectType() {
    return projectType;
  }

  public void setProjectType(ProjectType projectType) {
    this.projectType = projectType;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DashboardProjectItemView that = (DashboardProjectItemView) o;
    return Objects.equals(projectTitle, that.projectTitle)
        && Objects.equals(operatorName, that.operatorName)
        && Objects.equals(status, that.status)
        && Objects.equals(updateRequested, that.updateRequested)
        && Objects.equals(updateDeadlineDate, that.updateDeadlineDate)
        && Objects.equals(projectType, that.projectType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        projectTitle,
        operatorName,
        status,
        updateRequested,
        updateDeadlineDate,
        projectTitle
    );
  }
}
