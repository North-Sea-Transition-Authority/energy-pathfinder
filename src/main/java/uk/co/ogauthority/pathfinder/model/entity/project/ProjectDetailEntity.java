package uk.co.ogauthority.pathfinder.model.entity.project;

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

}
