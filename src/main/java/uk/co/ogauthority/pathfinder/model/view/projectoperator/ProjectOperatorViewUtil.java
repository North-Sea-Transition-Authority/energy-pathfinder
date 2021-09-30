package uk.co.ogauthority.pathfinder.model.view.projectoperator;

import org.apache.commons.lang3.BooleanUtils;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationUnit;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class ProjectOperatorViewUtil {

  private ProjectOperatorViewUtil() {
    throw new IllegalStateException("ProjectOperatorViewUtil is a util class and should not be instantiated");
  }

  public static ProjectOperatorView from(ProjectOperator projectOperator) {
    var projectOperatorView = new ProjectOperatorView();

    final var organisationGroup = projectOperator.getOrganisationGroup();

    projectOperatorView.setOperatorName(
        (organisationGroup != null)
            ? organisationGroup.getName()
            : ""
    );

    final var isPublishedAsOperatorDisplayString = getIsPublishedAsOperatorDisplayString(projectOperator.isPublishedAsOperator());
    projectOperatorView.setIsPublishedAsOperator(isPublishedAsOperatorDisplayString);

    final var publishableOrganisationName = getPublishableOrganisationName(
        projectOperator.isPublishedAsOperator(),
        projectOperator.getPublishableOrganisationUnit()
    );
    projectOperatorView.setPublishableOrganisationName(publishableOrganisationName);

    return projectOperatorView;
  }

  public static String getIsPublishedAsOperatorDisplayString(Boolean isPublishableAsOperator) {
    return StringDisplayUtil.yesNoFromBoolean(isPublishableAsOperator);
  }

  public static String getPublishableOrganisationName(Boolean isPublishedAsOperator,
                                                      PortalOrganisationUnit publishableOrganisation) {
    return BooleanUtils.isFalse(isPublishedAsOperator) && publishableOrganisation != null
        ? publishableOrganisation.getName()
        : "";
  }
}
