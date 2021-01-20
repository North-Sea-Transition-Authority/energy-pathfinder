package uk.co.ogauthority.pathfinder.model.view.projectupdate;

import java.util.Objects;

public class NoUpdateNotificationView {

  private String supplyChainReason;

  private String regulatorReason;

  private String submittedDate;

  private String submittedByUserName;

  private String submittedByUserEmailAddress;

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

  public String getSubmittedDate() {
    return submittedDate;
  }

  public void setSubmittedDate(String submittedDate) {
    this.submittedDate = submittedDate;
  }

  public String getSubmittedByUserName() {
    return submittedByUserName;
  }

  public void setSubmittedByUserName(String submittedByUserName) {
    this.submittedByUserName = submittedByUserName;
  }

  public String getSubmittedByUserEmailAddress() {
    return submittedByUserEmailAddress;
  }

  public void setSubmittedByUserEmailAddress(String submittedByUserEmailAddress) {
    this.submittedByUserEmailAddress = submittedByUserEmailAddress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NoUpdateNotificationView that = (NoUpdateNotificationView) o;
    return Objects.equals(supplyChainReason, that.supplyChainReason)
        && Objects.equals(regulatorReason, that.regulatorReason)
        && Objects.equals(submittedDate, that.submittedDate)
        && Objects.equals(submittedByUserName, that.submittedByUserName)
        && Objects.equals(submittedByUserEmailAddress, that.submittedByUserEmailAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        supplyChainReason,
        regulatorReason,
        submittedDate,
        submittedByUserName,
        submittedByUserEmailAddress
    );
  }
}
