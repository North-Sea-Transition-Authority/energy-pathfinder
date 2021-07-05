package uk.co.ogauthority.pathfinder.model.entity.project;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Immutable;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

@Entity
@Table(name = "api_published_projects")
@Immutable
public class PublishedProject implements SearchSelectable {

  @Id
  private Integer projectId;

  @Enumerated(EnumType.STRING)
  private ProjectType projectType;

  private String operatorGroupName;

  private String projectDisplayName;

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

  @Override
  public String getSelectionId() {
    return String.valueOf(getProjectId());
  }

  @Override
  public String getSelectionText() {
    return getProjectDisplayName();
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PublishedProject that = (PublishedProject) o;
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
