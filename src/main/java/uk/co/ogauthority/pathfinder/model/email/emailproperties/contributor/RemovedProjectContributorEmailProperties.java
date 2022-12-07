package uk.co.ogauthority.pathfinder.model.email.emailproperties.contributor;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;

public class RemovedProjectContributorEmailProperties extends ProjectContributorEmailProperties {

  public RemovedProjectContributorEmailProperties(String recipientIdentifier,
                                                  ProjectDetail detail,
                                                  ProjectOperator projectOperator,
                                                  String projectTitle) {
    super(
        recipientIdentifier,
        detail,
        projectOperator,
        projectTitle,
        NotifyTemplate.REMOVED_PROJECT_CONTRIBUTOR
    );
  }
}
