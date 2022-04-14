package uk.co.ogauthority.pathfinder.model.form.project.projectcontributor;

import uk.co.ogauthority.pathfinder.model.enums.ValidationType;

public class ProjectContributorValidationHint {

  private final ValidationType validationType;

  public ProjectContributorValidationHint(ValidationType validationType) {
    this.validationType = validationType;
  }

  public ValidationType getValidationType() {
    return validationType;
  }
}