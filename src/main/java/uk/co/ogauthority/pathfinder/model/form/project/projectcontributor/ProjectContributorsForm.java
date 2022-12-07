package uk.co.ogauthority.pathfinder.model.form.project.projectcontributor;

import java.util.List;

public class ProjectContributorsForm {

  List<Integer> contributors;

  String contributorsSelect;

  public List<Integer> getContributors() {
    return contributors;
  }

  public void setContributors(List<Integer> contributors) {
    this.contributors = contributors;
  }

  public String getContributorsSelect() {
    return contributorsSelect;
  }

  public void setContributorsSelect(String contributorsSelect) {
    this.contributorsSelect = contributorsSelect;
  }
}
