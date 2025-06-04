package uk.co.ogauthority.pathfinder.model.form.communication;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class OrganisationGroupSelectorForm {

  @NotEmpty(message = "You must select at least one operator/developer", groups = FullValidation.class)
  private List<Integer> organisationGroups;

  public List<Integer> getOrganisationGroups() {
    return organisationGroups;
  }

  public void setOrganisationGroups(List<Integer> organisationGroups) {
    this.organisationGroups = organisationGroups;
  }
}
