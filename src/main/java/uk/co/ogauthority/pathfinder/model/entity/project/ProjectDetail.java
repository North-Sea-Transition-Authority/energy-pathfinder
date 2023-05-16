package uk.co.ogauthority.pathfinder.model.entity.project;

import java.time.Instant;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.entityduplication.ParentEntity;

@Entity
@Table(name = "project_details")
public class ProjectDetail implements ParentEntity {

  private static final Integer FIRST_VERSION = 1;

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

  private Instant createdDatetime;

  @Enumerated(EnumType.STRING)
  private ProjectType projectType;

  /**
   * One-to-many relationships like this should generally be avoided, this relationship exists purely to allow the
   * Criteria API to be able to find associated data, and should not be used anywhere else.
   */
  @OneToMany(mappedBy = "projectDetail")
  private List<ProjectOperator> projectOperators;

  /**
   * One-to-many relationships like this should generally be avoided, this relationship exists purely to allow the
   * Criteria API to be able to find associated data, and should not be used anywhere else.
   */
  @OneToMany(mappedBy = "projectDetail")
  private List<ProjectInformation> projectInformation;

  public ProjectDetail() {
  }

  public ProjectDetail(Project project,
                       ProjectStatus status,
                       Integer createdByWua,
                       Integer version,
                       boolean isCurrentVersion,
                       ProjectType projectType) {
    this.project = project;
    this.status = status;
    this.createdByWua = createdByWua;
    this.version = version;
    this.isCurrentVersion = isCurrentVersion;
    this.createdDatetime = Instant.now();
    this.projectType = projectType;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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

  public Instant getCreatedDatetime() {
    return createdDatetime;
  }

  public void setCreatedDatetime(Instant createdDatetime) {
    this.createdDatetime = createdDatetime;
  }

  public boolean isFirstVersion() {
    return FIRST_VERSION.equals(version);
  }

  public ProjectType getProjectType() {
    return projectType;
  }

  public void setProjectType(ProjectType projectType) {
    this.projectType = projectType;
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
        && Objects.equals(submittedByWua, projectDetail.submittedByWua)
        && Objects.equals(createdDatetime, projectDetail.createdDatetime)
        && Objects.equals(projectType, projectDetail.projectType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        project,
        status,
        version,
        isCurrentVersion,
        createdByWua,
        submittedInstant,
        submittedByWua,
        createdDatetime,
        projectType
    );
  }
}
