package uk.co.ogauthority.pathfinder.model.entity.project;

import java.time.Instant;
import java.util.Objects;
import javax.persistence.Column;
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
import uk.co.ogauthority.pathfinder.service.entityduplication.ParentEntity;

@Entity
@Table(name = "project_details")
public class ProjectDetail implements ParentEntity {

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

  @Column(name = "submitted_datetime")
  private Instant submittedInstant;

  private Integer submittedByWua;

  public ProjectDetail() {
  }

  public ProjectDetail(Project project,
                       ProjectStatus status,
                       Integer createdByWua,
                       Integer version,
                       boolean isCurrentVersion) {
    this.project = project;
    this.status = status;
    this.createdByWua = createdByWua;
    this.version = version;
    this.isCurrentVersion = isCurrentVersion;
  }

  public Integer getId() {
    return id;
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

  public Instant getSubmittedInstant() {
    return submittedInstant;
  }

  public void setSubmittedInstant(Instant submittedInstant) {
    this.submittedInstant = submittedInstant;
  }

  public Integer getSubmittedByWua() {
    return submittedByWua;
  }

  public void setSubmittedByWua(Integer submittedByWua) {
    this.submittedByWua = submittedByWua;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectDetail projectDetail = (ProjectDetail) o;
    return Objects.equals(id, projectDetail.id)
        && Objects.equals(project, projectDetail.project)
        && status == projectDetail.status
        && Objects.equals(version, projectDetail.version)
        && Objects.equals(isCurrentVersion, projectDetail.isCurrentVersion)
        && Objects.equals(createdByWua, projectDetail.createdByWua)
        && Objects.equals(submittedInstant, projectDetail.submittedInstant)
        && Objects.equals(submittedByWua, projectDetail.submittedByWua);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, project, status, version, isCurrentVersion, createdByWua, submittedInstant, submittedByWua);
  }
}
