package uk.co.ogauthority.pathfinder.model.entity.dashboard;

import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Immutable;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;

@Entity
@Table(name = "dashboard_project_items")
@Immutable
public class DashboardProjectItem {

  @Id
  private Integer projectId;

  private Integer projectDetailId;

  private Instant createdDatetime;

  @Enumerated(EnumType.STRING)
  private ProjectStatus status;

  private String projectTitle;

  @Enumerated(EnumType.STRING)
  private FieldStage fieldStage;

  private String fieldName;

  @Enumerated(EnumType.STRING)
  private UkcsArea ukcsArea;

  @ManyToOne
  @JoinColumn(name = "operator_org_grp_id")
  private PortalOrganisationGroup organisationGroup;

  private Instant sortKey;

  public Integer getProjectId() {
    return projectId;
  }

  public Integer getProjectDetailId() {
    return projectDetailId;
  }

  public Instant getCreatedDatetime() {
    return createdDatetime;
  }

  public ProjectStatus getStatus() {
    return status;
  }

  public String getProjectTitle() {
    return projectTitle;
  }

  public FieldStage getFieldStage() {
    return fieldStage;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getOperatorName() {
    return organisationGroup.getName();
  }

  public UkcsArea getUkcsArea() {
    return ukcsArea;
  }

  public PortalOrganisationGroup getOrganisationGroup() {
    return organisationGroup;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public void setProjectDetailId(Integer projectDetailId) {
    this.projectDetailId = projectDetailId;
  }

  public void setCreatedDatetime(Instant createdDatetime) {
    this.createdDatetime = createdDatetime;
  }

  public void setStatus(ProjectStatus status) {
    this.status = status;
  }

  public void setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle;
  }

  public void setFieldStage(FieldStage fieldStage) {
    this.fieldStage = fieldStage;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public void setUkcsArea(UkcsArea ukcsArea) {
    this.ukcsArea = ukcsArea;
  }

  public void setOrganisationGroup(
      PortalOrganisationGroup organisationGroup) {
    this.organisationGroup = organisationGroup;
  }

  public Instant getSortKey() {
    return sortKey;
  }

  public void setSortKey(Instant sortkey) {
    this.sortKey = sortkey;
  }
}
