package uk.co.ogauthority.pathfinder.model.dashboard;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import org.springframework.web.bind.annotation.SessionAttributes;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;
import uk.co.ogauthority.pathfinder.model.form.dashboard.DashboardFilterForm;

@SessionAttributes("dashboardFilter")
public class DashboardFilter implements Serializable {

  private String operatorName;

  private String projectTitle;

  private String field;

  private List<FieldStage> fieldStages;

  private List<UkcsArea> ukcsAreas;

  private List<ProjectStatus> projectStatusList;

  public DashboardFilter() {
  }

  public DashboardFilter(List<ProjectStatus> projectStatusList) {
    this.projectStatusList = projectStatusList;
  }

  public void setFromForm(DashboardFilterForm form) {
    this.operatorName = form.getOperatorName();
    this.projectTitle = form.getProjectTitle();
    this.field = form.getField();
    this.fieldStages = form.getFieldStages();
    this.ukcsAreas = form.getUkcsAreas();
    this.projectStatusList = form.getProjectStatusList();
  }

  public void clearFilter() {
    operatorName = null;
    projectTitle = null;
    field = null;
    fieldStages = null;
    ukcsAreas = null;
    projectStatusList = null;
  }

  public String getOperatorName() {
    return operatorName;
  }

  public void setOperatorName(String operatorName) {
    this.operatorName = operatorName;
  }

  public String getProjectTitle() {
    return projectTitle;
  }

  public void setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public List<FieldStage> getFieldStages() {
    return fieldStages;
  }

  public void setFieldStages(List<FieldStage> fieldStages) {
    this.fieldStages = fieldStages;
  }

  public List<UkcsArea> getUkcsAreas() {
    return ukcsAreas;
  }

  public void setUkcsAreas(List<UkcsArea> ukcsAreas) {
    this.ukcsAreas = ukcsAreas;
  }

  public List<ProjectStatus> getProjectStatusList() {
    return projectStatusList;
  }

  public void setProjectStatusList(List<ProjectStatus> projectStatusList) {
    this.projectStatusList = projectStatusList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DashboardFilter that = (DashboardFilter) o;
    return Objects.equals(operatorName, that.operatorName)
        && Objects.equals(projectTitle, that.projectTitle)
        && Objects.equals(field, that.field)
        && Objects.equals(fieldStages, that.fieldStages)
        && Objects.equals(ukcsAreas, that.ukcsAreas)
        && Objects.equals(projectStatusList, that.projectStatusList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operatorName, projectTitle, field, fieldStages, ukcsAreas, projectStatusList);
  }
}
