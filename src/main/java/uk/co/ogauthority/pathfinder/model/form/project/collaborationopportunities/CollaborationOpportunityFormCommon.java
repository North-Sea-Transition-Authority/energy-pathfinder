package uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadMultipleFilesWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public abstract class CollaborationOpportunityFormCommon extends UploadMultipleFilesWithDescriptionForm {

  @NotEmpty(message = "Select a collaboration opportunity function", groups = FullValidation.class)
  private String function;

  @NotEmpty(message = "Enter a description of the work", groups = FullValidation.class)
  private String descriptionOfWork;

  @NotNull(message = "Select if an urgent response is required", groups = FullValidation.class)
  private Boolean urgentResponseNeeded;

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

  public Boolean getUrgentResponseNeeded() {
    return urgentResponseNeeded;
  }

  public void setUrgentResponseNeeded(Boolean urgentResponseNeeded) {
    this.urgentResponseNeeded = urgentResponseNeeded;
  }

  public ContactDetailForm getContactDetail() {
    return contactDetail;
  }

  public void setContactDetail(ContactDetailForm contactDetail) {
    this.contactDetail = contactDetail;
  }

}
