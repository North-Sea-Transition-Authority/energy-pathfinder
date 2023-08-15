package uk.co.ogauthority.pathfinder.model.form.project.upcomingtender;

import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedString;

public class UpcomingTenderConversionForm {

  @LengthRestrictedString(messagePrefix = "The contractor name", groups = {PartialValidation.class})
  private String contractorName;

  private ThreeFieldDateInput dateAwarded;

  public String getContractorName() {
    return contractorName;
  }

  public void setContractorName(String contractorName) {
    this.contractorName = contractorName;
  }

  public ThreeFieldDateInput getDateAwarded() {
    return dateAwarded;
  }

  public void setDateAwarded(ThreeFieldDateInput dateAwarded) {
    this.dateAwarded = dateAwarded;
  }
}
