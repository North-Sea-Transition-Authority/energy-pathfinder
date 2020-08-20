package uk.co.ogauthority.pathfinder.model.form.project.selectoperator;

import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;

public class ProjectOperatorForm {

  @NotNull(message = "Select an operator", groups = {FullValidation.class, PartialValidation.class})
  private String organisationGroup;

  public ProjectOperatorForm() {
  }

  public ProjectOperatorForm(String organisationGroup) {
    this.organisationGroup = organisationGroup;
  }

  public String getOrganisationGroup() {
    return organisationGroup;
  }

  public void setOrganisationGroup(String organisationGroup) {
    this.organisationGroup = organisationGroup;
  }
}
