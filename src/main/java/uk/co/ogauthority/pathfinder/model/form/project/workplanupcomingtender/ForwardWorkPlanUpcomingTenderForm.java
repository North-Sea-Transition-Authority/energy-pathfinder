package uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.enums.project.WorkPlanUpcomingTenderContractBand;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.positivewholenumber.PositiveWholeNumberGreaterThanZero;

public class ForwardWorkPlanUpcomingTenderForm {

  @NotEmpty(message = "Select a department", groups = FullValidation.class)
  private String departmentType;

  @NotEmpty(message = "Enter a description of the work", groups = FullValidation.class)
  private String descriptionOfWork;

  private QuarterYearInput estimatedTenderStartDate;

  @NotNull(message = "Select a contract band", groups = FullValidation.class)
  private WorkPlanUpcomingTenderContractBand contractBand;

  @NotNull(message = "Select if the contract length is measured in days, weeks, months or years", groups = FullValidation.class)
  private DurationPeriod contractTermDurationPeriod;

  @PositiveWholeNumberGreaterThanZero(
      messagePrefix = "Contract length",
      groups = { FullValidation.class, PartialValidation.class }
  )
  private Integer contractTermDayDuration;

  @PositiveWholeNumberGreaterThanZero(
      messagePrefix = "Contract length",
      groups = { FullValidation.class, PartialValidation.class }
  )
  private Integer contractTermWeekDuration;

  @PositiveWholeNumberGreaterThanZero(
      messagePrefix = "Contract length",
      groups = { FullValidation.class, PartialValidation.class }
  )
  private Integer contractTermMonthDuration;

  @PositiveWholeNumberGreaterThanZero(
      messagePrefix = "Contract length",
      groups = { FullValidation.class, PartialValidation.class }
  )
  private Integer contractTermYearDuration;

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

  public QuarterYearInput getEstimatedTenderStartDate() {
    return estimatedTenderStartDate;
  }

  public void setEstimatedTenderStartDate(
      QuarterYearInput estimatedTenderStartDate) {
    this.estimatedTenderStartDate = estimatedTenderStartDate;
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

  public DurationPeriod getContractTermDurationPeriod() {
    return contractTermDurationPeriod;
  }

  public void setContractTermDurationPeriod(
      DurationPeriod contractTermDurationPeriod) {
    this.contractTermDurationPeriod = contractTermDurationPeriod;
  }

  public Integer getContractTermDayDuration() {
    return contractTermDayDuration;
  }

  public void setContractTermDayDuration(Integer contractTermDayDuration) {
    this.contractTermDayDuration = contractTermDayDuration;
  }

  public Integer getContractTermWeekDuration() {
    return contractTermWeekDuration;
  }

  public void setContractTermWeekDuration(Integer contractTermWeekDuration) {
    this.contractTermWeekDuration = contractTermWeekDuration;
  }

  public Integer getContractTermMonthDuration() {
    return contractTermMonthDuration;
  }

  public void setContractTermMonthDuration(Integer contractTermMonthDuration) {
    this.contractTermMonthDuration = contractTermMonthDuration;
  }

  public Integer getContractTermYearDuration() {
    return contractTermYearDuration;
  }

  public void setContractTermYearDuration(Integer contractTermYearDuration) {
    this.contractTermYearDuration = contractTermYearDuration;
  }
}
