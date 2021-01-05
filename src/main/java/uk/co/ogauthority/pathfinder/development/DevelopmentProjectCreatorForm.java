package uk.co.ogauthority.pathfinder.development;

import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.validation.positivewholenumber.PositiveWholeNumber;

public class DevelopmentProjectCreatorForm {

  @NotNull(message = "Select a field type")
  @PositiveWholeNumber(messagePrefix = "number of projects")
  private Integer numberOfProjects;

  @NotNull(message = "Select a project status")
  private ProjectStatus projectStatus;

  public Integer getNumberOfProjects() {
    return numberOfProjects;
  }

  public void setNumberOfProjects(Integer numberOfProjects) {
    this.numberOfProjects = numberOfProjects;
  }

  public ProjectStatus getProjectStatus() {
    return projectStatus;
  }

  public void setProjectStatus(ProjectStatus projectStatus) {
    this.projectStatus = projectStatus;
  }
}
