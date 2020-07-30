package uk.co.ogauthority.pathfinder.model.entity.project;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;

@Entity
@Table(name = "project_details")
public class ProjectDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "project_id")
  private Project project;

  @Enumerated(EnumType.STRING)
  private ProjectStatus status;

  private Integer version;

  private boolean isCurrentVersion;

  private Integer createdByWua;


  public ProjectDetails() {
  }

  public ProjectDetails(Project project,
                        ProjectStatus status,
                        Integer createdByWua,
                        Integer version,
                        boolean isCurrentVersion) {
    this.project = project;
    this.status = status;
    this.createdByWua = createdByWua;
    this.version = 1;
    this.isCurrentVersion = true;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public ProjectStatus getStatus() {
    return status;
  }

  public void setStatus(ProjectStatus status) {
    this.status = status;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public boolean getIsCurrentVersion() {
    return isCurrentVersion;
  }

  public void setIsCurrentVersion(boolean currentVersion) {
    isCurrentVersion = currentVersion;
  }

  public Integer getCreatedByWua() {
    return createdByWua;
  }

  public void setCreatedByWua(Integer createdByWua) {
    this.createdByWua = createdByWua;
  }
}
