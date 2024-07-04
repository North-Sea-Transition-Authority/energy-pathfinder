package uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailCapture;
import uk.co.ogauthority.pathfinder.service.entityduplication.ParentEntity;
import uk.co.ogauthority.pathfinder.service.file.EntityWithLinkedFile;

@Entity
@Table(name = "upcoming_tenders")
public class UpcomingTender
    extends ProjectDetailEntity implements ContactDetailCapture, EntityWithLinkedFile, ParentEntity {

  @Enumerated(EnumType.STRING)
  private Function tenderFunction;

  private String manualTenderFunction;

  @Lob
  @Column(name = "description_of_work", columnDefinition = "CLOB")
  private String descriptionOfWork;

  private LocalDate estimatedTenderDate;

  @Enumerated(EnumType.STRING)
  private ContractBand contractBand;

  private String contactName;

  private String phoneNumber;

  private String jobTitle;

  private String emailAddress;

  private Integer addedByOrganisationGroup;

  public UpcomingTender() {
  }

  public UpcomingTender(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public Function getTenderFunction() {
    return tenderFunction;
  }

  public void setTenderFunction(Function tenderFunction) {
    this.tenderFunction = tenderFunction;
  }

  public String getManualTenderFunction() {
    return manualTenderFunction;
  }

  public void setManualTenderFunction(String manualTenderFunction) {
    this.manualTenderFunction = manualTenderFunction;
  }

  public String getDescriptionOfWork() {
    return descriptionOfWork;
  }

  public void setDescriptionOfWork(String descriptionOfWork) {
    this.descriptionOfWork = descriptionOfWork;
  }

  public LocalDate getEstimatedTenderDate() {
    return estimatedTenderDate;
  }

  public void setEstimatedTenderDate(LocalDate estimatedTenderDate) {
    this.estimatedTenderDate = estimatedTenderDate;
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

  public Integer getAddedByOrganisationGroup() {
    return addedByOrganisationGroup;
  }

  public void setAddedByOrganisationGroup(
      Integer portalOrganisationGroupId) {
    this.addedByOrganisationGroup = portalOrganisationGroupId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UpcomingTender)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    UpcomingTender that = (UpcomingTender) o;
    return tenderFunction == that.tenderFunction
        && Objects.equals(manualTenderFunction, that.manualTenderFunction)
        && Objects.equals(descriptionOfWork, that.descriptionOfWork)
        && Objects.equals(estimatedTenderDate, that.estimatedTenderDate)
        && contractBand == that.contractBand
        && Objects.equals(contactName, that.contactName)
        && Objects.equals(phoneNumber, that.phoneNumber)
        && Objects.equals(jobTitle, that.jobTitle)
        && Objects.equals(emailAddress, that.emailAddress)
        && Objects.equals(addedByOrganisationGroup, that.addedByOrganisationGroup);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        tenderFunction,
        manualTenderFunction,
        descriptionOfWork,
        estimatedTenderDate,
        contractBand,
        contactName,
        phoneNumber,
        jobTitle,
        emailAddress,
        addedByOrganisationGroup
    );
  }
}
