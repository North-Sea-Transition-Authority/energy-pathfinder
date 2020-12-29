package uk.co.ogauthority.pathfinder.model.view.projecttransfer;

import java.util.Objects;

public class ProjectTransferView {

  private String oldOperator;

  private String newOperator;

  private String transferReason;

  private String transferDate;

  private String transferredByUserName;

  private String transferredByUserEmailAddress;

  public String getOldOperator() {
    return oldOperator;
  }

  public void setOldOperator(String oldOperator) {
    this.oldOperator = oldOperator;
  }

  public String getNewOperator() {
    return newOperator;
  }

  public void setNewOperator(String newOperator) {
    this.newOperator = newOperator;
  }

  public String getTransferReason() {
    return transferReason;
  }

  public void setTransferReason(String transferReason) {
    this.transferReason = transferReason;
  }

  public String getTransferDate() {
    return transferDate;
  }

  public void setTransferDate(String transferDate) {
    this.transferDate = transferDate;
  }

  public String getTransferredByUserName() {
    return transferredByUserName;
  }

  public void setTransferredByUserName(String transferredByUserName) {
    this.transferredByUserName = transferredByUserName;
  }

  public String getTransferredByUserEmailAddress() {
    return transferredByUserEmailAddress;
  }

  public void setTransferredByUserEmailAddress(String transferredByUserEmailAddress) {
    this.transferredByUserEmailAddress = transferredByUserEmailAddress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectTransferView that = (ProjectTransferView) o;
    return Objects.equals(getOldOperator(), that.getOldOperator())
        && Objects.equals(getNewOperator(), that.getNewOperator())
        && Objects.equals(getTransferReason(), that.getTransferReason())
        && Objects.equals(getTransferDate(), that.getTransferDate())
        && Objects.equals(getTransferredByUserName(), that.getTransferredByUserName())
        && Objects.equals(getTransferredByUserEmailAddress(), that.getTransferredByUserEmailAddress());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getOldOperator(),
        getNewOperator(),
        getTransferReason(),
        getTransferDate(),
        getTransferredByUserName(),
        getTransferredByUserEmailAddress()
    );
  }
}
