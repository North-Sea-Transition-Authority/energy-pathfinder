package uk.co.ogauthority.pathfinder.model.form.project.upcomingtender;

import javax.validation.constraints.NotEmpty;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.form.forminput.twofielddateinput.TwoFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.email.ValidEmail;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedString;
import uk.co.ogauthority.pathfinder.model.form.validation.phonenumber.ValidPhoneNumber;

public class UpcomingTenderForm {

  @NotEmpty(message = "Select a tender function", groups = FullValidation.class)
  private String tenderFunction;

  @NotEmpty(message = "Enter a description of the work", groups = FullValidation.class)
  private String descriptionOfWork;

  private TwoFieldDateInput estimatedTenderDate;

  private ContractBand contractBand;

  @LengthRestrictedString(messagePrefix = "The contact name", groups = {FullValidation.class, PartialValidation.class})
  @NotEmpty(message = "Enter a contact name", groups = FullValidation.class)
  private String name;

  @NotEmpty(message = "Enter a telephone number", groups = FullValidation.class)
  @ValidPhoneNumber(messagePrefix = "The contact telephone number", groups = {FullValidation.class, PartialValidation.class})
  private String phoneNumber;

  @LengthRestrictedString(messagePrefix = "The contact job title", groups = {FullValidation.class, PartialValidation.class})
  @NotEmpty(message = "Enter a job title", groups = FullValidation.class)
  private String jobTitle;

  @NotEmpty(message = "Enter an email address", groups = FullValidation.class)
  @ValidEmail(messagePrefix = "The contact email", groups = {FullValidation.class, PartialValidation.class})
  private String emailAddress;

  public UpcomingTenderForm() {
  }

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

  public TwoFieldDateInput getEstimatedTenderDate() {
    return estimatedTenderDate;
  }

  public void setEstimatedTenderDate(
      TwoFieldDateInput estimatedTenderDate) {
    this.estimatedTenderDate = estimatedTenderDate;
  }

  public ContractBand getContractBand() {
    return contractBand;
  }

  public void setContractBand(ContractBand contractBand) {
    this.contractBand = contractBand;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }
}
