package uk.co.ogauthority.pathfinder.model.entity.projectassessment;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;

@Entity
@Table(name = "project_assessments")
public class ProjectAssessment extends ProjectDetailEntity {

  private Boolean readyToBePublished;

  private Boolean updateRequired;

  @Column(name = "assessed_datetime")
  private Instant assessedInstant;

  private Integer assessorWuaId;

  public Boolean getReadyToBePublished() {
    return readyToBePublished;
  }

  public void setReadyToBePublished(Boolean readyToBePublished) {
    this.readyToBePublished = readyToBePublished;
  }

  public Boolean getUpdateRequired() {
    return updateRequired;
  }

  public void setUpdateRequired(Boolean updateRequired) {
    this.updateRequired = updateRequired;
  }

  public Instant getAssessedInstant() {
    return assessedInstant;
  }

  public void setAssessedInstant(Instant assessedInstant) {
    this.assessedInstant = assessedInstant;
  }

  public Integer getAssessorWuaId() {
    return assessorWuaId;
  }

  public void setAssessorWuaId(Integer assessorWua) {
    this.assessorWuaId = assessorWua;
  }
}
