package uk.co.ogauthority.pathfinder.model.entity.projectupdate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.projectupdate.ProjectUpdateType;

@Entity
@Table(name = "project_updates")
public class ProjectUpdate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Integer id;

  @OneToOne
  @JoinColumn(name = "from_project_detail_id")
  private ProjectDetail fromDetail;

  @OneToOne
  @JoinColumn(name = "new_project_detail_id")
  private ProjectDetail toDetail;

  @Enumerated(EnumType.STRING)
  private ProjectUpdateType updateType;

  public Integer getId() {
    return id;
  }

  public ProjectDetail getFromDetail() {
    return fromDetail;
  }

  public void setFromDetail(ProjectDetail fromDetail) {
    this.fromDetail = fromDetail;
  }

  public ProjectDetail getToDetail() {
    return toDetail;
  }

  public void setToDetail(ProjectDetail toDetail) {
    this.toDetail = toDetail;
  }

  public ProjectUpdateType getUpdateType() {
    return updateType;
  }

  public void setUpdateType(ProjectUpdateType updateType) {
    this.updateType = updateType;
  }
}
