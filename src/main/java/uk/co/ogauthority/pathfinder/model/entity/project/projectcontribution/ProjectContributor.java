package uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;

@Entity
@Table(name = "project_contributor")
public class ProjectContributor extends ProjectDetailEntity {

  @ManyToOne
  @JoinColumn(name = "cont_org_group_id")
  private PortalOrganisationGroup contributionOrganisationGroup;

  public ProjectContributor() {

  }

  public ProjectContributor(ProjectDetail projectDetail,
                            PortalOrganisationGroup portalOrganisationGroup) {
    this.projectDetail = projectDetail;
    this.contributionOrganisationGroup = portalOrganisationGroup;
  }

  public PortalOrganisationGroup getContributionOrganisationGroup() {
    return contributionOrganisationGroup;
  }

  public void setContributionOrganisationGroup(
      PortalOrganisationGroup contributionOrganisationGroup) {
    this.contributionOrganisationGroup = contributionOrganisationGroup;
  }
}
