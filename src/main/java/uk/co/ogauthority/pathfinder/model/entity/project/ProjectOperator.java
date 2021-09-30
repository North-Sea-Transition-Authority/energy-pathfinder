package uk.co.ogauthority.pathfinder.model.entity.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationUnit;

@Entity
@Table(name = "project_operators")
public class ProjectOperator extends ProjectDetailEntity {

  @ManyToOne
  @JoinColumn(name = "operator_org_grp_id")
  private PortalOrganisationGroup organisationGroup;

  @Column(name = "publish_as_project_operator")
  private Boolean isPublishedAsOperator;

  @ManyToOne
  @JoinColumn(name = "publishable_org_unit_id")
  private PortalOrganisationUnit publishableOrganisationUnit;

  public ProjectOperator() {
  }

  public ProjectOperator(ProjectDetail detail,
                         PortalOrganisationGroup organisationGroup) {
    this.projectDetail = detail;
    this.organisationGroup = organisationGroup;
  }

  public ProjectOperator(ProjectDetail detail) {
    this.projectDetail = detail;
  }

  public PortalOrganisationGroup getOrganisationGroup() {
    return organisationGroup;
  }

  public void setOrganisationGroup(PortalOrganisationGroup organisationGroup) {
    this.organisationGroup = organisationGroup;
  }

  public Boolean isPublishedAsOperator() {
    return isPublishedAsOperator;
  }

  public void setIsPublishedAsOperator(Boolean isPublishedAsOperator) {
    this.isPublishedAsOperator = isPublishedAsOperator;
  }

  public PortalOrganisationUnit getPublishableOrganisationUnit() {
    return publishableOrganisationUnit;
  }

  public void setPublishableOrganisationUnit(PortalOrganisationUnit publishableOrganisationUnit) {
    this.publishableOrganisationUnit = publishableOrganisationUnit;
  }
}
