package uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan;

import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ForwardWorkPlanAwardedContractSummaryForm {

  @NotNull(
      message = "Select yes if you want to add another awarded contract",
      groups = FullValidation.class
  )
  private Boolean hasOtherContractsToAdd;

  public Boolean getHasOtherContractsToAdd() {
    return hasOtherContractsToAdd;
  }

  public void setHasOtherContractsToAdd(Boolean hasOtherContractsToAdd) {
    this.hasOtherContractsToAdd = hasOtherContractsToAdd;
  }
}
