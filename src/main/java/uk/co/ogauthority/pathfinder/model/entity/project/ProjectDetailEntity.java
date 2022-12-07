package uk.co.ogauthority.pathfinder.model.entity.project;

import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
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
