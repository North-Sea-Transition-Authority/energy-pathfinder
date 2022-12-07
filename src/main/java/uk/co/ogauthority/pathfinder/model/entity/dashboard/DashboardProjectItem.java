package uk.co.ogauthority.pathfinder.model.entity.dashboard;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationUnit;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributorConverter;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;

@MappedSuperclass
public abstract class DashboardProjectItem {

  @Id
  private Integer projectId;

  private Integer projectDetailId;

  private Instant createdDatetime;

  @Enumerated(EnumType.STRING)
  private ProjectStatus status;

  private Integer version;

  private String projectTitle;

  @Enumerated(EnumType.STRING)
  private FieldStage fieldStage;

  private String fieldName;

  @Enumerated(EnumType.STRING)
  private UkcsArea ukcsArea;

  @ManyToOne
  @JoinColumn(name = "operator_org_grp_id")
  private PortalOrganisationGroup organisationGroup;

  @ManyToOne
  @JoinColumn(name = "publishable_org_unit_id")
  private PortalOrganisationUnit publishableOperator;

  private boolean updateRequested;

  private LocalDate updateDeadlineDate;

  private Instant sortKey;

  private Instant updateSortKey;

  @Enumerated(EnumType.STRING)
  private ProjectType projectType;

  private int projectTypeSortKey;

  @Convert(converter = ProjectContributorConverter.class)
  private List<Integer> contributorOrgIds;

  public List<Integer> getContributorOrgIds() {
    return contributorOrgIds;
  }

  public void setContributorOrgIds(List<Integer> contributorOrgIds) {
    this.contributorOrgIds = contributorOrgIds;
  }

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

  public Integer getVersion() {
    return version;
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

  public boolean isUpdateRequested() {
    return updateRequested;
  }

  public LocalDate getUpdateDeadlineDate() {
    return updateDeadlineDate;
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

  public void setVersion(Integer version) {
    this.version = version;
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

  public void setUpdateRequested(boolean updateRequested) {
    this.updateRequested = updateRequested;
  }

  public void setUpdateDeadlineDate(LocalDate updateDeadlineDate) {
    this.updateDeadlineDate = updateDeadlineDate;
  }

  public Instant getSortKey() {
    return sortKey;
  }

  public void setSortKey(Instant sortKey) {
    this.sortKey = sortKey;
  }

  public Instant getUpdateSortKey() {
    return updateSortKey;
  }

  public void setUpdateSortKey(Instant updateSortKey) {
    this.updateSortKey = updateSortKey;
  }

  public ProjectType getProjectType() {
    return projectType;
  }

  public void setProjectType(ProjectType projectType) {
    this.projectType = projectType;
  }

  public int getProjectTypeSortKey() {
    return projectTypeSortKey;
  }

  public void setProjectTypeSortKey(int projectTypeSortKey) {
    this.projectTypeSortKey = projectTypeSortKey;
  }

  public PortalOrganisationUnit getPublishableOperator() {
    return publishableOperator;
  }

  public void setPublishableOperator(
      PortalOrganisationUnit publishableOperator) {
    this.publishableOperator = publishableOperator;
  }
}
