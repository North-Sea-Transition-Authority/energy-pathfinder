package uk.co.ogauthority.pathfinder.model.view.projectupdate;

import java.util.Objects;

public class RegulatorUpdateRequestView {

  private String updateReason;

  private String deadlineDate;

  private String requestedDate;

  private String requestedByUserName;

  private String requestedByUserEmailAddress;

  public String getUpdateReason() {
    return updateReason;
  }

  public void setUpdateReason(String updateReason) {
    this.updateReason = updateReason;
  }

  public String getDeadlineDate() {
    return deadlineDate;
  }

  public void setDeadlineDate(String deadlineDate) {
    this.deadlineDate = deadlineDate;
  }

  public String getRequestedDate() {
    return requestedDate;
  }

  public void setRequestedDate(String requestedDate) {
    this.requestedDate = requestedDate;
  }

  public String getRequestedByUserName() {
    return requestedByUserName;
  }

  public void setRequestedByUserName(String requestedByUserName) {
    this.requestedByUserName = requestedByUserName;
  }

  public String getRequestedByUserEmailAddress() {
    return requestedByUserEmailAddress;
  }

  public void setRequestedByUserEmailAddress(String requestedByUserEmailAddress) {
    this.requestedByUserEmailAddress = requestedByUserEmailAddress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegulatorUpdateRequestView that = (RegulatorUpdateRequestView) o;
    return Objects.equals(updateReason, that.updateReason)
        && Objects.equals(deadlineDate, that.deadlineDate)
        && Objects.equals(requestedDate, that.requestedDate)
        && Objects.equals(requestedByUserName, that.requestedByUserName)
        && Objects.equals(requestedByUserEmailAddress, that.requestedByUserEmailAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(updateReason, requestedDate, deadlineDate, requestedByUserName, requestedByUserEmailAddress);
  }
}
