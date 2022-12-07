package uk.co.ogauthority.pathfinder.model.view.projectcontributor;

import java.util.List;

public class ProjectContributorsView {

  private List<String> organisationGroupNames;

  public ProjectContributorsView(List<String> organisationGroupNames) {
    this.organisationGroupNames = organisationGroupNames;
  }

  public List<String> getOrganisationGroupNames() {
    return organisationGroupNames;
  }

  public void setOrganisationGroupNames(List<String> organisationGroupNames) {
    this.organisationGroupNames = organisationGroupNames;
  }
}
