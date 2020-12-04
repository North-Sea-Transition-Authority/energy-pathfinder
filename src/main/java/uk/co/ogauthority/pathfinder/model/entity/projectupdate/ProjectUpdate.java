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
  private ProjectDetail newDetail;

  @Enumerated(EnumType.STRING)
  private ProjectUpdateType updateType;

  private boolean noUpdate;

  private String reasonNoUpdateRequired;

  public Integer getId() {
    return id;
  }

  public ProjectDetail getFromDetail() {
    return fromDetail;
  }

  public void setFromDetail(ProjectDetail fromDetail) {
    this.fromDetail = fromDetail;
  }

  public ProjectDetail getNewDetail() {
    return newDetail;
  }

  public void setNewDetail(ProjectDetail newDetail) {
    this.newDetail = newDetail;
  }

  public ProjectUpdateType getUpdateType() {
    return updateType;
  }

  public void setUpdateType(ProjectUpdateType updateType) {
    this.updateType = updateType;
  }

  public boolean isNoUpdate() {
    return noUpdate;
  }

  public void setNoUpdate(boolean noUpdate) {
    this.noUpdate = noUpdate;
  }

  public String getReasonNoUpdateRequired() {
    return reasonNoUpdateRequired;
  }

  public void setReasonNoUpdateRequired(String reasonNoUpdateRequired) {
    this.reasonNoUpdateRequired = reasonNoUpdateRequired;
  }
}
