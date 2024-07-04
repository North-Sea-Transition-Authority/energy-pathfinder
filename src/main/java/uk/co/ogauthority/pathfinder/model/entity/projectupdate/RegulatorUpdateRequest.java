package uk.co.ogauthority.pathfinder.model.entity.projectupdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
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
