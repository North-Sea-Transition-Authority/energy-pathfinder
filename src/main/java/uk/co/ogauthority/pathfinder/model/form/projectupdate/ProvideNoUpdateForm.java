package uk.co.ogauthority.pathfinder.model.form.projectupdate;

import jakarta.validation.constraints.NotEmpty;

public class ProvideNoUpdateForm {

  @NotEmpty(message = "Enter a reason for the supply chain as to why no changes are required")
  private String supplyChainReason;

  private String regulatorReason;

  public String getSupplyChainReason() {
    return supplyChainReason;
  }

  public void setSupplyChainReason(String supplyChainReason) {
    this.supplyChainReason = supplyChainReason;
  }

  public String getRegulatorReason() {
    return regulatorReason;
  }

  public void setRegulatorReason(String regulatorReason) {
    this.regulatorReason = regulatorReason;
  }
}
