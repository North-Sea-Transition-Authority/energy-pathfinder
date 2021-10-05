package uk.co.ogauthority.pathfinder.service.project;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationUnit;

public class ProjectOperatorDisplayNameUtil {

  private ProjectOperatorDisplayNameUtil() {
    throw new IllegalStateException("ProjectOperatorDisplayNameUtil is a utility class and should not be instantiated");
  }

  public static String getProjectOperatorDisplayName(PortalOrganisationGroup projectOperator,
                                                     PortalOrganisationUnit publishableOrganisation) {
    return publishableOrganisation != null
        ? String.format("%s (%s)", projectOperator.getName(), publishableOrganisation.getName())
        : projectOperator.getName();
  }
}
