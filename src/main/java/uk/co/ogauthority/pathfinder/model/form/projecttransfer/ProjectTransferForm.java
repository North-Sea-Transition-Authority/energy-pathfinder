package uk.co.ogauthority.pathfinder.model.form.projecttransfer;

import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ProjectTransferForm {

  @NotNull(message = "Select a new operator/developer", groups = FullValidation.class)
  private String newOrganisationGroup;

  @NotNull(message = "Enter the reason you are changing the operator/developer", groups = FullValidation.class)
  private String transferReason;

  @NotNull(
      message = "Select if this is the operator/developer you want shown on the supply chain interface",
      groups = FullValidation.class
  )
  private Boolean isPublishedAsOperator;

  private String publishableOrganisation;

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

  public Boolean isPublishedAsOperator() {
    return isPublishedAsOperator;
  }

  public Boolean getIsPublishedAsOperator() {
    return isPublishedAsOperator();
  }

  public void setIsPublishedAsOperator(Boolean isPublishedAsOperator) {
    this.isPublishedAsOperator = isPublishedAsOperator;
  }

  public String getPublishableOrganisation() {
    return publishableOrganisation;
  }

  public void setPublishableOrganisation(String publishableOrganisation) {
    this.publishableOrganisation = publishableOrganisation;
  }
}
