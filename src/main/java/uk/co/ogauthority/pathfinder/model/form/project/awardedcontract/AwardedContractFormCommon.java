package uk.co.ogauthority.pathfinder.model.form.project.awardedcontract;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedString;

public abstract class AwardedContractFormCommon {

  @NotEmpty(message = "Enter a contractor name", groups = FullValidation.class)
  @LengthRestrictedString(messagePrefix = "The contractor name", groups = {FullValidation.class, PartialValidation.class})
  private String contractorName;

  @NotEmpty(message = "Select a function for the contract", groups = FullValidation.class)
  private String contractFunction;

  @NotEmpty(message = "Enter a description of the work", groups = FullValidation.class)
  private String descriptionOfWork;

  private ThreeFieldDateInput dateAwarded;

  @NotNull(message = "Select a contract band", groups = FullValidation.class)
  private ContractBand contractBand;

  @Valid
  private ContactDetailForm contactDetail;

  public String getContractorName() {
    return contractorName;
  }

  public void setContractorName(String contractorName) {
    this.contractorName = contractorName;
  }

  public String getContractFunction() {
    return contractFunction;
  }

  public void setContractFunction(String contractFunction) {
    this.contractFunction = contractFunction;
  }

  public String getDescriptionOfWork() {
    return descriptionOfWork;
  }

  public void setDescriptionOfWork(String descriptionOfWork) {
    this.descriptionOfWork = descriptionOfWork;
  }

  public ThreeFieldDateInput getDateAwarded() {
    return dateAwarded;
  }

  public void setDateAwarded(ThreeFieldDateInput dateAwarded) {
    this.dateAwarded = dateAwarded;
  }

  public ContractBand getContractBand() {
    return contractBand;
  }

  public void setContractBand(ContractBand contractBand) {
    this.contractBand = contractBand;
  }

  public ContactDetailForm getContactDetail() {
    return contactDetail;
  }

  public void setContactDetail(ContactDetailForm contactDetail) {
    this.contactDetail = contactDetail;
  }
}
