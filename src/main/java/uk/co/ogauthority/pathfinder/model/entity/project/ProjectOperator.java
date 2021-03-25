package uk.co.ogauthority.pathfinder.model.entity.project;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;

@Entity
@Table(name = "project_operators")
public class ProjectOperator extends ProjectDetailEntity {

  @ManyToOne
  @JoinColumn(name = "operator_org_grp_id")
  private PortalOrganisationGroup organisationGroup;

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
}
