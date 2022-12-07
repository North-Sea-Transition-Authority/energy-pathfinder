package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;

public class ProjectContributorTestUtil {

  private ProjectContributorTestUtil() {
    throw new IllegalStateException("ProjectContributorTestUtil a utility class and should not be instantiated");
  }

  public static ProjectContributor contributorWithGroupOrgId(ProjectDetail detail, int organisationId) {
    return contributorWithGroupOrgIdAndName(detail, organisationId, "name");
  }

  public static ProjectContributor contributorWithGroupOrg(ProjectDetail detail, PortalOrganisationGroup portalOrganisationGroup) {
    return new ProjectContributor(detail, portalOrganisationGroup);
  }

  public static ProjectContributor contributorWithGroupOrgIdAndName(ProjectDetail detail, int organisationId, String name) {
    return new ProjectContributor(detail, TeamTestingUtil.generateOrganisationGroup(organisationId, name, name));
  }
}
