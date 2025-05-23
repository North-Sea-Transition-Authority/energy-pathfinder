package uk.co.ogauthority.pathfinder.model.form.projectupdate;

import jakarta.validation.constraints.NotEmpty;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;

public class RequestUpdateForm {

  @NotEmpty(message = "Enter the reason for the update")
  private String updateReason;

  private ThreeFieldDateInput deadlineDate;

  public String getUpdateReason() {
    return updateReason;
  }

  public void setUpdateReason(String updateReason) {
    this.updateReason = updateReason;
  }

  public ThreeFieldDateInput getDeadlineDate() {
    return deadlineDate;
  }

  public void setDeadlineDate(ThreeFieldDateInput deadlineDate) {
    this.deadlineDate = deadlineDate;
  }
}
