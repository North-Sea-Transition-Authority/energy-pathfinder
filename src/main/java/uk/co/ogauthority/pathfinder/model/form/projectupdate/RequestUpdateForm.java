package uk.co.ogauthority.pathfinder.model.form.projectupdate;

import javax.validation.constraints.NotEmpty;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedString;

public class RequestUpdateForm {

  @NotEmpty(message = "Enter the reason for the update")
  @LengthRestrictedString(messagePrefix = "The reason for the update")
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
