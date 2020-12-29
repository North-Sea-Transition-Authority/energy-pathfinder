package uk.co.ogauthority.pathfinder.model.form.projecttransfer;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.form.validation.ValidationHint;

public class ProjectTransferValidationHint implements ValidationHint {

  private final PortalOrganisationGroup currentOrganisationGroup;

  public ProjectTransferValidationHint(
      PortalOrganisationGroup currentOrganisationGroup) {
    this.currentOrganisationGroup = currentOrganisationGroup;
  }

  @Override
  public boolean isValid(Object objectToTest) {
    return true;
  }

  @Override
  public String getErrorMessage() {
    return null;
  }

  @Override
  public String getCode() {
    return null;
  }

  public PortalOrganisationGroup getCurrentOrganisationGroup() {
    return currentOrganisationGroup;
  }
}
