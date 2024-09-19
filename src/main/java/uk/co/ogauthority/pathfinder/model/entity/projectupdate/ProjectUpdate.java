package uk.co.ogauthority.pathfinder.model.entity.projectupdate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
