package uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract;

import com.google.common.annotations.VisibleForTesting;
import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailCapture;
import uk.co.ogauthority.pathfinder.service.entityduplication.ParentEntity;

@MappedSuperclass
public abstract class AwardedContractCommon
    extends ProjectDetailEntity implements ContactDetailCapture, ParentEntity {

  private String contractorName;

  @Enumerated(EnumType.STRING)
  private Function contractFunction;

  private String manualContractFunction;

  @Lob
  @Column(name = "description_of_work", columnDefinition = "CLOB")
  private String descriptionOfWork;

  private LocalDate dateAwarded;

  @Enumerated(EnumType.STRING)
  private ContractBand contractBand;

  private String contactName;

  private String phoneNumber;

  private String jobTitle;

  private String emailAddress;

  private Integer addedByOrganisationGroup;

  public AwardedContractCommon() {

  }

  public AwardedContractCommon(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  @VisibleForTesting
  public void setId(Integer id) {
    this.id = id;
  }

  public String getContractorName() {
    return contractorName;
  }

  public void setContractorName(String contractorName) {
    this.contractorName = contractorName;
  }

  public Function getContractFunction() {
    return contractFunction;
  }

  public void setContractFunction(Function contractFunction) {
    this.contractFunction = contractFunction;
  }

  public String getManualContractFunction() {
    return manualContractFunction;
  }

  public void setManualContractFunction(String manualContractFunction) {
    this.manualContractFunction = manualContractFunction;
  }

  public String getDescriptionOfWork() {
    return descriptionOfWork;
  }

  public void setDescriptionOfWork(String descriptionOfWork) {
    this.descriptionOfWork = descriptionOfWork;
  }

  public LocalDate getDateAwarded() {
    return dateAwarded;
  }

  public void setDateAwarded(LocalDate dateAwarded) {
    this.dateAwarded = dateAwarded;
  }

  public ContractBand getContractBand() {
    return contractBand;
  }

  public void setContractBand(ContractBand contractBand) {
    this.contractBand = contractBand;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @Override
  public String getName() {
    return getContactName();
  }

  @Override
  public String getPhoneNumber() {
    return phoneNumber;
  }

  @Override
  public String getJobTitle() {
    return jobTitle;
  }

  @Override
  public String getEmailAddress() {
    return emailAddress;
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
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    AwardedContractCommon that = (AwardedContractCommon) o;
    return Objects.equals(contractorName, that.contractorName)
        && contractFunction == that.contractFunction
        && Objects.equals(manualContractFunction, that.manualContractFunction)
        && Objects.equals(descriptionOfWork, that.descriptionOfWork)
        && Objects.equals(dateAwarded, that.dateAwarded)
        && contractBand == that.contractBand
        && Objects.equals(contactName, that.contactName)
        && Objects.equals(phoneNumber, that.phoneNumber)
        && Objects.equals(jobTitle, that.jobTitle)
        && Objects.equals(emailAddress, that.emailAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        contractorName,
        contractFunction,
        manualContractFunction,
        descriptionOfWork,
        dateAwarded,
        contractBand,
        contactName,
        phoneNumber,
        jobTitle,
        emailAddress
    );
  }
}
