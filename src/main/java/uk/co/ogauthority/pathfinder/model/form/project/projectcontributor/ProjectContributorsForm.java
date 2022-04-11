package uk.co.ogauthority.pathfinder.model.form.project.projectcontributor;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ProjectContributorsForm {

  @NotEmpty(message = "You must select at least one operator", groups = FullValidation.class)
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
