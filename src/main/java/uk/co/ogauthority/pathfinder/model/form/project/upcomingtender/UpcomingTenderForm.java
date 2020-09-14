package uk.co.ogauthority.pathfinder.model.form.project.upcomingtender;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class UpcomingTenderForm {

  @NotEmpty(message = "Select a tender function", groups = FullValidation.class)
  private String tenderFunction;

  @NotEmpty(message = "Enter a description of the work", groups = FullValidation.class)
  private String descriptionOfWork;

  private ThreeFieldDateInput estimatedTenderDate;

  @NotNull(message = "Select a contract band", groups = FullValidation.class)
  private ContractBand contractBand;

  @Valid
  private ContactDetailForm contactDetail;

  public String getTenderFunction() {
    return tenderFunction;
  }

  public void setTenderFunction(String tenderFunction) {
    this.tenderFunction = tenderFunction;
  }

  public String getDescriptionOfWork() {
    return descriptionOfWork;
  }

  public void setDescriptionOfWork(String descriptionOfWork) {
    this.descriptionOfWork = descriptionOfWork;
  }

  public ThreeFieldDateInput getEstimatedTenderDate() {
    return estimatedTenderDate;
  }

  public void setEstimatedTenderDate(ThreeFieldDateInput estimatedTenderDate) {
    this.estimatedTenderDate = estimatedTenderDate;
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
