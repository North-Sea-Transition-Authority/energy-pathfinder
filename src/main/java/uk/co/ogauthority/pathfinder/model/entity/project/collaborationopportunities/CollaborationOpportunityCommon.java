package uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailCapture;
import uk.co.ogauthority.pathfinder.service.entityduplication.ParentEntity;
import uk.co.ogauthority.pathfinder.service.file.EntityWithLinkedFile;

@MappedSuperclass
public abstract class CollaborationOpportunityCommon
    extends ProjectDetailEntity implements ContactDetailCapture, EntityWithLinkedFile, ParentEntity {

  @Enumerated(EnumType.STRING)
  private Function function;

  private String manualFunction;

  @Lob
  @Column(name = "description_of_work", columnDefinition = "CLOB")
  private String descriptionOfWork;

  private Boolean urgentResponseNeeded;

  private String contactName;

  private String phoneNumber;

  private String jobTitle;

  private String emailAddress;

  public CollaborationOpportunityCommon() {
  }

  public CollaborationOpportunityCommon(ProjectDetail projectDetail) {
    this.projectDetail = projectDetail;
  }

  public Function getFunction() {
    return function;
  }

  public void setFunction(Function tenderFunction) {
    this.function = tenderFunction;
  }

  public String getManualFunction() {
    return manualFunction;
  }

  public void setManualFunction(String manualTenderFunction) {
    this.manualFunction = manualTenderFunction;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CollaborationOpportunityCommon)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    CollaborationOpportunityCommon that = (CollaborationOpportunityCommon) o;
    return function == that.function
        && Objects.equals(manualFunction, that.manualFunction)
        && Objects.equals(descriptionOfWork, that.descriptionOfWork)
        && Objects.equals(urgentResponseNeeded, that.urgentResponseNeeded)
        && Objects.equals(contactName, that.contactName)
        && Objects.equals(phoneNumber, that.phoneNumber)
        && Objects.equals(jobTitle, that.jobTitle)
        && Objects.equals(emailAddress, that.emailAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        function,
        manualFunction,
        descriptionOfWork,
        urgentResponseNeeded,
        contactName,
        phoneNumber,
        jobTitle,
        emailAddress
    );
  }
}
