package uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailCapture;

@Entity
@Table(name = "collaboration_opportunities")
public class CollaborationOpportunity extends ProjectDetailEntity implements ContactDetailCapture {

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

  public CollaborationOpportunity() {
  }

  public CollaborationOpportunity(ProjectDetail projectDetail) {
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
}
