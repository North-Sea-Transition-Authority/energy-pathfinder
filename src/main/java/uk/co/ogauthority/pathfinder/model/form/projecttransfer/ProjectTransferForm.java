package uk.co.ogauthority.pathfinder.model.form.projecttransfer;

import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ProjectTransferForm {

  @NotNull(message = "Select a new operator", groups = FullValidation.class)
  private String newOrganisationGroup;

  @NotNull(message = "Enter the reason you are changing the operator", groups = FullValidation.class)
  private String transferReason;

  public String getNewOrganisationGroup() {
    return newOrganisationGroup;
  }

  public void setNewOrganisationGroup(String newOrganisationGroup) {
    this.newOrganisationGroup = newOrganisationGroup;
  }

  public String getTransferReason() {
    return transferReason;
  }

  public void setTransferReason(String transferReason) {
    this.transferReason = transferReason;
  }
}
