package uk.co.ogauthority.pathfinder.model.entity.project;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import org.hibernate.annotations.Immutable;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

@Entity
@Table(name = "api_selectable_projects")
@Immutable
public class SelectableProject {

  @Id
  private Integer projectId;

  @Enumerated(EnumType.STRING)
  private ProjectType projectType;

  private String operatorGroupName;

  private String projectDisplayName;

  private boolean isPublished;

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public ProjectType getProjectType() {
    return projectType;
  }

  public void setProjectType(ProjectType projectType) {
    this.projectType = projectType;
  }

  public String getOperatorGroupName() {
    return operatorGroupName;
  }

  public void setOperatorGroupName(String operatorGroupName) {
    this.operatorGroupName = operatorGroupName;
  }

  public String getProjectDisplayName() {
    return projectDisplayName;
  }

  public void setProjectDisplayName(String projectDisplayName) {
    this.projectDisplayName = projectDisplayName;
  }

  public boolean isPublished() {
    return isPublished;
  }

  public void setPublished(boolean published) {
    isPublished = published;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SelectableProject that = (SelectableProject) o;
    return Objects.equals(projectId, that.projectId)
        && Objects.equals(projectType, that.projectType)
        && Objects.equals(operatorGroupName, that.operatorGroupName)
        && Objects.equals(projectDisplayName, that.projectDisplayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        projectId,
        projectType,
        operatorGroupName,
        projectDisplayName
    );
  }
}
