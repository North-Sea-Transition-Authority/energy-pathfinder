package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;

public class ProjectContributorTestUtil {

  private ProjectContributorTestUtil() {
    throw new IllegalStateException("ProjectContributorTestUtil a utility class and should not be instantiated");
  }

  public static ProjectContributor contributorWithGroupOrgId(ProjectDetail detail, int organisationId) {
    return new ProjectContributor(detail, TeamTestingUtil.generateOrganisationGroup(1, "group", "grp"));
  }
}
