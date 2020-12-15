package uk.co.ogauthority.pathfinder.model.dashboard;

import java.io.Serializable;
import java.util.List;
import org.springframework.web.bind.annotation.SessionAttributes;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;
import uk.co.ogauthority.pathfinder.model.form.dashboard.DashboardFilterForm;

@SessionAttributes("dashboardFilter")
public class DashboardFilter implements Serializable {

  private String projectTitle;

  private String field;

  private List<FieldStage> fieldStages;

  private List<UkcsArea> ukcsAreas;

  private List<ProjectStatus> projectStatusList;

  public void setFromForm(DashboardFilterForm form) {
    this.projectTitle = form.getProjectTitle();
    this.field = form.getField();
    this.fieldStages = form.getFieldStages();
    this.ukcsAreas = form.getUkcsAreas();
    this.projectStatusList = form.getProjectStatusList();
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
}
