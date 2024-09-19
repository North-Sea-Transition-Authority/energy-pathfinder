package uk.co.ogauthority.pathfinder.model.entity.project;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.service.entityduplication.ChildEntity;

@MappedSuperclass
public abstract class ProjectDetailEntity implements ChildEntity<Integer, ProjectDetail> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Integer id;

  @ManyToOne
  @JoinColumn(name = "project_detail_id")
  protected ProjectDetail projectDetail;

  public Integer getId() {
    return id;
  }

  public ProjectDetail getProjectDetail() {
    return projectDetail;
  }

  public void setProjectDetail(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  @Override
  public void clearId() {
    this.id = null;
  }

  @Override
  public void setParent(ProjectDetail parentEntity) {
    setProjectDetail(parentEntity);
  }

  @Override
  public ProjectDetail getParent() {
    return getProjectDetail();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProjectDetailEntity)) {
      return false;
    }
    ProjectDetailEntity that = (ProjectDetailEntity) o;
    return Objects.equals(id, that.id)
        && Objects.equals(projectDetail, that.projectDetail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        projectDetail
    );
  }
}
