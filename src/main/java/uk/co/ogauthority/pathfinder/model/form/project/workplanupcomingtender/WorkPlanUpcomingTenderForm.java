package uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.project.WorkPlanUpcomingTenderContractBand;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class WorkPlanUpcomingTenderForm {

  @NotEmpty(message = "Select a department", groups = FullValidation.class)
  private String departmentType;

  @NotEmpty(message = "Enter a description of the work", groups = FullValidation.class)
  private String descriptionOfWork;

  private ThreeFieldDateInput estimatedTenderDate;

  @NotNull(message = "Select a contract band", groups = FullValidation.class)
  private WorkPlanUpcomingTenderContractBand contractBand;

  @Valid
  private ContactDetailForm contactDetail;

  public String getDepartmentType() {
    return departmentType;
  }

  public void setDepartmentType(String departmentType) {
    this.departmentType = departmentType;
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

  public void setEstimatedTenderDate(
      ThreeFieldDateInput estimatedTenderDate) {
    this.estimatedTenderDate = estimatedTenderDate;
  }

  public WorkPlanUpcomingTenderContractBand getContractBand() {
    return contractBand;
  }

  public void setContractBand(WorkPlanUpcomingTenderContractBand contractBand) {
    this.contractBand = contractBand;
  }

  public ContactDetailForm getContactDetail() {
    return contactDetail;
  }

  public void setContactDetail(ContactDetailForm contactDetail) {
    this.contactDetail = contactDetail;
  }
}
