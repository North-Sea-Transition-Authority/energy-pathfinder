package uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;

@Entity
@Table(name = "project_contributor")
public class ProjectContributor extends ProjectDetailEntity implements Serializable {

  private static final long serialVersionUID = -660335873665638108L;

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

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProjectContributor)) {
      return false;
    }
    if (o == this) {
      return true;
    }
    ProjectContributor that = (ProjectContributor) o;

    return this.projectDetail.equals(that.getProjectDetail())
        && this.contributionOrganisationGroup.equals(that.getContributionOrganisationGroup());
  }

  @Override
  public int hashCode() {
    return Objects.hash(contributionOrganisationGroup, projectDetail);
  }
}
