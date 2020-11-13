package uk.co.ogauthority.pathfinder.model.view.projectoperator;

import java.util.Objects;

public class ProjectOperatorView {

  private String organisationGroupName;

  public String getOrganisationGroupName() {
    return organisationGroupName;
  }

  public void setOrganisationGroupName(String organisationGroupName) {
    this.organisationGroupName = organisationGroupName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ProjectOperatorView projectOperatorView = (ProjectOperatorView) o;

    return Objects.equals(organisationGroupName, projectOperatorView.getOrganisationGroupName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(organisationGroupName);
  }
}
