package uk.co.ogauthority.pathfinder.model.form.project.selectoperator;

import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;

public class SelectOperatorForm {

  @NotNull(message = "Select an operator", groups = {FullValidation.class, PartialValidation.class})
  private String organisationGroup;

  public SelectOperatorForm() {
  }

  public SelectOperatorForm(String organisationGroup) {
    this.organisationGroup = organisationGroup;
  }

  public String getOrganisationGroup() {
    return organisationGroup;
  }

  public void setOrganisationGroup(String organisationGroup) {
    this.organisationGroup = organisationGroup;
  }
}
