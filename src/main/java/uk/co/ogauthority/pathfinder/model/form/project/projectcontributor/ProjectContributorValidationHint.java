package uk.co.ogauthority.pathfinder.model.form.project.projectcontributor;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;

public class ProjectContributorValidationHint {

  private final ValidationType validationType;

  private final ProjectDetail projectDetail;

  public ProjectContributorValidationHint(ValidationType validationType,
                                          ProjectDetail projectDetail) {
    this.validationType = validationType;
    this.projectDetail = projectDetail;
  }

  public ValidationType getValidationType() {
    return validationType;
  }

  public ProjectDetail getProjectDetail() {
    return projectDetail;
  }
}