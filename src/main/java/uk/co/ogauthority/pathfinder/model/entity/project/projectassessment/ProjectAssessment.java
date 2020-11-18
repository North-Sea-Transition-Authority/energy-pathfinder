package uk.co.ogauthority.pathfinder.model.entity.project.projectassessment;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.projectassessment.ProjectQuality;

@Entity
@Table(name = "project_assessments")
public class ProjectAssessment extends ProjectDetailEntity {

  @Enumerated(EnumType.STRING)
  private ProjectQuality projectQuality;

  private Boolean readyToBePublished;

  private Boolean updateRequired;

  @Column(name = "assessed_datetime")
  private Instant assessedInstant;

  private Integer assessorWua;

  public ProjectQuality getProjectQuality() {
    return projectQuality;
  }

  public void setProjectQuality(
      ProjectQuality projectQuality) {
    this.projectQuality = projectQuality;
  }

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

  public Integer getAssessorWua() {
    return assessorWua;
  }

  public void setAssessorWua(Integer assessorWua) {
    this.assessorWua = assessorWua;
  }
}
