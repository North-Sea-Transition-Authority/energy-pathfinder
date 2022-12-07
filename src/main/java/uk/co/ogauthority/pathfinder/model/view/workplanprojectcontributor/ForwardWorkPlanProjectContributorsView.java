package uk.co.ogauthority.pathfinder.model.view.workplanprojectcontributor;

import java.util.List;
import uk.co.ogauthority.pathfinder.model.view.projectcontributor.ProjectContributorsView;

public class ForwardWorkPlanProjectContributorsView extends ProjectContributorsView {

  private Boolean hasProjectContributors;

  public ForwardWorkPlanProjectContributorsView(List<String> organisationGroupNames,
                                                Boolean hasProjectContributors) {
    super(organisationGroupNames);
    this.hasProjectContributors = hasProjectContributors;
  }

  public Boolean getHasProjectContributors() {
    return hasProjectContributors;
  }

  public void setHasProjectContributors(Boolean hasProjectContributors) {
    this.hasProjectContributors = hasProjectContributors;
  }
}
