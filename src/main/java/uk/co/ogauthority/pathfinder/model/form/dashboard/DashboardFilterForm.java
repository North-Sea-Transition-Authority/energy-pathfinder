package uk.co.ogauthority.pathfinder.model.form.dashboard;

import java.util.List;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;

public class DashboardFilterForm {

  private String projectTitle;

  private String field;

  private List<FieldStage> fieldStages;

  private List<UkcsArea> ukcsAreas;

  private List<ProjectStatus> projectStatusList;


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
}
