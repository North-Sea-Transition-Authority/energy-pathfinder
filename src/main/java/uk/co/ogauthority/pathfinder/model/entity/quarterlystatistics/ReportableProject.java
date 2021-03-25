package uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics;

import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Immutable;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;

@Entity
@Table(name = "reportable_projects")
@Immutable
public class ReportableProject {

  @Id
  private Integer projectDetailId;

  @Enumerated(EnumType.STRING)
  private FieldStage fieldStage;

  private Instant lastUpdatedDatetime;

  private String operatorName;

  private String projectTitle;

  private Integer projectId;

  public Integer getProjectDetailId() {
    return projectDetailId;
  }

  public void setProjectDetailId(Integer projectDetailId) {
    this.projectDetailId = projectDetailId;
  }

  public FieldStage getFieldStage() {
    return fieldStage;
  }

  public void setFieldStage(FieldStage fieldStage) {
    this.fieldStage = fieldStage;
  }

  public Instant getLastUpdatedDatetime() {
    return lastUpdatedDatetime;
  }

  public void setLastUpdatedDatetime(Instant lastUpdatedDatetime) {
    this.lastUpdatedDatetime = lastUpdatedDatetime;
  }

  public String getOperatorName() {
    return operatorName;
  }

  public void setOperatorName(String operatorName) {
    this.operatorName = operatorName;
  }

  public String getProjectTitle() {
    return projectTitle;
  }

  public void setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }
}
