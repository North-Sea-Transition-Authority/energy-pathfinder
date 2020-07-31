package uk.co.ogauthority.pathfinder.model.entity.project;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;

@Entity
@Table(name = "project_operators")
public class ProjectOperator {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "project_details_id")
  private ProjectDetail projectDetail;


  @ManyToOne
  @JoinColumn(name = "operator_org_grp_id")
  private PortalOrganisationGroup organisationGroup;

  public ProjectOperator() {
  }

  public ProjectOperator(ProjectDetail projectDetail,
                         PortalOrganisationGroup organisationGroup) {
    this.projectDetail = projectDetail;
    this.organisationGroup = organisationGroup;
  }

  public ProjectDetail getProjectDetail() {
    return projectDetail;
  }

  public void setProjectDetail(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public PortalOrganisationGroup getOrganisationGroup() {
    return organisationGroup;
  }

  public void setOrganisationGroup(
      PortalOrganisationGroup organisationGroup) {
    this.organisationGroup = organisationGroup;
  }
}
