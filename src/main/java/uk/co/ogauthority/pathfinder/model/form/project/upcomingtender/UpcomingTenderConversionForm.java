package uk.co.ogauthority.pathfinder.model.form.project.upcomingtender;

import javax.validation.constraints.NotEmpty;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedString;

public class UpcomingTenderConversionForm {

  @NotEmpty(message = "Enter a contractor name", groups = FullValidation.class)
  @LengthRestrictedString(messagePrefix = "The contractor name", groups = {FullValidation.class, PartialValidation.class})
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
