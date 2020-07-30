package uk.co.ogauthority.pathfinder.model.form.project.projectinformation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;

public class ProjectInformationForm {

  public interface Full {
  }

  @NotNull(message = "Select a field stage", groups = Full.class)
  private FieldStage fieldStage;

  @NotEmpty(message = "Enter a project title", groups = Full.class)
  private String projectTitle;

  @NotEmpty(message = "Provide a summary of the project", groups = Full.class)
  private String projectSummary;

  public ProjectInformationForm() {
  }


  public FieldStage getFieldStage() {
    return fieldStage;
  }

  public void setFieldStage(FieldStage fieldStage) {
    this.fieldStage = fieldStage;
  }

  public String getProjectTitle() {
    return projectTitle;
  }

  public void setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle;
  }

  public String getProjectSummary() {
    return projectSummary;
  }

  public void setProjectSummary(String projectSummary) {
    this.projectSummary = projectSummary;
  }
}
