package uk.co.ogauthority.pathfinder.model.form.projectupdate;

import javax.validation.constraints.NotEmpty;

public class ProvideNoUpdateForm {

  @NotEmpty(message = "Enter the reason no changes are required")
  private String reasonNoUpdateRequired;

  public String getReasonNoUpdateRequired() {
    return reasonNoUpdateRequired;
  }

  public void setReasonNoUpdateRequired(String reasonNoUpdateRequired) {
    this.reasonNoUpdateRequired = reasonNoUpdateRequired;
  }
}
