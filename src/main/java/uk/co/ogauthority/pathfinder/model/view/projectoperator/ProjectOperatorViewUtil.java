package uk.co.ogauthority.pathfinder.model.view.projectoperator;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;

public class ProjectOperatorViewUtil {

  private ProjectOperatorViewUtil() {
    throw new IllegalStateException("ProjectOperatorViewUtil is a util class and should not be instantiated");
  }

  public static ProjectOperatorView from(ProjectOperator projectOperator) {
    var projectOperatorView = new ProjectOperatorView();

    final var organisationGroup = projectOperator.getOrganisationGroup();

    projectOperatorView.setOrganisationGroupName(
        (organisationGroup != null)
            ? organisationGroup.getName()
            : ""
    );

    return projectOperatorView;
  }
}
