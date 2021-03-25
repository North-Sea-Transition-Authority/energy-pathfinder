package uk.co.ogauthority.pathfinder.model.entity.projectupdate;

import java.time.Instant;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;

@Entity
@Table(name = "regulator_update_requests")
public class RegulatorUpdateRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Integer id;

  @OneToOne
  @JoinColumn(name = "project_detail_id")
  private ProjectDetail projectDetail;

  @Lob
  @Column(name = "update_reason", columnDefinition = "CLOB")
  private String updateReason;

  private LocalDate deadlineDate;

  private Integer requestedByWuaId;

  @Column(name = "requested_datetime")
  private Instant requestedInstant;

  public Integer getId() {
    return id;
  }

  public ProjectDetail getProjectDetail() {
    return projectDetail;
  }

  public void setProjectDetail(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public String getUpdateReason() {
    return updateReason;
  }

  public void setUpdateReason(String updateReason) {
    this.updateReason = updateReason;
  }

  public LocalDate getDeadlineDate() {
    return deadlineDate;
  }

  public void setDeadlineDate(LocalDate deadlineDate) {
    this.deadlineDate = deadlineDate;
  }

  public Integer getRequestedByWuaId() {
    return requestedByWuaId;
  }

  public void setRequestedByWuaId(Integer requestedByWuaId) {
    this.requestedByWuaId = requestedByWuaId;
  }

  public Instant getRequestedInstant() {
    return requestedInstant;
  }

  public void setRequestedInstant(Instant requestedInstant) {
    this.requestedInstant = requestedInstant;
  }
}
