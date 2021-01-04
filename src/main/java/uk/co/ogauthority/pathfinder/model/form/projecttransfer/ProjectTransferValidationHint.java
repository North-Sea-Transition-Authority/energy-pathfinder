package uk.co.ogauthority.pathfinder.model.form.projecttransfer;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;

public class ProjectTransferValidationHint {

  private final PortalOrganisationGroup currentOrganisationGroup;

  public ProjectTransferValidationHint(
      PortalOrganisationGroup currentOrganisationGroup) {
    this.currentOrganisationGroup = currentOrganisationGroup;
  }

  public PortalOrganisationGroup getCurrentOrganisationGroup() {
    return currentOrganisationGroup;
  }
}
