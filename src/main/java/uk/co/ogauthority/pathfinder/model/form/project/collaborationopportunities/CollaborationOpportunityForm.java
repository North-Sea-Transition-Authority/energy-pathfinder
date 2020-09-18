package uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class CollaborationOpportunityForm {

  @NotEmpty(message = "Select a collaboration opportunity function", groups = FullValidation.class)
  private String function;

  @NotEmpty(message = "Enter a description of the work", groups = FullValidation.class)
  private String descriptionOfWork;

  private ThreeFieldDateInput estimatedServiceDate;

  @Valid
  private ContactDetailForm contactDetail;

  public String getFunction() {
    return function;
  }

  public void setFunction(String function) {
    this.function = function;
  }

  public String getDescriptionOfWork() {
    return descriptionOfWork;
  }

  public void setDescriptionOfWork(String descriptionOfWork) {
    this.descriptionOfWork = descriptionOfWork;
  }

  public ThreeFieldDateInput getEstimatedServiceDate() {
    return estimatedServiceDate;
  }

  public void setEstimatedServiceDate(ThreeFieldDateInput estimatedServiceDate) {
    this.estimatedServiceDate = estimatedServiceDate;
  }

  public ContactDetailForm getContactDetail() {
    return contactDetail;
  }

  public void setContactDetail(ContactDetailForm contactDetail) {
    this.contactDetail = contactDetail;
  }

}
