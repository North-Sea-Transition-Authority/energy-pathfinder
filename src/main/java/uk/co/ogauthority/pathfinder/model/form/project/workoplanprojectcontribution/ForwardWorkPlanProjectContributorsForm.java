package uk.co.ogauthority.pathfinder.model.form.project.workoplanprojectcontribution;

import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsForm;

public class ForwardWorkPlanProjectContributorsForm extends ProjectContributorsForm {

  private Boolean hasProjectContributors;

  public Boolean getHasProjectContributors() {
    return hasProjectContributors;
  }

  public void setHasProjectContributors(Boolean hasProjectContributors) {
    this.hasProjectContributors = hasProjectContributors;
  }
}
