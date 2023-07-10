package uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan;

import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ForwardWorkPlanAwardedContractSetupForm {

  @NotNull(message = "Select yes if you have any upcoming tenders to add", groups = FullValidation.class)
  private Boolean hasContractToAdd;

  public Boolean getHasContractToAdd() {
    return hasContractToAdd;
  }

  public void setHasContractToAdd(Boolean hasContractToAdd) {
    this.hasContractToAdd = hasContractToAdd;
  }
}
