package uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailCapture;

@Entity
@Table(name = "work_plan_upcoming_tenders")
public class ForwardWorkPlanUpcomingTender extends ProjectDetailEntity implements ContactDetailCapture {

  @Enumerated(EnumType.STRING)
  private Function departmentType;

  private String manualDepartmentType;

  @Lob
  @Column(name = "description_of_work", columnDefinition = "CLOB")
  private String descriptionOfWork;

  @Enumerated(EnumType.STRING)
  private Quarter estimatedTenderDateQuarter;

  private Integer estimatedTenderDateYear;

  private String contactName;

  private String phoneNumber;

  private String jobTitle;

  private String emailAddress;

  @Enumerated(EnumType.STRING)
  private ContractBand contractBand;

  private Integer contractTermDuration;

  @Enumerated(EnumType.STRING)
  private DurationPeriod contractTermDurationPeriod;

  private Integer addedByOrganisationGroup;

  public ForwardWorkPlanUpcomingTender() {
  }

  public ForwardWorkPlanUpcomingTender(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public Function getDepartmentType() {
    return departmentType;
  }

  public void setDepartmentType(Function departmentType) {
    this.departmentType = departmentType;
  }

  public String getManualDepartmentType() {
    return manualDepartmentType;
  }

  public void setManualDepartmentType(String manualDepartmentType) {
    this.manualDepartmentType = manualDepartmentType;
  }

  public String getDescriptionOfWork() {
    return descriptionOfWork;
  }

  public void setDescriptionOfWork(String descriptionOfWork) {
    this.descriptionOfWork = descriptionOfWork;
  }

  public Quarter getEstimatedTenderDateQuarter() {
    return estimatedTenderDateQuarter;
  }

  public void setEstimatedTenderDateQuarter(Quarter estimatedTenderDateQuarter) {
    this.estimatedTenderDateQuarter = estimatedTenderDateQuarter;
  }

  public Integer getEstimatedTenderDateYear() {
    return estimatedTenderDateYear;
  }

  public void setEstimatedTenderDateYear(Integer estimatedTenderDateYear) {
    this.estimatedTenderDateYear = estimatedTenderDateYear;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  @Override
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Override
  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  @Override
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @Override
  public String getName() {
    return getContactName();
  }

  public ContractBand getContractBand() {
    return contractBand;
  }

  public void setContractBand(ContractBand contractBand) {
    this.contractBand = contractBand;
  }

  public Integer getContractTermDuration() {
    return contractTermDuration;
  }

  public void setContractTermDuration(Integer contractTermDuration) {
    this.contractTermDuration = contractTermDuration;
  }

  public DurationPeriod getContractTermDurationPeriod() {
    return contractTermDurationPeriod;
  }

  public void setContractTermDurationPeriod(DurationPeriod contractTermDurationPeriod) {
    this.contractTermDurationPeriod = contractTermDurationPeriod;
  }

  public Integer getAddedByOrganisationGroup() {
    return addedByOrganisationGroup;
  }

  public void setAddedByOrganisationGroup(Integer addedByOrganisationGroup) {
    this.addedByOrganisationGroup = addedByOrganisationGroup;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ForwardWorkPlanUpcomingTender)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ForwardWorkPlanUpcomingTender that = (ForwardWorkPlanUpcomingTender) o;
    return departmentType == that.departmentType
        && Objects.equals(manualDepartmentType, that.manualDepartmentType)
        && Objects.equals(descriptionOfWork, that.descriptionOfWork)
        && estimatedTenderDateQuarter == that.estimatedTenderDateQuarter
        && Objects.equals(estimatedTenderDateYear, that.estimatedTenderDateYear)
        && Objects.equals(contactName, that.contactName)
        && Objects.equals(phoneNumber, that.phoneNumber)
        && Objects.equals(jobTitle, that.jobTitle)
        && Objects.equals(emailAddress, that.emailAddress)
        && contractBand == that.contractBand
        && Objects.equals(contractTermDuration, that.contractTermDuration)
        && contractTermDurationPeriod == that.contractTermDurationPeriod
        && Objects.equals(addedByOrganisationGroup, that.addedByOrganisationGroup);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        departmentType,
        manualDepartmentType,
        descriptionOfWork,
        estimatedTenderDateQuarter,
        estimatedTenderDateYear,
        contactName,
        phoneNumber,
        jobTitle,
        emailAddress,
        contractBand,
        contractTermDuration,
        contractTermDurationPeriod,
        addedByOrganisationGroup
    );
  }
}
