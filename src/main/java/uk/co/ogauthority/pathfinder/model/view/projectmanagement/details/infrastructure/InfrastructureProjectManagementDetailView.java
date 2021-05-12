package uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.infrastructure;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.ProjectManagementDetailView;

public class InfrastructureProjectManagementDetailView extends ProjectManagementDetailView {

  private String fieldStage;

  private String field;

  private boolean isEnergyTransitionProject;

  public String getFieldStage() {
    return fieldStage;
  }

  public void setFieldStage(String fieldStage) {
    this.fieldStage = fieldStage;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public boolean getIsEnergyTransitionProject() {
    return isEnergyTransitionProject;
  }

  public void setIsEnergyTransitionProject(boolean isEnergyTransitionProject) {
    this.isEnergyTransitionProject = isEnergyTransitionProject;
  }

  @Override
  public boolean equals(Object o) {

    if (!super.equals(o)) {
      return false;
    }

    if (this == o) {
      return true;
    }
    if (getClass() != o.getClass()) {
      return false;
    }
    InfrastructureProjectManagementDetailView that = (InfrastructureProjectManagementDetailView) o;
    return Objects.equals(fieldStage, that.fieldStage)
        && Objects.equals(field, that.field)
        && Objects.equals(isEnergyTransitionProject, that.isEnergyTransitionProject);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        fieldStage,
        field,
        isEnergyTransitionProject
    );
  }
}
