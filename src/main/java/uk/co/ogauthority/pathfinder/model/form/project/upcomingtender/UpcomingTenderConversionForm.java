package uk.co.ogauthority.pathfinder.model.form.project.upcomingtender;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedString;

public class UpcomingTenderConversionForm {

  @NotEmpty(message = "Enter a contractor name", groups = FullValidation.class)
  @LengthRestrictedString(messagePrefix = "The contractor name", groups = FullValidation.class)
  private String contractorName;

  private ThreeFieldDateInput dateAwarded;

  @Valid
  private ContactDetailForm contactDetail;

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

  public ContactDetailForm getContactDetail() {
    return contactDetail;
  }

  public void setContactDetail(ContactDetailForm contactDetail) {
    this.contactDetail = contactDetail;
  }
}
